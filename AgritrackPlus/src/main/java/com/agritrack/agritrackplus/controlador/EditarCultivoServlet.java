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
        String estado = (request.getParameter("estado") != null) ? request.getParameter("estado") : "Activo";

        String supervisorIdStr = request.getParameter("supervisor_id");
        int supervisorId = (supervisorIdStr != null && !supervisorIdStr.isEmpty())
                           ? Integer.parseInt(supervisorIdStr) : 0;

        String[] productoIds = request.getParameterValues("producto_id");
        String[] cantidades = request.getParameterValues("cantidad_producto");
        String[] trabajadoresIds = request.getParameterValues("trabajadores");

        Registro_CultivoDAO dao = new Registro_CultivoDAO();

        try {
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
                    "/public/Administrador/Editar_Cultivo.jsp?id=" + id + "&error=insuficiente&detalle=" +
                    java.net.URLEncoder.encode(detalle, "UTF-8"));

            } else {
                response.sendRedirect(request.getContextPath() +
                    "/public/Administrador/Editar_Cultivo.jsp?id=" + id + "&error=true");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                "/public/Administrador/Editar_Cultivo.jsp?id=" + id + "&error=excepcion");
        }
    }
}