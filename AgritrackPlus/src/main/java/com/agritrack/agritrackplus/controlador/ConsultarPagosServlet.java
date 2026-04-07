package com.agritrack.agritrackplus.controlador; // Paquete que contiene la clase del controlador para consultar pagos

import com.agritrack.agritrackplus.DAO.PagoDAO; // Importación de la clase PagoDAO para interactuar con la base de datos de pagos
import java.io.IOException; // Importación de la excepción para manejar errores de entrada/salida
import java.io.PrintWriter; // Importación de la clase PrintWriter para escribir respuestas en el cuerpo de la respuesta HTTP
import java.util.List; // Importación de la clase List para manejar listas de objetos
import java.util.Map; // Importación de la clase Map para trabajar con pares clave-valor
import jakarta.servlet.ServletException; // Importación de la excepción para manejar errores en servlets
import jakarta.servlet.annotation.WebServlet; // Importación de la anotación para definir un servlet
import jakarta.servlet.http.HttpServlet; // Importación de la clase base para servlets HTTP
import jakarta.servlet.http.HttpServletRequest; // Importación para manejar solicitudes HTTP
import jakarta.servlet.http.HttpServletResponse; // Importación para manejar respuestas HTTP

/*
Este servlet se encarga de consultar las tareas de un trabajador específico a través de una solicitud GET.
Al recibir la solicitud, configura la respuesta para que sea en formato JSON. 
Luego, obtiene un parámetro de búsqueda que puede ser el nombre o documento del trabajador.
Si el parámetro es nulo o vacío, devuelve un array vacío. Si hay un criterio válido, utiliza el PagoDAO
para buscar las tareas correspondientes y construye un JSON con los resultados. Finalmente, envía el 
JSON como respuesta al cliente. En caso de errores, captura la excepción y también devuelve un array 
vacío para evitar romper el código JavaScript en el cliente.
*/
@WebServlet("/ConsultarPagosServlet") // Define el servlet con una URL que lo identifica
public class ConsultarPagosServlet extends HttpServlet { // Clase que extiende HttpServlet para manejar solicitudes HTTP

    /**
     * Procesa las peticiones GET para buscar tareas de un trabajador específico.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) // Método que maneja solicitudes GET
            throws ServletException, IOException {
        
        // 1. Configurar la respuesta como JSON y con codificación UTF-8
        response.setContentType("application/json"); // Establece el tipo de contenido de la respuesta como JSON
        response.setCharacterEncoding("UTF-8"); // Establece la codificación de caracteres de la respuesta a UTF-8
        
        // 2. Obtener el parámetro de búsqueda (Nombre o Documento)
        String criterio = request.getParameter("criterio"); // Captura el parámetro 'criterio' enviado en la solicitud
        PrintWriter out = response.getWriter(); // Crea un PrintWriter para enviar texto como respuesta al cliente
        
        // 3. Validar que el criterio no sea nulo o vacío
        if (criterio == null || criterio.trim().isEmpty()) { // Verifica si el criterio es nulo o solo contiene espacios
            out.print("[]"); // Si no hay criterio, devuelve un array vacío en formato JSON
            return; // Termina la ejecución del método
        }

        try {
            // 4. Consultar al DAO (Asegúrate de tener el PagoDAO que creamos antes)
            PagoDAO dao = new PagoDAO(); // Crea una instancia de PagoDAO para interactuar con la base de datos
            List<Map<String, String>> tareas = dao.buscarTareasPorTrabajador(criterio); // Llama al método buscarTareasPorTrabajador para obtener las tareas asociadas al criterio

            // 5. Construir el JSON manualmente (Estructura: [{id, tarea, jornada, estado}, ...])
            StringBuilder json = new StringBuilder(); // Crea un StringBuilder para construir la respuesta JSON
            json.append("["); // Comienza el array JSON
            
            for (int i = 0; i < tareas.size(); i++) { // Itera sobre la lista de tareas obtenidas
                Map<String, String> t = tareas.get(i); // Obtiene cada tarea como un mapa de clave-valor
                json.append("{"); // Comienza un objeto JSON
                json.append("\"id\":\"").append(t.get("id")).append("\","); // Añade el ID de la tarea al objeto JSON
                json.append("\"tarea\":\"").append(t.get("tarea").replace("\"", "\\\"")).append("\","); // Añade el nombre de la tarea, escapando comillas dobles
                json.append("\"jornada\":\"").append(t.get("jornada")).append("\","); // Añade la jornada de la tarea al objeto JSON
                json.append("\"estado\":\"").append(t.get("estado")).append("\""); // Añade el estado de la tarea al objeto JSON
                json.append("}"); // Cierra el objeto JSON
                
                if (i < tareas.size() - 1) { // Si no es el último elemento de la lista
                    json.append(","); // Añade una coma para separar los objetos en el array JSON
                }
            }
            
            json.append("]"); // Cierra el array JSON

            // 6. Enviar la respuesta al cliente
            out.print(json.toString()); // Envía el JSON construido como respuesta al cliente
            
        } catch (Exception e) { // Captura cualquier excepción que ocurra durante el proceso
            e.printStackTrace(); // Imprime la traza de la excepción en la consola para depuración
            // En caso de error crítico, devolvemos un array vacío para no romper el JS
            out.print("[]"); // Envía un array vacío en formato JSON para evitar errores en el lado del cliente
        } finally {
            out.close(); // Cierra el PrintWriter para liberar recursos
        }
    }
}