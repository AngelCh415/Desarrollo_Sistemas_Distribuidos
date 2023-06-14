using System;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Azure.WebJobs;
using Microsoft.Azure.WebJobs.Extensions.Http;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging;

using System.IO;
using Newtonsoft.Json;

namespace Tarea9
{
    public static class Get
    {
        [FunctionName("Get")]
        [ResponseCache(Duration = 86400, NoStore = false)]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Anonymous, "get")] HttpRequest req,
            ILogger log)
        {
            try
            {
                string path = (string)req.Query["nombre"];

                // el parámetro "descargar" es opcional, por default es "NO"
                bool descargar = ((string)req.Query["descargar"] ?? "NO").ToUpper() == "SI";
                // la variable de entorno HOME está predefinida en el servidor (C:\home o D:\home)
                // Los archivos cargados se pueden ver en el portal, seleccionando la aplicación de funciones y 
                // la opción "Consola" en la sección "Herramientas de desarrollo"
                string home = Environment.GetEnvironmentVariable("HOME");

                try
                {
                    byte[] contenido = File.ReadAllBytes(home + "/data" + path);
                    string nombre = Path.GetFileName(path);
                    string tipo_mime = MimeMapping.GetMimeMapping(path);
                    DateTime fecha_modificacion = File.GetLastWriteTime(home + "/data" + path);

                    if (descargar)
                        // el navegador descargará el contenido como archivo
                        return new FileContentResult(contenido, tipo_mime) { FileDownloadName = nombre };
                    else
                        // el navegador mostrará el contenido
                        // se envia al navegador el tipo mime para que lo procese adecuadamente
                        // se envia al navegador la fecha de modificación del archivo para usar HTTP caching
                        return new FileContentResult(contenido, tipo_mime) { LastModified = fecha_modificacion };
                }
                catch (FileNotFoundException)
                {
                    return new NotFoundResult();
                }
            }
            catch (Exception e)
            {
                return new BadRequestObjectResult(e.Message);
            }
        }
    }
} 