package com.agritrack.agritrackplus.controlador;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // Importación esencial para manejar la sesión

@WebServlet(name = "LogoutServlet", urlPatterns = {"/LogoutServlet"})
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Obtener la sesión actual sin crear una nueva (false)
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            // 2. Destruir la sesión por completo
            session.invalidate(); 
            System.out.println("Sesión cerrada correctamente.");
        }
        
        // 3. Redirigir al archivo de inicio (index.jsp)
        response.sendRedirect("index.jsp"); 
    }
}