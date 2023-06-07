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
    public static class modifica_usuario
    {
        class Usuario
        {
            public string email;
            public string nombre;
            public string apellido_paterno;
            public string apellido_materno;
            public DateTime? fecha_nacimiento;
            public long? telefono;
            public string genero;
            public string foto; // foto en base 64
        }
        class ParamModificaUsuario
        {
            public Usuario usuario;
        }
        class Error
        {
            public string mensaje;
            public Error(string mensaje)
            {
                this.mensaje = mensaje;
            }
        }
        [FunctionName("modifica_usuario")]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Anonymous, "post")] HttpRequest req,
            ILogger log)
        {
            try
            {
                string requestBody = await new StreamReader(req.Body).ReadToEndAsync();
                ParamModificaUsuario data = JsonConvert.DeserializeObject<ParamModificaUsuario>(requestBody);
                Usuario usuario = data.usuario;

                if (usuario.email == null || usuario.email == "") throw new Exception("Se debe ingresar el email");
                if (usuario.nombre == null || usuario.nombre == "") throw new Exception("Se debe ingresar el nombre");
                if (usuario.apellido_paterno == null || usuario.apellido_paterno == "") throw new Exception("Se debe ingresar el apellido_paterno");
                if (usuario.fecha_nacimiento == null) throw new Exception("Se debe ingresar la fecha de nacimiento");

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
                    cmd_2.CommandText = "UPDATE usuarios SET nombre=@nombre,apellido_paterno=@apellido_paterno,apellido_materno=@apellido_materno,fecha_nacimiento=@fecha_nacimiento,telefono=@telefono,genero=@genero WHERE email=@email";
                    cmd_2.Parameters.AddWithValue("@nombre", usuario.nombre);
                    cmd_2.Parameters.AddWithValue("@apellido_paterno", usuario.apellido_paterno);
                    cmd_2.Parameters.AddWithValue("@apellido_materno", usuario.apellido_materno != null ? usuario.apellido_materno : DBNull.Value);
                    cmd_2.Parameters.AddWithValue("@fecha_nacimiento", usuario.fecha_nacimiento);
                    cmd_2.Parameters.AddWithValue("@telefono", usuario.telefono != null ? usuario.telefono : DBNull.Value);
                    cmd_2.Parameters.AddWithValue("@genero", usuario.genero != null ? usuario.genero  : DBNull.Value);
                    cmd_2.Parameters.AddWithValue("@email", usuario.email);
                    cmd_2.ExecuteNonQuery();

                    var cmd_3 = new MySqlCommand();
                    cmd_3.Connection = conexion;
                    cmd_3.Transaction = transaccion;
                    cmd_3.CommandText = "DELETE FROM fotos_usuarios WHERE id_usuario=(SELECT id_usuario FROM usuarios WHERE email=@email)";
                    cmd_3.Parameters.AddWithValue("@email", usuario.email);
                    cmd_3.ExecuteNonQuery();

                    if (usuario.foto != null)
                    {
                        var cmd_4 = new MySqlCommand();
                        cmd_4.Connection = conexion;
                        cmd_4.Transaction = transaccion;
                        cmd_4.CommandText = "INSERT INTO fotos_usuarios (foto,id_usuario) VALUES (@foto,(SELECT id_usuario FROM usuarios WHERE email=@email))";
                        cmd_4.Parameters.AddWithValue("@foto", Convert.FromBase64String(usuario.foto));
                        cmd_4.Parameters.AddWithValue("@email", usuario.email);
                        cmd_4.ExecuteNonQuery();
                    }

                    transaccion.Commit();
                    return new OkObjectResult("Usuario modificado");
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
