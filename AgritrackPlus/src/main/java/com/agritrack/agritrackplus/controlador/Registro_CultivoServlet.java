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

        // 1. Capturar datos principales del formulario
        String id = request.getParameter("id"); 
        String nombre = request.getParameter("nombre");
        String fechaSiembra = request.getParameter("fecha_siembra");
        String fechaCosecha = request.getParameter("fecha_cosecha"); // Capturamos fecha de cosecha
        String ciclo = request.getParameter("ciclo");
        String estado = request.getParameter("estado");

        // 2. Capturar datos de relaciones (IDs de otras tablas)
        String supervisorId = request.getParameter("supervisor_id");
        String[] productoIds = request.getParameterValues("producto_id[]");
        String[] cantidades = request.getParameterValues("cantidad_producto[]");
        String[] trabajadoresIds = request.getParameterValues("trabajadores[]");

        Registro_CultivoDAO dao = new Registro_CultivoDAO();
        boolean exito = false;
        int cultivoId = 0;

        // 3. Lógica de persistencia (Editar o Registrar)
        if (id != null && !id.isEmpty()) {
            // --- MODO EDICIÓN ---
            cultivoId = Integer.parseInt(id);
            exito = dao.editar(id, nombre, fechaSiembra, fechaCosecha, ciclo, estado);
            
            if (exito) {
                // Limpiamos relaciones antiguas para evitar duplicados al re-asignar
                dao.eliminarTrabajadoresCultivo(cultivoId);
                dao.eliminarSupervisorCultivo(cultivoId);
                dao.eliminarProductosCultivo(cultivoId);
            }
        } else {
            // --- MODO REGISTRO NUEVO ---
            // El método registrar ahora incluye internamente fecha_cosecha como NULL
            cultivoId = dao.registrar(nombre, fechaSiembra, ciclo, estado);
            exito = (cultivoId > 0);
        }

        // 4. Procesar asignaciones solo si el cultivo se guardó/editó correctamente
        if (exito) {
            // Asignar Supervisor (Tabla supervisor)
            if (supervisorId != null && !supervisorId.isEmpty()) {
                dao.asignarSupervisor(cultivoId, Integer.parseInt(supervisorId));
            }

            // Asignar Productos (Tabla stock_cultivo)
            if (productoIds != null) {
                for (int i = 0; i < productoIds.length; i++) {
                    if (productoIds[i] != null && !productoIds[i].isEmpty()) {
                        try {
                            int cantidad = (cantidades != null && i < cantidades.length && !cantidades[i].isEmpty()) 
                                           ? Integer.parseInt(cantidades[i]) : 1;
                            dao.asignarProducto(cultivoId, Integer.parseInt(productoIds[i]), cantidad);
                        } catch (NumberFormatException e) {
                            e.printStackTrace(); // Manejo de error si la cantidad no es numérica
                        }
                    }
                }
            }

            // Asignar Trabajadores (Tabla cultivo_trabajador)
            if (trabajadoresIds != null) {
                for (String trabajadorId : trabajadoresIds) {
                    if (trabajadorId != null && !trabajadorId.isEmpty()) {
                        dao.asignarTrabajador(cultivoId, Integer.parseInt(trabajadorId));
                    }
                }
            }

            // Redirigir al éxito
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Cultivos_Registrados.jsp?registro=exitoso");
        } else {
            // Redirigir al error
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Registro_Cultivos.jsp?error=true");
        }
    }
}