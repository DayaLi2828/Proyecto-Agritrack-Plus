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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");
        
        if (id != null) {
            UsuarioDAO dao = new UsuarioDAO();
            boolean eliminado = dao.eliminarUsuario(Integer.parseInt(id));
            
            if (eliminado) {
                response.sendRedirect(request.getContextPath() + "/public/Administrador/Usuarios.jsp?mensaje=eliminado");
            } else {
                response.sendRedirect(request.getContextPath() + "/public/Administrador/Usuarios.jsp?error=eliminado");
            }
        }
    }
}
