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

        String[] trabajadores = request.getParameterValues("trabajadores[]");
        String[] productos = request.getParameterValues("productos[]");

        if (fechaCosecha == null || fechaCosecha.trim().isEmpty()) {
            fechaCosecha = null;
        }

        Registro_CultivoDAO dao = new Registro_CultivoDAO();

        boolean actualizado = dao.editar(id, nombre, fechaSiembra, fechaCosecha, ciclo, estado);

        if (actualizado) {

            int idCultivo = Integer.parseInt(id);

            if (trabajadores != null) {
                dao.eliminarTrabajadoresCultivo(idCultivo);

                for (int i = 0; i < trabajadores.length; i++) {
                    dao.asignarTrabajador(idCultivo, Integer.parseInt(trabajadores[i]));
                }
            }

            if (productos != null) {
                dao.eliminarProductosCultivo(idCultivo);

                for (int i = 0; i < productos.length; i++) {
                    dao.asignarProducto(idCultivo, Integer.parseInt(productos[i]));
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