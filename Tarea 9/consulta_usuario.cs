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
    public static class consulta_usuario
    {
        class UsuarioFoto
        {
            public int id_usuario;
            public string email;
            public string nombre;
            public string apellido_paterno;
            public string apellido_materno;
            public DateTime fecha_nacimiento;
            public long? telefono;
            public string genero;
            public string foto;  // foto en base 64
        }
        class ParamConsultaUsuario
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

        [FunctionName("consulta_usuario")]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Anonymous, "post")] HttpRequest req,
            ILogger log)
        {
            try
            {
                string requestBody = await new StreamReader(req.Body).ReadToEndAsync();
                ParamConsultaUsuario data = JsonConvert.DeserializeObject<ParamConsultaUsuario>(requestBody);
                string email = data.email;

                string Server = Environment.GetEnvironmentVariable("Server");
                string UserID = Environment.GetEnvironmentVariable("UserID");
                string Password = Environment.GetEnvironmentVariable("Password");
                string Database = Environment.GetEnvironmentVariable("Database");

                string cs = "Server=" + Server + ";UserID=" + UserID + ";Password=" + Password + ";" + "Database=" + Database + ";SslMode=Preferred;";
                var conexion = new MySqlConnection(cs);
                conexion.Open();

                try
                {
                    var cmd = new MySqlCommand("SELECT a.id_usuario,a.email,a.nombre," +
                                                "a.apellido_paterno,a.apellido_materno," +
                                                "a.fecha_nacimiento,a.telefono,a.genero," +
                                                "b.foto,length(b.foto) " +
                                                "FROM usuarios a LEFT OUTER JOIN fotos_usuarios b ON a.id_usuario=b.id_usuario " +
                                                "WHERE a.email=@email");
                    cmd.Connection = conexion;
                    cmd.Parameters.AddWithValue("@email", email);
                    MySqlDataReader r = cmd.ExecuteReader();

                    try
                    {
                        if (!r.Read())
                            throw new Exception("El email no existe");

                        var usuario_foto = new UsuarioFoto();
                        usuario_foto.id_usuario = r.GetInt32(0);
                        usuario_foto.email = r.GetString(1);
                        usuario_foto.nombre = r.GetString(2);
                        usuario_foto.apellido_paterno = r.GetString(3);
                        usuario_foto.apellido_materno = !r.IsDBNull(4) ? r.GetString(4) : null;
                        usuario_foto.fecha_nacimiento = r.GetDateTime(5);
                        usuario_foto.telefono = !r.IsDBNull(6) ? r.GetInt64(6) : null;
                        usuario_foto.genero = !r.IsDBNull(7) ? r.GetString(7) : null;

                        if (!r.IsDBNull(8))
                        {
                            var longitud = r.GetInt32(9);
                            byte[] foto = new byte[longitud];
                            r.GetBytes(8, 0, foto, 0, longitud);
                            usuario_foto.foto = Convert.ToBase64String(foto);
                        }

                        // Notar que el formato de fecha es compatible con <input> de HTML con tipo "datetime-local"
                        return new ContentResult { Content = JsonConvert.SerializeObject(usuario_foto), ContentType = "application/json" };
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
