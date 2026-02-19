package com.agritrack.agritrackplus.controlador;

import com.agritrack.agritrackplus.db.Conexion;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

@WebServlet(name = "ConexionServlet", urlPatterns = {"/ConexionServlet"})
public class ConexionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            try (Connection conn = Conexion.getConnection()) {
                if (conn != null && !conn.isClosed()) {
                    out.println("<h1 style='color:green'>¡Conexión Exitosa rosel!</h1>");
                } else {
                    out.println("<h1 style='color:red'>Error de conexión</h1>");
                }
            } catch (Exception e) {
                out.println("<h1 style='color:red'>Error: " + e.getMessage() + "</h1>");
            }
        }
    }
}
    