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
            // 1. Obtención de parámetros (incluyendo el ID para edición)
            String idStr = request.getParameter("id"); 
            String nombre = request.getParameter("nombre");
            String unidadMedida = request.getParameter("unidad_medida");
            String precioStr = request.getParameter("precio");
            String fechaCompra = request.getParameter("fecha_compra");
            String fechaVencimiento = request.getParameter("fecha_vencimiento");
            String tipoProducto = request.getParameter("tipo_producto_id");
            String cantidadStr = request.getParameter("cantidad");

            // 2. Validación de campos obligatorios
            if (nombre == null || nombre.isEmpty() || 
                precioStr == null || precioStr.isEmpty() ||
                tipoProducto == null || tipoProducto.isEmpty() || 
                cantidadStr == null || cantidadStr.isEmpty()) {
                
                response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Producto.jsp?error=true");
                return;
            }

            // 3. Conversión de tipos de datos
            double precio = Double.parseDouble(precioStr);
            int cantidad = Integer.parseInt(cantidadStr);

            if (fechaVencimiento == null || fechaVencimiento.trim().isEmpty()) {
                fechaVencimiento = null;
            }

            ProductoDAO dao = new ProductoDAO();
            
            // 4. Lógica de Decisión: ¿Editar o Insertar?
            if (idStr != null && !idStr.isEmpty()) {
                // MODO EDICIÓN
                int id = Integer.parseInt(idStr);
                int tipoId = Integer.parseInt(tipoProducto);
                
                boolean exito = dao.editarProducto(id, nombre, unidadMedida, precio, cantidad, tipoId);
                
                if (exito) {
                    response.sendRedirect(request.getContextPath() + "/public/Administrador/Productos.jsp?actualizacion=exitosa");
                } else {
                    response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Producto.jsp?id=" + id + "&error=true");
                }
            } else {
                // MODO REGISTRO NUEVO
                boolean exito = dao.insertarProducto(nombre, tipoProducto, unidadMedida, precio, cantidad, fechaCompra, fechaVencimiento);
                
                if (exito) {
                    response.sendRedirect(request.getContextPath() + "/public/Administrador/Productos.jsp?registro=exitoso");
                } else {
                    response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Producto.jsp?error=true");
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Producto.jsp?error=true");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Producto.jsp");
    }
}