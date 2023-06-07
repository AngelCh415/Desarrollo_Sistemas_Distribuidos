// Carlos Pineda Guerrero. 2021-2023

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

namespace FunctionApp1
{
    public static class borra_usuario
    {
        class ParamBorraUsuario
        {
            public string email;
        }
        class Error
        {
            public string mensaje;
            public Error(string mensaje)
            {
                this.mensaje = mensaje;
            }
        }
        [FunctionName("borra_usuario")]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Anonymous, "post")] HttpRequest req,
            ILogger log)
        {
            try
            {
                string requestBody = await new StreamReader(req.Body).ReadToEndAsync();
                ParamBorraUsuario data = JsonConvert.DeserializeObject<ParamBorraUsuario>(requestBody);
                string email = data.email;

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
                    var cmd_2 = new MySqlCommand();
                    cmd_2.Connection = conexion;
                    cmd_2.Transaction = transaccion;
                    cmd_2.CommandText = "DELETE FROM fotos_usuarios WHERE id_usuario=(SELECT id_usuario FROM usuarios WHERE email=@email)";
                    cmd_2.Parameters.AddWithValue("@email", email);
                    cmd_2.ExecuteNonQuery();

                    var cmd_3 = new MySqlCommand();
                    cmd_3.Connection = conexion;
                    cmd_3.Transaction = transaccion;
                    cmd_3.CommandText = "DELETE FROM usuarios WHERE email=@email";
                    cmd_3.Parameters.AddWithValue("@email", email);
                    cmd_3.ExecuteNonQuery();

                    transaccion.Commit();
                    return new OkObjectResult("Usuario borrado");
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