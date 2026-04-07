package com.agritrack.agritrackplus.controlador; // Paquete que contiene la clase del controlador para cambiar el estado de los cultivos

import com.agritrack.agritrackplus.DAO.Registro_CultivoDAO; // Importación de la clase Registro_CultivoDAO para interactuar con la base de datos de cultivos
import jakarta.servlet.ServletException; // Importación de la excepción para manejar errores en servlets
import jakarta.servlet.annotation.WebServlet; // Importación de la anotación para definir un servlet
import jakarta.servlet.http.HttpServlet; // Importación de la clase base para servlets HTTP
import jakarta.servlet.http.HttpServletRequest; // Importación para manejar solicitudes HTTP
import jakarta.servlet.http.HttpServletResponse; // Importación para manejar respuestas HTTP
import java.io.IOException; // Importación de la excepción para manejar errores de entrada/salida

/**
 * Servlet para gestionar el cambio de estado (Activo/Inactivo) de los cultivos.
 */
@WebServlet(name = "CambiarEstadoCultivoServlet", urlPatterns = {"/CambiarEstadoCultivoServlet"}) // Define el servlet con un nombre y una URL
public class CambiarEstadoCultivoServlet extends HttpServlet { // Clase que extiende HttpServlet para manejar solicitudes HTTP

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) // Método que maneja solicitudes POST
            throws ServletException, IOException {
        
        // 1. Obtener los parámetros del formulario del JSP
        String idStr = request.getParameter("id"); // Captura el ID del cultivo desde el formulario
        String estadoActual = request.getParameter("estadoActual"); // Captura el estado actual del cultivo (Activo/Inactivo)
        
        // Instancia del DAO
        Registro_CultivoDAO dao = new Registro_CultivoDAO(); // Crea una instancia de Registro_CultivoDAO para interactuar con la base de datos

        try {
            // Verifica que el ID no sea nulo o vacío y que el estado actual no sea nulo
            if (idStr != null && !idStr.isEmpty() && estadoActual != null) {
                int id = Integer.parseInt(idStr); // Convierte el ID del cultivo de String a int
                
                // 2. Lógica de intercambio (Toggle)
                // Si el estado actual es 'Activo' (sin importar mayúsculas o minúsculas), el nuevo estado será 'Inactivo'
                String nuevoEstado = estadoActual.equalsIgnoreCase("Activo") ? "Inactivo" : "Activo"; // Determina el nuevo estado basado en el estado actual
                
                // 3. Ejecutar la actualización en la base de datos
                boolean actualizado = dao.cambiarEstado(id, nuevoEstado); // Llama al método cambiarEstado en el DAO para actualizar el estado en la base de datos
                
                // Verifica si la actualización fue exitosa
                if (actualizado) {
                    System.out.println("DEBUG: Estado del cultivo " + id + " cambiado a " + nuevoEstado); // Mensaje de depuración si la actualización fue exitosa
                } else {
                    System.out.println("DEBUG: No se pudo actualizar el estado en la BD."); // Mensaje de depuración si la actualización falló
                }
            }
        } catch (NumberFormatException e) { // Captura excepciones de formato numérico
            System.err.println("Error al convertir ID: " + e.getMessage()); // Mensaje de error si la conversión del ID falla
        } catch (Exception e) { // Captura cualquier otra excepción
            e.printStackTrace(); // Imprime la traza de la excepción en la consola para depuración
        }

        // 4. Redirigir de vuelta al JSP de Cultivos Registrados
        response.sendRedirect(request.getContextPath() + "/public/Administrador/Cultivos_Registrados.jsp?mensaje=estado"); // Redirige al JSP de cultivos registrados con un mensaje de estado
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) // Método que maneja solicitudes GET
            throws ServletException, IOException {
        // Por seguridad, si intentan acceder por URL (GET), redirigimos a la lista
        response.sendRedirect(request.getContextPath() + "/public/Administrador/Cultivos_Registrados.jsp"); // Redirige al JSP de cultivos registrados si se accede mediante GET
    }
}