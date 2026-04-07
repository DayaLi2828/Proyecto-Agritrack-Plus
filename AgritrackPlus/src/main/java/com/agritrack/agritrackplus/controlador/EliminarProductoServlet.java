package com.agritrack.agritrackplus.controlador; // Paquete que contiene la clase del controlador del servlet para la gestión de productos

import com.agritrack.agritrackplus.DAO.ProductoDAO; // Importa la clase ProductoDAO para realizar operaciones relacionadas con productos
import java.io.IOException; // Importa la clase IOException para manejar errores de entrada/salida
import jakarta.servlet.ServletException; // Importa la clase ServletException para manejar errores específicos de servlets
import jakarta.servlet.annotation.WebServlet; // Importa la anotación WebServlet para definir el servlet y su URL
import jakarta.servlet.http.HttpServlet; // Importa la clase HttpServlet, que es la clase base para los servlets que manejan solicitudes HTTP
import jakarta.servlet.http.HttpServletRequest; // Importa la clase HttpServletRequest para manejar las solicitudes HTTP entrantes
import jakarta.servlet.http.HttpServletResponse; // Importa la clase HttpServletResponse para manejar las respuestas HTTP salientes
/*
maneja solicitudes POST para eliminar un producto de la base de datos. 
Extrae el ID del producto desde la solicitud, utiliza el objeto ProductoDAO para intentar eliminarlo y 
redirige al usuario a la página de productos con un mensaje de éxito o error, según el resultado de la 
operación. Si ocurre una excepción durante el proceso, se captura y se redirige al usuario a la misma 
página con un mensaje de error.
*/
@WebServlet(name = "EliminarProductoServlet", urlPatterns = {"/EliminarProductoServlet"}) // Define el servlet con un nombre y una URL específica para acceder a él
public class EliminarProductoServlet extends HttpServlet { // Clase que extiende HttpServlet para manejar las solicitudes HTTP relacionadas con la eliminación de productos

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) // Método que maneja las solicitudes POST enviadas al servlet
            throws ServletException, IOException { // Declara que el método puede lanzar excepciones ServletException e IOException
        
        try { // Inicia un bloque try para manejar excepciones que puedan ocurrir durante la ejecución
            int id = Integer.parseInt(request.getParameter("id")); // Extrae el parámetro "id" de la solicitud HTTP y lo convierte de String a int para identificar el producto a eliminar
            ProductoDAO dao = new ProductoDAO(); // Crea una instancia del DAO para realizar operaciones de base de datos relacionadas con productos
            
            if (dao.eliminarProducto(id)) { // Llama al método eliminarProducto del DAO, pasando el ID del producto, y verifica si la eliminación fue exitosa
                response.sendRedirect(request.getContextPath() + "/public/Administrador/Productos.jsp?eliminar=exitoso"); // Si la eliminación fue exitosa, redirige al usuario a la página de productos con un mensaje de éxito en la URL
            } else { // Si la eliminación no fue exitosa
                response.sendRedirect(request.getContextPath() + "/public/Administrador/Productos.jsp?error=true"); // Redirige al usuario a la página de productos con un mensaje de error en la URL
            }
        } catch (Exception e) { // Captura cualquier excepción que ocurra durante el proceso
            e.printStackTrace(); // Imprime el stack trace del error en la consola para facilitar la depuración
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Productos.jsp?error=true"); // Redirige al usuario a la página de productos con un mensaje de error en la URL en caso de excepción
        }
    }
}