package com.agritrack.agritrackplus.controlador;

import com.agritrack.agritrackplus.DAO.TareaDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/AsignarTareaServlet")
public class AsignarTareaServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // 1. Capturamos los datos
            String nombreTareaManual = request.getParameter("txtNombreTarea");
            String idCultivoStr = request.getParameter("cboCultivo");
            String descripcion = request.getParameter("txtDescripcion");
            String jornada = request.getParameter("cboJornada");
            String idTrabajadorStr = request.getParameter("cboTrabajador");

            // DEBUG: Esto saldrá en la consola de tu IDE para que verifiques si llegan los datos
            System.out.println("--- INTENTO DE REGISTRO DE TAREA ---");
            System.out.println("Nombre Tarea: " + nombreTareaManual);
            System.out.println("ID Cultivo: " + idCultivoStr);
            System.out.println("ID Trabajador: " + idTrabajadorStr);

            // 2. Validación rápida para evitar el NumberFormatException
            if (idCultivoStr == null || idTrabajadorStr == null || nombreTareaManual == null) {
                System.out.println("ERROR: Uno de los campos llegó NULL del formulario");
                response.sendRedirect(request.getContextPath() + "/public/Supervisor/Agregar_Tarea.jsp?status=missing_data");
                return;
            }

            int idCultivo = Integer.parseInt(idCultivoStr);
            int idTrabajador = Integer.parseInt(idTrabajadorStr);

            TareaDAO tdao = new TareaDAO();
            boolean insertado = tdao.agregarTareaManual(idCultivo, descripcion, nombreTareaManual, jornada, idTrabajador);

            if (insertado) {
                System.out.println("REGISTRO EXITOSO");
                response.sendRedirect(request.getContextPath() + "/public/Supervisor/Tareas.jsp?status=success");
            } else {
                System.out.println("ERROR: El DAO devolvió false (Error en el INSERT)");
                response.sendRedirect(request.getContextPath() + "/public/Supervisor/Agregar_Tarea.jsp?status=error");
            }

        } catch (NumberFormatException e) {
            System.out.println("ERROR: No se pudo convertir un ID a número");
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/public/Supervisor/Agregar_Tarea.jsp?status=invalid_format");
        } catch (Exception e) {
            System.out.println("ERROR FATAL: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/public/Supervisor/Agregar_Tarea.jsp?status=fatal_error");
        }
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirigimos al formulario usando la ruta completa
        response.sendRedirect(request.getContextPath() + "/public/Supervisor/Agregar_Tarea.jsp");
    }
}