package com.agritrack.agritrackplus.controlador;

import com.agritrack.agritrackplus.DAO.Registro_CultivoDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Registro_CultivoServlet", urlPatterns = {"/Registro_CultivoServlet"})
public class Registro_CultivoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8"); 

        // 1. Capturar datos del formulario
        String id = request.getParameter("id"); 
        String nombre = request.getParameter("nombre");
        String fechaSiembra = request.getParameter("fecha_siembra");
        String fechaCosecha = request.getParameter("fecha_cosecha"); 
        String ciclo = request.getParameter("ciclo");
        String estado = (request.getParameter("estado") != null) ? request.getParameter("estado") : "Activo";
        
        // Capturar ID del supervisor
        String supervisorIdStr = request.getParameter("supervisor_id");
        int supervisorId = (supervisorIdStr != null && !supervisorIdStr.isEmpty()) ? Integer.parseInt(supervisorIdStr) : 0;

        // Capturar datos de relaciones (Trabajadores y Productos)
        String[] productoIds = request.getParameterValues("producto_id");
        String[] cantidades = request.getParameterValues("cantidad_producto");
        String[] trabajadoresIds = request.getParameterValues("trabajadores");

        Registro_CultivoDAO dao = new Registro_CultivoDAO();
        boolean exito = false;

        try {
            if (id != null && !id.isEmpty()) {
                // --- MODO EDICIÓN ---
                int cultivoId = Integer.parseInt(id);
                
                // IMPORTANTE: Ahora pasamos 7 parámetros al método editar
                exito = dao.editar(id, nombre, fechaSiembra, fechaCosecha, ciclo, estado, supervisorId);
                
                if (exito) {
                    // Limpiar y re-asignar tablas intermedias
                    dao.eliminarTrabajadoresCultivo(cultivoId);
                    dao.eliminarProductosCultivo(cultivoId);
                    
                    // Re-asignar Productos
                    if (productoIds != null) {
                        for (int i = 0; i < productoIds.length; i++) {
                            if (productoIds[i] != null && !productoIds[i].isEmpty()) {
                                int cant = (cantidades != null && i < cantidades.length && !cantidades[i].isEmpty()) 
                                           ? Integer.parseInt(cantidades[i]) : 1;
                                
                                // Usamos el método de asignar producto con cantidad
                                dao.asignarProducto(cultivoId, Integer.parseInt(productoIds[i]));
                            }
                        }
                    }

                    // Re-asignar Trabajadores
                    if (trabajadoresIds != null) {
                        for (String tId : trabajadoresIds) {
                            if (tId != null && !tId.isEmpty()) {
                                dao.asignarTrabajador(cultivoId, Integer.parseInt(tId));
                            }
                        }
                    }
                }
            } else {
                // --- MODO REGISTRO NUEVO ---
                exito = dao.registrarCultivoCompleto(nombre, fechaSiembra, ciclo, supervisorId, productoIds, cantidades, trabajadoresIds);
            }

            // Redirección según el resultado
            if (exito) {
                response.sendRedirect(request.getContextPath() + "/public/Administrador/Cultivos_Registrados.jsp?registro=exitoso");
            } else {
                response.sendRedirect(request.getContextPath() + "/public/Administrador/Registro_Cultivos.jsp?error=true");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Registro_Cultivos.jsp?error=excepcion");
        }
    }
}