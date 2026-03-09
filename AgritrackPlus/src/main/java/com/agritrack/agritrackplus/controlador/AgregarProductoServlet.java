package com.agritrack.agritrackplus.controlador;

import com.agritrack.agritrackplus.DAO.ProductoDAO; // Cambiado al DAO de productos
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

        // Configurar codificación para evitar problemas con tildes o caracteres especiales
        request.setCharacterEncoding("UTF-8");

        try {
            // 1. Obtención de parámetros del formulario
            String nombre = request.getParameter("nombre");
            String unidadMedida = request.getParameter("unidad_medida");
            String precioStr = request.getParameter("precio");
            String fechaCompra = request.getParameter("fecha_compra");
            String fechaVencimiento = request.getParameter("fecha_vencimiento");
            String tipoProducto = request.getParameter("tipo_producto_id"); // Se asume que es el nombre o ID del tipo
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

            // Manejo de fecha de vencimiento vacía (para herramientas o productos sin vencimiento)
            if (fechaVencimiento == null || fechaVencimiento.trim().isEmpty()) {
                fechaVencimiento = null;
            }

            // 4. Uso del DAO correcto para procesar la información
            ProductoDAO dao = new ProductoDAO();
            
            // Se envía "Activo" como estado por defecto para los nuevos productos
            boolean exito = dao.insertarProducto(nombre, tipoProducto, unidadMedida, precio, cantidad, fechaCompra, fechaVencimiento, "Activo");
            
            // 5. Redirección según el resultado
            if (exito) {
                response.sendRedirect(request.getContextPath() + "/public/Administrador/Productos.jsp?registro=exitoso");
            } else {
                response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Producto.jsp?error=true");
            }
            
        } catch (Exception e) {
            // Log de error en consola para depuración técnica
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Producto.jsp?error=true");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirección de seguridad para evitar acceso directo por URL
        response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Producto.jsp");
    }
}