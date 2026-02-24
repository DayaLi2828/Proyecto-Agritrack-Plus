package com.agritrack.agritrackplus.controlador;

import com.agritrack.agritrackplus.DAO.UsuarioDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "CrearUsuarioServlet", urlPatterns = {"/CrearUsuarioServlet"})
public class CrearUsuarioServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nombre = request.getParameter("nombre");
        String pass = request.getParameter("pass");//contraseña que se le asigna a un usuario trabajador
        String documento = request.getParameter("documento");
        String direccion = request.getParameter("direccion");
        String estado = request.getParameter("estado");
        String correo = request.getParameter("correo");
        String telefono = request.getParameter("telefono");
        int rolId = Integer.parseInt(request.getParameter("rol_id"));

        UsuarioDAO dao = new UsuarioDAO();
        boolean exito = dao.crear(nombre, pass, documento, direccion, estado, correo, telefono, rolId);

        if (exito) {
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Usuarios.jsp?registro=exitoso");
        } else {
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Usuario.jsp?error=true");
        }
    }
}