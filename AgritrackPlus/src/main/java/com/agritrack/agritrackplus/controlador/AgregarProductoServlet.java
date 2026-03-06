package com.agritrack.agritrackplus.controlador;

import com.agritrack.agritrackplus.DAO.ProductoDAO;
import com.agritrack.agritrackplus.DAO.Registro_CultivoDAO;
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
            String unidadMedida = request.getParameter("unidad_medida"); // Ahora es String (Kilogramo, etc)
            String precioStr = request.getParameter("precio");
            String fechaCompra = request.getParameter("fecha_compra");
            String fechaVencimiento = request.getParameter("fecha_vencimiento");
            String tipoStr = request.getParameter("tipo_producto_id");
            String cantidadStr = request.getParameter("cantidad");

            // Verificación de nulidad sin el campo estado
            if (nombre == null || precioStr == null || precioStr.isEmpty() ||
                tipoStr == null || tipoStr.isEmpty() || cantidadStr == null || cantidadStr.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Producto.jsp?error=true");
                return;
            }

            double precio = Double.parseDouble(precioStr);
            int tipoProductoId = Integer.parseInt(tipoStr);
            int cantidad = Integer.parseInt(cantidadStr);

            // Si la fecha de vencimiento viene vacía (herramientas/semillas), será null
            if (fechaVencimiento == null || fechaVencimiento.trim().isEmpty()) {
                fechaVencimiento = null;
            }

            Registro_CultivoDAO dao = new Registro_CultivoDAO();
            // Llamamos al método sin el parámetro estado
            boolean exito = dao.agregar(nombre, unidadMedida, precio, fechaCompra, fechaVencimiento, tipoProductoId, cantidad);
            
            if (exito) {
                response.sendRedirect(request.getContextPath() + "/public/Administrador/Productos.jsp?registro=exitoso");
            } else {
                response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Producto.jsp?error=true");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Producto.jsp?error=true");
        }
    }
}