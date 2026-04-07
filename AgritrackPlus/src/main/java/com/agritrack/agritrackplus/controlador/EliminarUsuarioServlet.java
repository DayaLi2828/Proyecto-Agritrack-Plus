package com.agritrack.agritrackplus.controlador; // Declara el paquete donde se encuentra la clase, que es parte de la aplicación Agritrack Plus.

import com.agritrack.agritrackplus.DAO.UsuarioDAO; // Importa la clase UsuarioDAO, que maneja la lógica de acceso a datos para usuarios.
import jakarta.servlet.ServletException; // Importa la excepción ServletException, que se lanza cuando hay un problema en el servlet.
import jakarta.servlet.annotation.WebServlet; // Importa la anotación WebServlet, que define la configuración del servlet.
import jakarta.servlet.http.HttpServlet; // Importa la clase base HttpServlet, que proporciona la funcionalidad para manejar solicitudes HTTP.
import jakarta.servlet.http.HttpServletRequest; // Importa la clase HttpServletRequest, que representa la solicitud HTTP del cliente.
import jakarta.servlet.http.HttpServletResponse; // Importa la clase HttpServletResponse, que representa la respuesta HTTP que se enviará al cliente.
import java.io.IOException; // Importa la excepción IOException, que se lanza cuando hay un error de entrada/salida.

@WebServlet(name = "EliminarUsuarioServlet", urlPatterns = {"/EliminarUsuarioServlet"}) // Define el servlet con el nombre "EliminarUsuarioServlet" y especifica la URL a la que responde.
public class EliminarUsuarioServlet extends HttpServlet { // Declara la clase EliminarUsuarioServlet que extiende HttpServlet, permitiendo manejar solicitudes HTTP.

    @Override // Indica que este método sobrescribe un método de la clase padre HttpServlet.
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 
        // Método que maneja las solicitudes POST. Recibe el objeto request (contiene datos de la solicitud) y el objeto response (para enviar la respuesta).

        String idStr = request.getParameter("id"); // Obtiene el parámetro "id" de la solicitud HTTP, que se espera que contenga el ID del usuario a eliminar.

        if (idStr != null && !idStr.isEmpty()) { // Verifica que el ID no sea nulo y no esté vacío.
            try {
                int id = Integer.parseInt(idStr); // Intenta convertir el ID de String a int. Si falla, lanza una excepción NumberFormatException.
                UsuarioDAO dao = new UsuarioDAO(); // Crea una instancia de UsuarioDAO para acceder a la lógica de eliminación de usuarios.

                if (dao.eliminarUsuario(id)) { // Llama al método eliminarUsuario en UsuarioDAO, pasando el ID del usuario. Si devuelve true, la eliminación fue exitosa.
                    response.sendRedirect(request.getContextPath() + "/public/Administrador/Usuarios.jsp?mensaje=eliminado"); 
                    // Redirige al usuario a la página de usuarios con un mensaje indicando que la eliminación fue exitosa.
                } else {
                    response.sendRedirect(request.getContextPath() + "/public/Administrador/Usuarios.jsp?error=no_eliminado"); 
                    // Redirige a la misma página con un mensaje de error si la eliminación no fue exitosa.
                }
            } catch (NumberFormatException e) { // Captura la excepción si el ID no se puede convertir a un número entero.
                response.sendRedirect(request.getContextPath() + "/public/Administrador/Usuarios.jsp?error=id_invalido"); 
                // Redirige a la página de usuarios con un mensaje de error indicando que el ID es inválido.
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Usuarios.jsp"); 
            // Si el ID es nulo o vacío, redirige a la página de usuarios sin ningún mensaje.
        }
    }
}