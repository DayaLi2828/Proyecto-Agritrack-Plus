package com.agritrack.agritrackplus.controlador;

import com.agritrack.agritrackplus.DAO.Registro_CultivoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet para gestionar el cambio de estado (Activo/Inactivo) de los cultivos.
 */
@WebServlet(name = "CambiarEstadoCultivoServlet", urlPatterns = {"/CambiarEstadoCultivoServlet"})
public class CambiarEstadoCultivoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Obtener los parámetros del formulario del JSP
        String idStr = request.getParameter("id");
        String estadoActual = request.getParameter("estadoActual");
        
        // Instancia del DAO
        Registro_CultivoDAO dao = new Registro_CultivoDAO();

        try {
            if (idStr != null && !idStr.isEmpty() && estadoActual != null) {
                int id = Integer.parseInt(idStr);
                
                // 2. Lógica de intercambio (Toggle)
                // Si viene como 'Activo' (con mayúscula o minúscula), el nuevo será 'Inactivo'
                String nuevoEstado = estadoActual.equalsIgnoreCase("Activo") ? "Inactivo" : "Activo";
                
                // 3. Ejecutar la actualización en la base de datos
                boolean actualizado = dao.cambiarEstado(id, nuevoEstado);
                
                if (actualizado) {
                    System.out.println("DEBUG: Estado del cultivo " + id + " cambiado a " + nuevoEstado);
                } else {
                    System.out.println("DEBUG: No se pudo actualizar el estado en la BD.");
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("Error al convertir ID: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 4. Redirigir de vuelta al JSP de Cultivos Registrados
        // Usamos la ruta que tienes en tu proyecto para evitar el 404
        response.sendRedirect(request.getContextPath() + "/public/Administrador/Cultivos_Registrados.jsp?mensaje=estado");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Por seguridad, si intentan acceder por URL (GET), redirigimos a la lista
        response.sendRedirect(request.getContextPath() + "/public/Administrador/Cultivos_Registrados.jsp");
    }
}