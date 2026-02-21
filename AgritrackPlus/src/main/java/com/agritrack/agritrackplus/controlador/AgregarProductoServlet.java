package com.agritrack.agritrackplus.controlador;

import com.agritrack.agritrackplus.DAO.ProductoDAO;
import java.io.IOException;
import java.sql.Date;
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

        request.setCharacterEncoding("UTF-8"); // ✅ Para evitar problemas con tildes

        try {
            // 1. Obtener parámetros
            String nombre = request.getParameter("nombre");
            
            String unidadMedida = request.getParameter("unidad_medida");
            
            double precio = Double.parseDouble(request.getParameter("precio"));
            
            String estado = request.getParameter("estado");
            
            int tipoProductoId = Integer.parseInt(request.getParameter("tipo_producto_id"));

          // En doPost() del Servlet:
            String fechaCompraStr = request.getParameter("fecha_compra");
            java.sql.Date fechaCompra = java.sql.Date.valueOf(fechaCompraStr);

            String fechaVencimientoStr = request.getParameter("fecha_vencimiento");
            java.sql.Date fechaVencimiento = null;
            if (fechaVencimientoStr != null && !fechaVencimientoStr.trim().isEmpty()) {
                fechaVencimiento = java.sql.Date.valueOf(fechaVencimientoStr);
            }

            // Luego llamar al DAO con los tipos correctos:
            ProductoDAO dao = new ProductoDAO();
            boolean exito = dao.agregar(nombre, unidadMedida, precio, 
                                        fechaCompra, fechaVencimiento, 
                                        estado, tipoProductoId);

            // 3. Redirigir según resultado
            if (exito) {
                response.sendRedirect(request.getContextPath() + "/public/Administrador/Productos.jsp?registro=exitoso");
            } else {
                response.sendRedirect(request.getContextPath() + "/public/Administrador/Añadir_Producto.jsp?error=true");
            }
            
        }catch (IllegalArgumentException e) {
            // Captura errores de conversión de fechas (Date.valueOf con formato inválido)
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Añadir_Producto.jsp?error=fecha_invalida");
            
        }
        // Captura errores de conversión de números (precio, tipo_producto_id)
         catch (Exception e) {
            // Cualquier otro error inesperado
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Añadir_Producto.jsp?error=desconocido");
        }
    }
}