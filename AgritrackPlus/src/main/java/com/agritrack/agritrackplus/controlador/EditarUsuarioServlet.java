package com.agritrack.agritrackplus.controlador;

import com.agritrack.agritrackplus.DAO.UsuarioDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "EditarUsuarioServlet", urlPatterns = {"/EditarUsuarioServlet"})
public class EditarUsuarioServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Obtener parámetros del formulario
        String id = request.getParameter("id");
        String nombre = request.getParameter("nombre");
        String documento = request.getParameter("documento");
        String direccion = request.getParameter("direccion");
        String correo = request.getParameter("correo");
        String telefono = request.getParameter("telefono");
        String pass = request.getParameter("pass");
        String rolId = request.getParameter("rol_id");
        String estado = request.getParameter("estado");

        // Aquí podrías manejar también la foto si la subes como archivo multipart
        // Por simplicidad, lo dejamos pendiente

        UsuarioDAO dao = new UsuarioDAO();
        boolean exito = dao.editarUsuario(id, nombre, documento, direccion, correo, telefono, pass, rolId, estado);

        if (exito) {
            // Redirigir de nuevo a la lista de usuarios
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Usuarios.jsp");
        } else {
            // Redirigir al formulario de edición con error
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Usuario.jsp?id=" + id + "&error=true");
        }
    }
}
