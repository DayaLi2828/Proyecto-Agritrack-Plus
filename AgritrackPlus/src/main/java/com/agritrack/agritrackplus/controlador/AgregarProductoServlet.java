package com.agritrack.agritrackplus.controlador;

import com.agritrack.agritrackplus.DAO.ProductoDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "AgregarProductoServlet", urlPatterns = {"/AgregarProductoServlet"})
public class AgregarProductoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        try {
            String nombre = request.getParameter("nombre");
            int tipoProductoId = Integer.parseInt(request.getParameter("tipo_producto_id"));
            double unidadMedida = Double.parseDouble(request.getParameter("unidad_medida"));
            double precio = Double.parseDouble(request.getParameter("precio"));
            String fechaCompra = request.getParameter("fecha_compra");
           String fechaVencimiento = request.getParameter("fecha_vencimiento");
                if (fechaVencimiento == null || fechaVencimiento.isEmpty()) {
                    fechaVencimiento = null; // o manejarlo como quieras
                }

            String estado = request.getParameter("estado");
            int cantidad = Integer.parseInt(request.getParameter("cantidad"));

            ProductoDAO dao = new ProductoDAO();
            boolean exito = dao.agregar(nombre, unidadMedida, precio, fechaCompra, 
                                       fechaVencimiento, estado, tipoProductoId, cantidad);

            if (exito) {
                response.sendRedirect(request.getContextPath() + "/webapp/public/Administrador/Productos.jsp?exito=true");

            } else {
                response.sendRedirect(request.getContextPath() + "/webapp/public/Administrador/Añadir_Producto.jsp?error=true");

            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("Añadir_Producto.jsp?error=true");
        }
    }
}
