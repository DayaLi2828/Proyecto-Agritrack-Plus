package com.agritrack.agritrackplus.controlador; // Paquete que contiene la clase del controlador para cambiar el estado de los productos

import com.agritrack.agritrackplus.DAO.ProductoDAO; // Importación de la clase ProductoDAO para interactuar con la base de datos de productos
import java.io.IOException; // Importación de la excepción para manejar errores de entrada/salida
import jakarta.servlet.ServletException; // Importación de la excepción para manejar errores en servlets
import jakarta.servlet.annotation.WebServlet; // Importación de la anotación para definir un servlet
import jakarta.servlet.http.HttpServlet; // Importación de la clase base para servlets HTTP
import jakarta.servlet.http.HttpServletRequest; // Importación para manejar solicitudes HTTP
import jakarta.servlet.http.HttpServletResponse; // Importación para manejar respuestas HTTP

/*
Este servlet se encarga de cambiar el estado de los productos entre "Activo" e "Inactivo". 
Primero, obtiene los datos enviados desde un formulario y verifica que sean válidos. Luego, 
determina el nuevo estado basado en el estado actual y llama a un método en la clase ProductoDAO 
para actualizar la base de datos. Después de intentar actualizar el estado, redirige al usuario de 
vuelta a la página de productos, añadiendo un mensaje en la URL para indicar si la acción fue exitosa 
o si hubo un error.
*/
@WebServlet(name = "CambiarEstadoProductoServlet", urlPatterns = {"/CambiarEstadoProductoServlet"}) // Define el servlet con un nombre y una URL
public class CambiarEstadoProductoServlet extends HttpServlet { // Clase que extiende HttpServlet para manejar solicitudes HTTP

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) // Método que maneja solicitudes POST
            throws ServletException, IOException {
        
        try {
            // 1. Obtener datos del formulario
            int id = Integer.parseInt(request.getParameter("id")); // Captura el ID del producto desde el formulario y lo convierte a entero
            String estadoActual = request.getParameter("estadoActual"); // Captura el estado actual del producto (Activo/Inactivo) desde el formulario
            
            // 2. Lógica de cambio (Toggle)
            // Determina el nuevo estado: si el estado actual es 'Activo', cambia a 'Inactivo'; de lo contrario, cambia a 'Activo'
            String nuevoEstado = "Activo".equalsIgnoreCase(estadoActual) ? "Inactivo" : "Activo"; 
            
            // 3. Llamar al DAO
            ProductoDAO dao = new ProductoDAO(); // Crea una instancia de ProductoDAO para interactuar con la base de datos de productos
            boolean exito = dao.cambiarEstado(id, nuevoEstado); // Llama al método cambiarEstado en el DAO para actualizar el estado del producto en la base de datos
            
            // 4. Redirigir con mensaje de éxito
            // Si la actualización fue exitosa, redirige al JSP de productos con un mensaje de éxito; si falló, redirige con un mensaje de error
            if (exito) {
                response.sendRedirect(request.getContextPath() + "/public/Administrador/Productos.jsp?mensaje=estado"); // Redirige a la página de productos con un mensaje de estado
            } else {
                response.sendRedirect(request.getContextPath() + "/public/Administrador/Productos.jsp?error=true"); // Redirige a la página de productos con un indicador de error
            }
            
        } catch (Exception e) { // Captura cualquier excepción que ocurra durante el proceso
            e.printStackTrace(); // Imprime la traza de la excepción en la consola para depuración
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Productos.jsp?error=true"); // Redirige a la página de productos con un indicador de error
        }
    }
}