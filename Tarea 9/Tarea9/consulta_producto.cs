using System;
using System.IO;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Azure.WebJobs;
using Microsoft.Azure.WebJobs.Extensions.Http;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;

using MySql.Data.MySqlClient;

namespace Tarea9
{
    public static class consulta_producto
    {
        class ProductoFoto
        {
            public int id_producto;
            public string nombre;
            public string descripcion;
            public decimal precio;
            public string foto;  // foto en base 64
        }

        class ParamConsultaProducto
        {
            public string nombre;
            public string descripcion;
        }

        class Error
        {
            public string mensaje;
            public Error(string mensaje)
            {
                this.mensaje = mensaje;
            }
        }

        [FunctionName("consulta_producto")]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Anonymous, "post")] HttpRequest req,
            ILogger log)
        {
            try
            {
                string requestBody = await new StreamReader(req.Body).ReadToEndAsync();
                ParamConsultaProducto data = JsonConvert.DeserializeObject<ParamConsultaProducto>(requestBody);
                string nombre = data.nombre;
                string descripcion = data.descripcion;

                string Server = Environment.GetEnvironmentVariable("Server");
                string UserID = Environment.GetEnvironmentVariable("UserID");
                string Password = Environment.GetEnvironmentVariable("Password");
                string Database = Environment.GetEnvironmentVariable("Database");

                string cs = "Server=" + Server + ";UserID=" + UserID + ";Password=" + Password + ";" + "Database=" + Database + ";SslMode=Preferred;";
                var conexion = new MySqlConnection(cs);
                conexion.Open();

                try
                {
                    var cmd = new MySqlCommand("SELECT id, nombre, descripcion, precio, fotografia, length(fotografia) " +
                                                "FROM articulos " +
                                                "WHERE nombre LIKE @nombre OR descripcion LIKE @descripcion");
                    cmd.Connection = conexion;
                    cmd.Parameters.AddWithValue("@nombre", "%" + nombre + "%");
                    cmd.Parameters.AddWithValue("@descripcion", "%" + descripcion + "%");
                    MySqlDataReader r = cmd.ExecuteReader();

                    try
                    {
                        if (!r.Read())
                            throw new Exception("No se encontraron productos con los criterios de búsqueda proporcionados");

                        var productoFoto = new ProductoFoto();
                        productoFoto.id_producto = r.GetInt32(0);
                        productoFoto.nombre = r.GetString(1);
                        productoFoto.descripcion = r.GetString(2);
                        productoFoto.precio = r.GetDecimal(3);

                        if (!r.IsDBNull(4))
                        {
                            var longitud = r.GetInt32(5);
                            byte[] foto = new byte[longitud];
                            r.GetBytes(4, 0, foto, 0, longitud);
                            productoFoto.foto = Convert.ToBase64String(foto);
                        }

                        return new ContentResult { Content = JsonConvert.SerializeObject(productoFoto), ContentType = "application/json" };
                    }
                    finally
                    {
                        r.Close();
                    }
                }
                finally
                {
                    conexion.Close();
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
                return new BadRequestObjectResult(JsonConvert.SerializeObject(new Error(e.Message)));
            }
        }
    }
}
