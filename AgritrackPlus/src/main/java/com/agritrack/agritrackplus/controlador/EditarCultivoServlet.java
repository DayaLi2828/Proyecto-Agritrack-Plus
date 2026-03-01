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

        String id = request.getParameter("id");
        String nombre = request.getParameter("nombre");
        String fechaSiembra = request.getParameter("fecha_siembra");
        String fechaCosecha = request.getParameter("fecha_cosecha");
        String ciclo = request.getParameter("ciclo");
        String estado = request.getParameter("estado");
        
        // CORRECCIÓN: Usar "trabajadores[]" para que coincida con el formulario JSP
        String[] trabajadoresIds = request.getParameterValues("trabajadores[]");

        Registro_CultivoDAO dao = new Registro_CultivoDAO();
        
        // Intentar actualizar los datos básicos del cultivo
        boolean exito = dao.editar(id, nombre, fechaSiembra, fechaCosecha, ciclo, estado);

        if (exito) {
            int idCultivo = Integer.parseInt(id);
            
            // 1. Limpiamos los trabajadores actuales para actualizar la lista
            dao.eliminarTrabajadoresCultivo(idCultivo);
            
            // 2. Asignamos los nuevos trabajadores seleccionados
            if (trabajadoresIds != null) {
                for (String trabajadorId : trabajadoresIds) {
                    if (trabajadorId != null && !trabajadorId.isEmpty()) {
                        dao.asignarTrabajador(idCultivo, Integer.parseInt(trabajadorId));
                    }
                }
            }
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Detalles_Cultivo.jsp?id=" + id);
        } else {
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Editar_Cultivo.jsp?id=" + id + "&error=true");
        }
    }
}