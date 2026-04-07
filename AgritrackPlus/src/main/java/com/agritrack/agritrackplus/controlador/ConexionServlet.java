package com.agritrack.agritrackplus.controlador; // Paquete que contiene la clase del controlador para manejar la conexión a la base de datos

import com.agritrack.agritrackplus.db.Conexion; // Importación de la clase Conexion para establecer conexiones con la base de datos
import jakarta.servlet.ServletException; // Importación de la excepción para manejar errores en servlets
import jakarta.servlet.annotation.WebServlet; // Importación de la anotación para definir un servlet
import jakarta.servlet.http.HttpServlet; // Importación de la clase base para servlets HTTP
import jakarta.servlet.http.HttpServletRequest; // Importación para manejar solicitudes HTTP
import jakarta.servlet.http.HttpServletResponse; // Importación para manejar respuestas HTTP
import java.io.IOException; // Importación de la excepción para manejar errores de entrada/salida
import java.io.PrintWriter; // Importación de la clase PrintWriter para escribir respuestas en el cuerpo de la respuesta HTTP
import java.sql.Connection; // Importación de la clase Connection para manejar conexiones a la base de datos

/*
Este servlet se encarga de probar la conexión a la base de datos. 
Cuando recibe una solicitud GET, establece el tipo de contenido de la respuesta como HTML. 
Luego, intenta obtener una conexión a la base de datos utilizando la clase Conexion. 
Si la conexión es exitosa y está abierta, envía un mensaje de éxito al cliente; 
de lo contrario, envía un mensaje de error. Si ocurre una excepción durante el proceso de conexión, 
captura la excepción y envía un mensaje de error
*/
@WebServlet(name = "ConexionServlet", urlPatterns = {"/ConexionServlet"}) // Define el servlet con un nombre y una URL
public class ConexionServlet extends HttpServlet { // Clase que extiende HttpServlet para manejar solicitudes HTTP

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) // Método que maneja solicitudes GET
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8"); // Establece el tipo de contenido de la respuesta como HTML con codificación UTF-8
        try (PrintWriter out = response.getWriter()) { // Crea un PrintWriter para enviar texto como respuesta al cliente
            try (Connection conn = Conexion.getConnection()) { // Intenta obtener una conexión a la base de datos usando el método estático getConnection
                // Verifica si la conexión fue exitosa y no está cerrada
                if (conn != null && !conn.isClosed()) {
                    out.println("<h1 style='color:green'>¡Conexión Exitosa rosel!</h1>"); // Envía un mensaje de éxito al cliente en color verde
                } else {
                    out.println("<h1 style='color:red'>Error de conexión</h1>"); // Envía un mensaje de error al cliente en color rojo si la conexión falló
                }
            } catch (Exception e) { // Captura cualquier excepción que ocurra al intentar obtener la conexión
                out.println("<h1 style='color:red'>Error: " + e.getMessage() + "</h1>"); // Envía un mensaje de error al cliente con la descripción de la excepción
            }
        }
    }
}