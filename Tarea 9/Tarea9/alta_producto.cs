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
    public static class alta_producto
    {
        class Producto
        {
            public string nombre;
            public string descripcion;
            public decimal precio;
            public int cantidad_almacen;
            public string foto; // foto en base 64
        }
        class ParamAltaProducto
        {
            public Producto producto;
        }
        class Error
        {
            public string mensaje;
            public Error(string mensaje)
            {
                this.mensaje = mensaje;
            }
        }
        [FunctionName("alta_producto")]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Anonymous, "post")] HttpRequest req,
            ILogger log)
        {
            try
            {
                string requestBody = await new StreamReader(req.Body).ReadToEndAsync();
                ParamAltaProducto data = JsonConvert.DeserializeObject<ParamAltaProducto>(requestBody);
                Producto producto = data.producto;

                if (producto.nombre == null || producto.nombre == "") throw new Exception("Se debe ingresar el nombre");
                if (producto.descripcion == null || producto.descripcion == "") throw new Exception("Se debe ingresar la descripción");
                if (producto.precio <= 0) throw new Exception("El precio debe ser mayor a cero");
                if (producto.cantidad_almacen <= 0) throw new Exception("La cantidad en almacén debe ser mayor a cero");

                string Server = Environment.GetEnvironmentVariable("Server");
                string UserID = Environment.GetEnvironmentVariable("UserID");
                string Password = Environment.GetEnvironmentVariable("Password");
                string Database = Environment.GetEnvironmentVariable("Database");

                string cs = "Server=" + Server + ";UserID=" + UserID + ";Password=" + Password + ";" + "Database=" + Database + ";SslMode=Preferred;";
                var conexion = new MySqlConnection(cs);
                conexion.Open();

                MySqlTransaction transaccion = conexion.BeginTransaction();

                try
                {
                    var cmd_1 = new MySqlCommand();
                    cmd_1.Connection = conexion;
                    cmd_1.Transaction = transaccion;
                    cmd_1.CommandText = "INSERT INTO articulos(nombre, descripcion, precio, cantidad_almacen, fotografia) VALUES (@nombre, @descripcion, @precio, @cantidad_almacen, @foto)";
                    cmd_1.Parameters.AddWithValue("@nombre", producto.nombre);
                    cmd_1.Parameters.AddWithValue("@descripcion", producto.descripcion);
                    cmd_1.Parameters.AddWithValue("@precio", producto.precio);
                    cmd_1.Parameters.AddWithValue("@cantidad_almacen", producto.cantidad_almacen);
                    cmd_1.Parameters.AddWithValue("@foto", Convert.FromBase64String(producto.foto));
                    cmd_1.ExecuteNonQuery();

                    transaccion.Commit();
                    return new OkObjectResult("Producto insertado");
                }
                catch (Exception e)
                {
                    transaccion.Rollback();
                    throw new Exception(e.Message);
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
