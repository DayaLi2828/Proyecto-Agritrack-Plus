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

        String nombre = request.getParameter("nombre");
        double unidadMedida = Double.parseDouble(request.getParameter("unidad_medida"));
        double precio = Double.parseDouble(request.getParameter("precio"));
        String fechaCompra = request.getParameter("fecha_compra");
        String fechaVencimiento = request.getParameter("fecha_vencimiento");
        String estado = request.getParameter("estado");
        int tipoProductoId = Integer.parseInt(request.getParameter("tipo_producto_id"));

        ProductoDAO dao = new ProductoDAO();
        boolean exito = dao.agregar(nombre, unidadMedida, precio, fechaCompra, fechaVencimiento, estado, tipoProductoId);

        if (exito) {
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Productos.jsp?registro=exitoso");
        } else {
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Añadir_Producto.jsp?error=true");
        }
    }
}