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

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Obtener lo que el usuario escribió en el HTML
        // Usamos los nombres exactos: "email" y "password"
        String correo = request.getParameter("email");
        String documento = request.getParameter("password"); 

        // 2. Llamar al DAO para validar
        UsuarioDAO dao = new UsuarioDAO();
        Usuario user = dao.login(correo, documento);

        if (user != null) {
            // LOGIN CORRECTO
            HttpSession session = request.getSession();
            session.setAttribute("usuarioLogueado", user);
            response.sendRedirect("bienvenida.jsp"); 
        } else {
            // LOGIN INCORRECTO
            // Redirige al index pero avisando que hubo un error
            response.sendRedirect("index.html?error=true");
        }
    }
}