package com.agritrack.agritrackplus.controlador;

import com.agritrack.agritrackplus.DAO.ProductoDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "EliminarProductoServlet", urlPatterns = {"/EliminarProductoServlet"})
public class EliminarProductoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            ProductoDAO dao = new ProductoDAO();
            
            if (dao.eliminarProducto(id)) {
                response.sendRedirect(request.getContextPath() + "/public/Administrador/Productos.jsp?eliminar=exitoso");
            } else {
                response.sendRedirect(request.getContextPath() + "/public/Administrador/Productos.jsp?error=true");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Productos.jsp?error=true");
        }
    }
}