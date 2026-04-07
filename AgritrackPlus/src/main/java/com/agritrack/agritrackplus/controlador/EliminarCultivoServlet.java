package com.agritrack.agritrackplus.controlador; // Paquete que contiene la clase del controlador del servlet para la gestión de cultivos

import com.agritrack.agritrackplus.DAO.Registro_CultivoDAO; // Importa la clase Registro_CultivoDAO para realizar operaciones relacionadas con cultivos
import jakarta.servlet.ServletException; // Importa la clase ServletException para manejar errores específicos de servlets
import jakarta.servlet.annotation.WebServlet; // Importa la anotación WebServlet para definir el servlet y su URL
import jakarta.servlet.http.HttpServlet; // Importa la clase HttpServlet, que es la clase base para los servlets que manejan solicitudes HTTP
import jakarta.servlet.http.HttpServletRequest; // Importa la clase HttpServletRequest para manejar las solicitudes HTTP entrantes
import jakarta.servlet.http.HttpServletResponse; // Importa la clase HttpServletResponse para manejar las respuestas HTTP salientes
import java.io.IOException; // Importa la clase IOException para manejar errores de entrada/salida
/*
maneja solicitudes POST para eliminar un cultivo específico de la base de datos. 
Extrae el ID del cultivo desde la solicitud, utiliza el objeto Registro_CultivoDAO para intentar 
eliminarlo y redirige al usuario a una página de cultivos registrados con un mensaje de éxito. 
Si se intenta acceder al servlet mediante una solicitud GET, redirige a la misma página como medida 
de seguridad. Además, maneja errores de formato en el ID proporcionado.
*/
@WebServlet(name = "EliminarCultivoServlet", urlPatterns = {"/EliminarCultivoServlet"}) // Define el servlet con un nombre y una URL específica para acceder a él
public class EliminarCultivoServlet extends HttpServlet { // Clase que extiende HttpServlet para manejar las solicitudes HTTP relacionadas con la eliminación de cultivos

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) // Método que maneja las solicitudes POST enviadas al servlet
            throws ServletException, IOException { // Declara que el método puede lanzar excepciones ServletException e IOException

        // 1. Obtener el ID del cultivo a eliminar
        String idStr = request.getParameter("id"); // Extrae el parámetro "id" de la solicitud HTTP, que representa el ID del cultivo a eliminar
        
        if (idStr != null && !idStr.isEmpty()) { // Verifica que el ID no sea nulo ni vacío
            try {
                int id = Integer.parseInt(idStr); // Convierte el ID de String a int para su uso en la eliminación
                Registro_CultivoDAO dao = new Registro_CultivoDAO(); // Crea una instancia del DAO para realizar operaciones de base de datos relacionadas con cultivos
                
                // 2. Llamar al método de eliminación del DAO
                boolean eliminado = dao.eliminarCultivo(id); // Llama al método eliminarCultivo del DAO, pasando el ID del cultivo y guarda el resultado en 'eliminado'
                
                if (eliminado) { // Verifica si la eliminación fue exitosa
                    System.out.println("DEBUG: Cultivo " + id + " eliminado con éxito."); // Imprime un mensaje de depuración en la consola indicando que el cultivo fue eliminado
                }
            } catch (NumberFormatException e) { // Captura cualquier excepción de formato que ocurra al convertir el ID
                System.err.println("Error de formato en ID: " + e.getMessage()); // Imprime un mensaje de error en la consola si el formato del ID es incorrecto
            }
        }

        // 3. Redirigir de vuelta a la lista con el mensaje para la alerta
        response.sendRedirect(request.getContextPath() + "/public/Administrador/Cultivos_Registrados.jsp?mensaje=eliminado"); // Redirige al usuario a la página de cultivos registrados, pasando un mensaje de éxito en la eliminación como parámetro en la URL
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) // Método que maneja las solicitudes GET enviadas al servlet
            throws ServletException, IOException { // Declara que el método puede lanzar excepciones ServletException e IOException
        // Redirección de seguridad si intentan entrar por URL
        response.sendRedirect(request.getContextPath() + "/public/Administrador/Cultivos_Registrados.jsp"); // Redirige al usuario a la página de cultivos registrados si intenta acceder al servlet directamente mediante una solicitud GET
    }
}