package com.agritrack.agritrackplus.controlador;

import com.agritrack.agritrackplus.DAO.TareaDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@WebServlet("/ActualizarEstadoTareaServlet")
public class ActualizarEstadoTareaServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);
        String rol = (sesion != null) ? (String) sesion.getAttribute("rol") : null;
        Integer idUsuario = (sesion != null) ? (Integer) sesion.getAttribute("usuario_id") : null;

        // Validación de seguridad (Case insensitive para el rol)
        if (idUsuario == null || !"trabajador".equalsIgnoreCase(rol)) {
            response.sendRedirect(request.getContextPath() + "/index.jsp?error=acceso_denegado");
            return;
        }

        String idTareaStr = request.getParameter("idTarea");
        String nuevoEstado = request.getParameter("estado");
        String observacion = request.getParameter("txtObservacion"); 
        
        if (observacion == null) observacion = "";                   
        observacion = observacion.trim(); 
        
        if (idTareaStr == null || nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/public/Trabajador/Completar_Tareas.jsp?status=error_parametros");
            return;
        }

        try {
            int idTarea = Integer.parseInt(idTareaStr);
            String estadoNormalizado;

            // Normalización estricta
            switch (nuevoEstado.toLowerCase()) {
                case "completada":  
                    estadoNormalizado = "Completada"; 
                    break;
                case "en proceso":
                case "proceso":     
                    estadoNormalizado = "En Proceso"; 
                    break;
                default:            
                    estadoNormalizado = "Pendiente";  
                    break;
            }

            TareaDAO dao = new TareaDAO();
            boolean actualizado = dao.actualizarEstadoTarea(idTarea, estadoNormalizado, observacion);

            if (actualizado) {
                // REFRESCAR DATOS PARA GRÁFICOS
                sesion.setAttribute("datosGrafico", dao.obtenerConteosPorEstado(idUsuario));
                sesion.setAttribute("datosCultivos", dao.obtenerCumplimientoCultivos(idUsuario));
                sesion.setAttribute("datosPago", dao.obtenerResumenPagos(idUsuario));

                // Recomendación: Regresar a la lista de tareas con un mensaje de éxito
                response.sendRedirect(request.getContextPath() + "/public/Trabajador/Completar_Tareas.jsp?status=success");
            } else {
                response.sendRedirect(request.getContextPath() + "/public/Trabajador/Completar_Tareas.jsp?status=error_db");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/public/Trabajador/Completar_Tareas.jsp?status=error_id");
        }
    }
}
