package com.agritrack.agritrackplus.controlador;

import com.agritrack.agritrackplus.DAO.Registro_CultivoDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "EditarCultivoServlet", urlPatterns = {"/EditarCultivoServlet"})
public class EditarCultivoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String id = request.getParameter("id");
        String nombre = request.getParameter("nombre");
        String fechaSiembra = request.getParameter("fecha_siembra");
        String fechaCosecha = request.getParameter("fecha_cosecha");
        String ciclo = request.getParameter("ciclo");
        String estado = request.getParameter("estado");
        
        // 1. CAPTURAR EL SUPERVISOR (Esto es lo que faltaba)
        String supervisorIdStr = request.getParameter("supervisor_id");
        int supervisorId = (supervisorIdStr != null && !supervisorIdStr.isEmpty()) 
                           ? Integer.parseInt(supervisorIdStr) : 0;
        
        String[] trabajadores = request.getParameterValues("trabajadores");
        String[] productos = request.getParameterValues("producto_id");
        
        if (fechaCosecha == null || fechaCosecha.trim().isEmpty()) {
            fechaCosecha = null;
        }

        Registro_CultivoDAO dao = new Registro_CultivoDAO();

        // 2. ACTUALIZAR LLAMADA AL DAO (Agregamos supervisorId al final)
        boolean actualizado = dao.editar(id, nombre, fechaSiembra, fechaCosecha, ciclo, estado, supervisorId);

        if (actualizado) {
            int idCultivo = Integer.parseInt(id);

            // 3. Actualizar trabajadores
            if (trabajadores != null) {
                dao.eliminarTrabajadoresCultivo(idCultivo);
                for (String tId : trabajadores) {
                    if (tId != null && !tId.isEmpty()) {
                        dao.asignarTrabajador(idCultivo, Integer.parseInt(tId));
                    }
                }
            }

            // 4. Actualizar productos
            if (productos != null) {
                dao.eliminarProductosCultivo(idCultivo);
                for (String pId : productos) {
                    if (pId != null && !pId.isEmpty()) {
                        dao.asignarProducto(idCultivo, Integer.parseInt(pId));
                    }
                }
            }

            response.sendRedirect(request.getContextPath()
                    + "/public/Administrador/Detalles_Cultivo.jsp?id=" + id);

        } else {
            response.sendRedirect(request.getContextPath()
                    + "/public/Administrador/Editar_Cultivo.jsp?id=" + id + "&error=true");
        }
    }
}