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
        int supervisorId = (supervisorIdStr != null && !supervisorIdStr.isEmpty())
                           ? Integer.parseInt(supervisorIdStr) : 0;

        // Capturar datos de relaciones (Trabajadores y Productos)
        String[] productoIds = request.getParameterValues("producto_id");
        String[] cantidades = request.getParameterValues("cantidad_producto");
        String[] trabajadoresIds = request.getParameterValues("trabajadores");

        Registro_CultivoDAO dao = new Registro_CultivoDAO();

        try {
            if (id != null && !id.isEmpty()) {
                // --- MODO EDICIÓN ---
                String resultado = dao.editarCultivoCompleto(
                    id, nombre, fechaSiembra, fechaCosecha, ciclo, estado, supervisorId,
                    productoIds, cantidades, trabajadoresIds
                );

                if (resultado.equals("ok")) {
                    response.sendRedirect(request.getContextPath() +
                        "/public/Administrador/Cultivos_Registrados.jsp?mensaje=actualizado");

                } else if (resultado.startsWith("agotado:")) {
                    String nombresAgotados = resultado.substring("agotado:".length());
                    response.sendRedirect(request.getContextPath() +
                        "/public/Administrador/Cultivos_Registrados.jsp?mensaje=actualizado&agotado=" +
                        java.net.URLEncoder.encode(nombresAgotados, "UTF-8"));

                } else if (resultado.startsWith("insuficiente:")) {
                    String detalle = resultado.substring("insuficiente:".length());
                    response.sendRedirect(request.getContextPath() +
                        "/public/Administrador/Registro_Cultivos.jsp?id=" + id + "&error=insuficiente&detalle=" +
                        java.net.URLEncoder.encode(detalle, "UTF-8"));

                } else {
                    response.sendRedirect(request.getContextPath() +
                        "/public/Administrador/Registro_Cultivos.jsp?id=" + id + "&error=true");
                }

            } else {
                // --- MODO REGISTRO NUEVO ---
                String resultado = dao.registrarCultivoCompleto(
                    nombre, fechaSiembra, ciclo, supervisorId,
                    productoIds, cantidades, trabajadoresIds
                );

                if (resultado.equals("ok")) {
                    response.sendRedirect(request.getContextPath() +
                        "/public/Administrador/Cultivos_Registrados.jsp?mensaje=registrado");

                } else if (resultado.startsWith("agotado:")) {
                    String nombresAgotados = resultado.substring("agotado:".length());
                    response.sendRedirect(request.getContextPath() +
                        "/public/Administrador/Cultivos_Registrados.jsp?mensaje=registrado&agotado=" +
                        java.net.URLEncoder.encode(nombresAgotados, "UTF-8"));

                } else if (resultado.startsWith("insuficiente:")) {
                    String detalle = resultado.substring("insuficiente:".length());
                    response.sendRedirect(request.getContextPath() +
                        "/public/Administrador/Registro_Cultivos.jsp?error=insuficiente&detalle=" +
                        java.net.URLEncoder.encode(detalle, "UTF-8"));

                } else {
                    response.sendRedirect(request.getContextPath() +
                        "/public/Administrador/Registro_Cultivos.jsp?error=true");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                "/public/Administrador/Registro_Cultivos.jsp?error=excepcion");
        }
    }
}