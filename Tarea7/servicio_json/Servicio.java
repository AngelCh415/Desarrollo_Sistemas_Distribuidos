/*
  Servicio.java
  Servicio web tipo REST
  Recibe parámetros utilizando JSON
  Carlos Pineda Guerrero, abril 2023
*/

package servicio_json;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.QueryParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Response;
import java.lang.reflect.Type;
import java.util.Base64;

import java.sql.*;
import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import com.google.gson.*;

// la URL del servicio web es http://localhost:8080/Servicio/rest/ws
// donde:
//	"Servicio" es el dominio del servicio web (es decir, el nombre de archivo Servicio.war)
//	"rest" se define en la etiqueta <url-pattern> de <servlet-mapping> en el archivo WEB-INF\web.xml
//	"ws" se define en la siguiente anotación @Path de la clase Servicio

@Path("ws")
public class Servicio
{
  static DataSource pool = null;
  static
  {		
    try
    {
      Context ctx = new InitialContext();
      pool = (DataSource)ctx.lookup("java:comp/env/jdbc/datasource_Servicio");
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  static Gson j = new GsonBuilder().registerTypeAdapter(byte[].class,new AdaptadorGsonBase64()).setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();

  // Estructura de la tabla "articulos":
  //   - id_articulo (PK, Auto-incremental)
  //   - nombre
  //   - descripcion
  //   - precio
  //   - cantidad_almacen
  //   - foto

  @POST
  @Path("alta_articulo")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response altaArticulo(String json) throws Exception
  {
    ParamAltaArticulo p = (ParamAltaArticulo) j.fromJson(json, ParamAltaArticulo.class);
    Articulo articulo = p.articulo;

    Connection conexion = pool.getConnection();

    if (articulo.nombre == null || articulo.nombre.equals(""))
      return Response.status(400).entity(j.toJson(new Error("Se debe ingresar el nombre del artículo"))).build();

    if (articulo.descripcion == null || articulo.descripcion.equals(""))
      return Response.status(400).entity(j.toJson(new Error("Se debe ingresar la descripción del artículo"))).build();

    if (articulo.precio <= 0)
      return Response.status(400).entity(j.toJson(new Error("El precio del artículo debe ser mayor a cero"))).build();

    if (articulo.cantidadAlmacen < 0)
      return Response.status(400).entity(j.toJson(new Error("La cantidad en almacén debe ser igual o mayor a cero"))).build();

    try
    {
      conexion.setAutoCommit(false);

      PreparedStatement stmt = conexion.prepareStatement("INSERT INTO articulos(nombre, descripcion, precio, cantidad_almacen, foto) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

      try
      {
        stmt.setString(1, articulo.nombre);
        stmt.setString(2, articulo.descripcion);
        stmt.setDouble(3, articulo.precio);
        stmt.setInt(4, articulo.cantidadAlmacen);
        stmt.setBytes(5, articulo.foto);

        stmt.executeUpdate();

        ResultSet rs = stmt.getGeneratedKeys();
        rs.next();

        int idArticulo = rs.getInt(1);
        articulo.idArticulo = idArticulo;

        conexion.commit();

        return Response.status(200).entity(j.toJson(new RespuestaAltaArticulo(articulo))).build();
      }
      finally
      {
        stmt.close();
      }
    }
    catch (Exception e)
    {
      conexion.rollback();
      throw e;
    }
    finally
    {
      conexion.close();
    }
  }

  @GET
  @Path("consulta_articulos")
  @Produces(MediaType.APPLICATION_JSON)
  public Response consultaArticulos() throws Exception
  {
    Connection conexion = pool.getConnection();

    try
    {
      Statement stmt = conexion.createStatement();

      try
      {
        ResultSet rs = stmt.executeQuery("SELECT * FROM articulos");

        ArrayList<Articulo> articulos = new ArrayList<>();

        while (rs.next())
        {
          Articulo articulo = new Articulo();

          articulo.idArticulo = rs.getInt("id_articulo");
          articulo.nombre = rs.getString("nombre");
          articulo.descripcion = rs.getString("descripcion");
          articulo.precio = rs.getDouble("precio");
          articulo.cantidadAlmacen = rs.getInt("cantidad_almacen");
          articulo.foto = rs.getBytes("foto");

          articulos.add(articulo);
        }

        return Response.status(200).entity(j.toJson(new RespuestaConsultaArticulos(articulos))).build();
      }
      finally
      {
        stmt.close();
      }
    }
    finally
    {
      conexion.close();
    }
  }

  @POST
  @Path("modificacion_articulo")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response modificacionArticulo(String json) throws Exception
  {
    ParamModificacionArticulo p = (ParamModificacionArticulo) j.fromJson(json, ParamModificacionArticulo.class);
    Articulo articulo = p.articulo;

    Connection conexion = pool.getConnection();

    if (articulo.idArticulo <= 0)
      return Response.status(400).entity(j.toJson(new Error("Se debe proporcionar el ID del artículo a modificar"))).build();

    if (articulo.nombre == null || articulo.nombre.equals(""))
      return Response.status(400).entity(j.toJson(new Error("Se debe ingresar el nombre del artículo"))).build();

    if (articulo.descripcion == null || articulo.descripcion.equals(""))
      return Response.status(400).entity(j.toJson(new Error("Se debe ingresar la descripción del artículo"))).build();

    if (articulo.precio <= 0)
      return Response.status(400).entity(j.toJson(new Error("El precio del artículo debe ser mayor a cero"))).build();

    if (articulo.cantidadAlmacen < 0)
      return Response.status(400).entity(j.toJson(new Error("La cantidad en almacén debe ser igual o mayor a cero"))).build();

    try
    {
      conexion.setAutoCommit(false);

      PreparedStatement stmt = conexion.prepareStatement("UPDATE articulos SET nombre = ?, descripcion = ?, precio = ?, cantidad_almacen = ?, foto = ? WHERE id_articulo = ?");

      try
      {
        stmt.setString(1, articulo.nombre);
        stmt.setString(2, articulo.descripcion);
        stmt.setDouble(3, articulo.precio);
        stmt.setInt(4, articulo.cantidadAlmacen);
        stmt.setBytes(5, articulo.foto);
        stmt.setInt(6, articulo.idArticulo);

        int numFilas = stmt.executeUpdate();

        if (numFilas == 0)
          return Response.status(400).entity(j.toJson(new Error("El ID del artículo no existe"))).build();

        conexion.commit();

        return Response.status(200).entity(j.toJson(new RespuestaModificacionArticulo())).build();
      }
      finally
      {
        stmt.close();
      }
    }
    catch (Exception e)
    {
      conexion.rollback();
      throw e;
    }
    finally
    {
      conexion.close();
    }
  }

  @POST
  @Path("eliminacion_articulo")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response eliminacionArticulo(String json) throws Exception
  {
    ParamEliminacionArticulo p = (ParamEliminacionArticulo) j.fromJson(json, ParamEliminacionArticulo.class);
    int idArticulo = p.idArticulo;

    Connection conexion = pool.getConnection();

    if (idArticulo <= 0)
      return Response.status(400).entity(j.toJson(new Error("Se debe proporcionar el ID del artículo a eliminar"))).build();

    try
    {
      conexion.setAutoCommit(false);

      PreparedStatement stmt = conexion.prepareStatement("DELETE FROM articulos WHERE id_articulo = ?");

      try
      {
        stmt.setInt(1, idArticulo);

        int numFilas = stmt.executeUpdate();

        if (numFilas == 0)
          return Response.status(400).entity(j.toJson(new Error("El ID del artículo no existe"))).build();

        conexion.commit();

        return Response.status(200).entity(j.toJson(new RespuestaEliminacionArticulo())).build();
      }
      finally
      {
        stmt.close();
      }
    }
    catch (Exception e)
    {
      conexion.rollback();
      throw e;
    }
    finally
    {
      conexion.close();
    }
  }
}

class ParamAltaArticulo
{
  public Articulo articulo;
}

class RespuestaAltaArticulo
{
  public Articulo articulo;

  public RespuestaAltaArticulo(Articulo articulo)
  {
    this.articulo = articulo;
  }
}

class ParamModificacionArticulo
{
  public Articulo articulo;
}

class RespuestaModificacionArticulo
{
}

class ParamEliminacionArticulo
{
  public int idArticulo;
}

class RespuestaEliminacionArticulo
{
}

class RespuestaConsultaArticulos
{
  public ArrayList<Articulo> articulos;

  public RespuestaConsultaArticulos(ArrayList<Articulo> articulos)
  {
    this.articulos = articulos;
  }
}

class Articulo
{
  public int idArticulo;
  public String nombre;
  public String descripcion;
  public double precio;
  public int cantidadAlmacen;
  public byte[] foto;
}

class Error
{
  public String mensaje;

  public Error(String mensaje)
  {
    this.mensaje = mensaje;
  }
}

class AdaptadorGsonBase64 implements JsonSerializer<byte[]>, JsonDeserializer<byte[]>
{
  public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
  {
    String base64 = json.getAsString();
    return Base64.getDecoder().decode(base64);
  }

  public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context)
  {
    String base64 = Base64.getEncoder().encodeToString(src);
    return new JsonPrimitive(base64);
  }
}
