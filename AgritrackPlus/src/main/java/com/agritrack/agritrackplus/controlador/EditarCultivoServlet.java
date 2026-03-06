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

        // Nota: Asegúrate que en tu HTML los select tengan name="trabajadores" y name="productos"
        String[] trabajadores = request.getParameterValues("trabajadores");
        String[] productos = request.getParameterValues("productos");

        if (fechaCosecha == null || fechaCosecha.trim().isEmpty()) {
            fechaCosecha = null;
        }

        Registro_CultivoDAO dao = new Registro_CultivoDAO();

        // 1. Actualizar datos básicos del cultivo
        boolean actualizado = dao.editar(id, nombre, fechaSiembra, fechaCosecha, ciclo, estado);

        if (actualizado) {
            int idCultivo = Integer.parseInt(id);

            // 2. Actualizar trabajadores (Borrar y volver a insertar)
            if (trabajadores != null) {
                dao.eliminarTrabajadoresCultivo(idCultivo);
                for (String tId : trabajadores) {
                    if (tId != null && !tId.isEmpty()) {
                        dao.asignarTrabajador(idCultivo, Integer.parseInt(tId));
                    }
                }
            }

            // 3. Actualizar productos/insumos (Borrar y volver a insertar)
           // 3. Actualizar productos/insumos (Borrar y volver a insertar)
            if (productos != null) {
                dao.eliminarProductosCultivo(idCultivo);
                for (String pId : productos) {
                    if (pId != null && !pId.isEmpty()) {
                        // USAMOS pId (que viene del for) e idCultivo (que definiste arriba)
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