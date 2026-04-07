package com.agritrack.agritrackplus.controlador; // Paquete que contiene la clase del controlador del servlet para la gestión de la sesión de usuario

import java.io.IOException; // Importa la clase IOException para manejar errores de entrada/salida
import jakarta.servlet.ServletException; // Importa la clase ServletException para manejar errores específicos de servlets
import jakarta.servlet.annotation.WebServlet; // Importa la anotación WebServlet para definir el servlet y su URL
import jakarta.servlet.http.HttpServlet; // Importa la clase HttpServlet, que es la clase base para los servlets que manejan solicitudes HTTP
import jakarta.servlet.http.HttpServletRequest; // Importa la clase HttpServletRequest para manejar las solicitudes HTTP entrantes
import jakarta.servlet.http.HttpServletResponse; // Importa la clase HttpServletResponse para manejar las respuestas HTTP salientes
import jakarta.servlet.http.HttpSession; // Importación esencial para manejar la sesión del usuario

@WebServlet(name = "LogoutServlet", urlPatterns = {"/LogoutServlet"}) // Define el servlet con un nombre y una URL específica para acceder a él
public class LogoutServlet extends HttpServlet { // Clase que extiende HttpServlet para manejar las solicitudes HTTP relacionadas con el cierre de sesión

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) // Método que maneja las solicitudes GET enviadas al servlet
            throws ServletException, IOException { // Declara que el método puede lanzar excepciones ServletException e IOException
        
        // 1. Obtener la sesión actual sin crear una nueva (false)
        HttpSession session = request.getSession(false); // Intenta obtener la sesión actual; si no existe, no se crea una nueva
        
        if (session != null) { // Verifica si la sesión existe
            // 2. Destruir la sesión por completo
            session.invalidate(); // Invalida y destruye la sesión actual, eliminando todos los datos asociados a ella
            System.out.println("Sesión cerrada correctamente."); // Imprime un mensaje en la consola indicando que la sesión se ha cerrado
        }
        
        // 3. Redirigir al archivo de inicio (index.jsp)
        response.sendRedirect("index.jsp"); // Redirige al usuario a la página de inicio (index.jsp) después de cerrar la sesión
    }
}