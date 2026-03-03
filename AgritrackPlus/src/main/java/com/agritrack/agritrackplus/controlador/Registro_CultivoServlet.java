package com.agritrack.agritrackplus.controlador;

import com.agritrack.agritrackplus.DAO.Registro_CultivoDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "RegistroCultivoServlet", urlPatterns = {"/RegistroCultivoServlet"})
public class Registro_CultivoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8"); // Evita problemas con tildes y caracteres especiales

        // 1. Capturar datos principales del formulario
        String id = request.getParameter("id"); 
        String nombre = request.getParameter("nombre");
        String fechaSiembra = request.getParameter("fecha_siembra");
        String fechaCosecha = request.getParameter("fecha_cosecha"); // Se mantiene para edición
        String ciclo = request.getParameter("ciclo");
        String estado = request.getParameter("estado");

        // 2. Capturar datos de relaciones (IDs de otras tablas)
        String supervisorIdStr = request.getParameter("supervisor_id");
        String[] productoIds = request.getParameterValues("producto_id[]");
        String[] cantidades = request.getParameterValues("cantidad_producto[]");
        String[] trabajadoresIds = request.getParameterValues("trabajadores[]");

        Registro_CultivoDAO dao = new Registro_CultivoDAO();
        boolean exito = false;

        try {
            // 3. Lógica de persistencia (Editar o Registrar)
            if (id != null && !id.isEmpty()) {
                // --- MODO EDICIÓN ---
                int cultivoId = Integer.parseInt(id);
                exito = dao.editar(id, nombre, fechaSiembra, fechaCosecha, ciclo, estado);
                
                if (exito) {
                    // Limpiamos relaciones antiguas para evitar duplicados al re-asignar
                    dao.eliminarTrabajadoresCultivo(cultivoId);
                    dao.eliminarSupervisorCultivo(cultivoId);
                    dao.eliminarProductosCultivo(cultivoId);
                    
                    // Re-asignar Supervisor
                    if (supervisorIdStr != null && !supervisorIdStr.isEmpty()) {
                        dao.asignarSupervisor(cultivoId, Integer.parseInt(supervisorIdStr));
                    }

                    // Re-asignar Productos
                    if (productoIds != null) {
                        for (int i = 0; i < productoIds.length; i++) {
                            if (productoIds[i] != null && !productoIds[i].isEmpty()) {
                                try {
                                    int cant = (cantidades != null && i < cantidades.length && !cantidades[i].isEmpty()) 
                                               ? Integer.parseInt(cantidades[i]) : 1;
                                    dao.asignarProducto(cultivoId, Integer.parseInt(productoIds[i]), cant);
                                } catch (NumberFormatException e) {
                                    dao.asignarProducto(cultivoId, Integer.parseInt(productoIds[i]), 1);
                                }
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
                // Convertimos el ID del supervisor a entero, manejando posibles nulos
                int supervisorId = (supervisorIdStr != null && !supervisorIdStr.isEmpty()) 
                                   ? Integer.parseInt(supervisorIdStr) : 0;
                
                // Llamamos al método que creamos en el DAO que hace la transacción completa:
                // Inserta en 'cultivos', 'supervisor', 'stock_cultivo' y 'cultivo_trabajador'
                exito = dao.registrarCultivoCompleto(nombre, fechaSiembra, ciclo, supervisorId, productoIds, cantidades, trabajadoresIds);
            }

            // 4. Redirección final basada en el resultado
            if (exito) {
                response.sendRedirect(request.getContextPath() + "/public/Administrador/Cultivos_Registrados.jsp?registro=exitoso");
            } else {
                response.sendRedirect(request.getContextPath() + "/public/Administrador/Registro_Cultivos.jsp?error=true");
            }

        } catch (Exception e) {
            e.printStackTrace();
            // En caso de error técnico (base de datos caída, error de parseo, etc.)
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Registro_Cultivos.jsp?error=excepcion");
        }
    }
}