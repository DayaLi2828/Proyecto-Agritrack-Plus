package com.agritrack.agritrackplus.controlador; // Paquete que contiene la clase del controlador para cambiar el estado de los usuarios

import com.agritrack.agritrackplus.DAO.UsuarioDAO; // Importación de la clase UsuarioDAO para interactuar con la base de datos de usuarios
import jakarta.servlet.ServletException; // Importación de la excepción para manejar errores en servlets
import jakarta.servlet.annotation.WebServlet; // Importación de la anotación para definir un servlet
import jakarta.servlet.http.HttpServlet; // Importación de la clase base para servlets HTTP
import jakarta.servlet.http.HttpServletRequest; // Importación para manejar solicitudes HTTP
import jakarta.servlet.http.HttpServletResponse; // Importación para manejar respuestas HTTP
import java.io.IOException; // Importación de la excepción para manejar errores de entrada/salida
import java.util.Map; // Importación de la clase Map para trabajar con pares clave-valor

/*
Este servlet se encarga de cambiar el estado de los usuarios entre "Activo" e "Inactivo". 
Primero, obtiene el ID del usuario enviado desde un formulario y verifica que sea válido. 
Luego, utiliza el UsuarioDAO para obtener la información del usuario y determinar su estado actual. 
Si el usuario existe, cambia su estado y actualiza la base de datos. 
Finalmente, redirige al usuario a la página de administración de usuarios, añadiendo un mensaje en 
la URL para indicar que se ha realizado una acción.
*/

@WebServlet(name = "CambiarEstadoServlet", urlPatterns = {"/CambiarEstadoServlet"}) // Define el servlet con un nombre y una URL
public class CambiarEstadoServlet extends HttpServlet { // Clase que extiende HttpServlet para manejar solicitudes HTTP

   @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) // Método que maneja solicitudes POST
            throws ServletException, IOException {

        // 1. Evitar que cualquier filtro previo ensucie la respuesta
        if (response.isCommitted()) return; // Si la respuesta ya ha sido comprometida, termina el método para evitar errores

        String idStr = request.getParameter("id"); // Captura el ID del usuario desde el formulario enviado
        UsuarioDAO dao = new UsuarioDAO(); // Crea una instancia de UsuarioDAO para interactuar con la base de datos de usuarios

        try {
            // Verifica que el ID no sea nulo o vacío
            if (idStr != null && !idStr.isEmpty()) {
                // Obtiene los datos del usuario desde la base de datos usando el ID
                Map<String, String> usuario = dao.obtenerPorId(idStr); // Llama al método obtenerPorId para obtener la información del usuario
                
                // Verifica que el usuario no sea nulo
                if (usuario != null) {
                    // Lógica para cambiar el estado: si el estado actual es 'Activo', cambia a 'Inactivo'; de lo contrario, cambia a 'Activo'
                    String nuevoEstado = "Activo".equalsIgnoreCase(usuario.get("estado")) ? "Inactivo" : "Activo"; 
                    // Llama al método actualizarEstado para cambiar el estado del usuario en la base de datos
                    dao.actualizarEstado(Integer.parseInt(idStr), nuevoEstado); // Convierte el ID a entero y actualiza el estado
                }
            }
        } catch (Exception e) { // Captura cualquier excepción que ocurra durante el proceso
            e.printStackTrace(); // Imprime la traza de la excepción en la consola para depuración
        }

        // 2. REDIRECCIÓN MANUAL (Más segura que sendRedirect en algunos entornos)
        response.setStatus(HttpServletResponse.SC_SEE_OTHER); // Establece el estado de la respuesta como '303 See Other'
        response.setHeader("Location", request.getContextPath() + "/public/Administrador/Usuarios.jsp?mensaje=estado"); // Establece la ubicación de redirección al JSP de usuarios con un mensaje
        return; // Finaliza el método, asegurando que no haya más código ejecutándose
    }
}