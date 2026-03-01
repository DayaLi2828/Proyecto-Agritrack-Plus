package com.agritrack.agritrackplus.controlador;

import com.agritrack.agritrackplus.DAO.UsuarioDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "EliminarUsuarioServlet", urlPatterns = {"/EliminarUsuarioServlet"})
    public class EliminarUsuarioServlet extends HttpServlet {

        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");

        if (idStr != null && !idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                UsuarioDAO dao = new UsuarioDAO();

                if (dao.eliminarUsuario(id)) {
                    response.sendRedirect(request.getContextPath() + "/public/Administrador/Usuarios.jsp?mensaje=eliminado");
                } else {
                    response.sendRedirect(request.getContextPath() + "/public/Administrador/Usuarios.jsp?error=no_eliminado");
                }
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/public/Administrador/Usuarios.jsp?error=id_invalido");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Usuarios.jsp");
        }
    }
}

