package com.agritrack.agritrackplus.controlador;

import com.agritrack.agritrackplus.DAO.ProductoDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "CambiarEstadoProductoServlet", urlPatterns = {"/CambiarEstadoProductoServlet"})
public class CambiarEstadoProductoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // 1. Obtener datos del formulario
            int id = Integer.parseInt(request.getParameter("id"));
            String estadoActual = request.getParameter("estadoActual");
            
            // 2. Lógica de cambio (Toggle)
            String nuevoEstado = "Activo".equalsIgnoreCase(estadoActual) ? "Inactivo" : "Activo";
            
            // 3. Llamar al DAO
            ProductoDAO dao = new ProductoDAO();
            boolean exito = dao.cambiarEstado(id, nuevoEstado);
            
            // 4. Redirigir con mensaje de éxito
            if (exito) {
                response.sendRedirect(request.getContextPath() + "/public/Administrador/Productos.jsp?mensaje=estado");
            } else {
                response.sendRedirect(request.getContextPath() + "/public/Administrador/Productos.jsp?error=true");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Productos.jsp?error=true");
        }
    }
}