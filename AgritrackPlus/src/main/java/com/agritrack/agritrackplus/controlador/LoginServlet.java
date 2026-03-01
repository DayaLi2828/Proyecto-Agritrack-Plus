package com.agritrack.agritrackplus.controlador;
import com.agritrack.agritrackplus.DAO.UsuarioDAO;

import com.agritrack.agritrackplus.modelo.Usuario;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String correo = request.getParameter("email");
        String pass = request.getParameter("password");

        UsuarioDAO dao = new UsuarioDAO();
        Usuario user = null;
        user = dao.login(correo, pass);

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("usuarioLogueado", user);
            String rol = user.getRol();
            if ("administrador".equals(rol)) {
                response.sendRedirect(request.getContextPath() + "/public/Administrador/Admin.jsp");
            } else if ("usuario".equals(rol)) {
                response.sendRedirect(request.getContextPath() + "/public/Trabajador/index_Trabajador.html");
            } else {
                response.sendRedirect(request.getContextPath() + "/index.jsp?error=true");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/index.jsp?error=true");
        }
    }
}