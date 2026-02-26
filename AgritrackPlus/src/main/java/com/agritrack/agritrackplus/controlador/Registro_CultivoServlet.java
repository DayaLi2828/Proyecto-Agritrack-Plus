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

        String nombre = request.getParameter("nombre");
        String fechaSiembra = request.getParameter("fecha_siembra");
        String ciclo = request.getParameter("ciclo");
        String estado = request.getParameter("estado");
        String supervisorId = request.getParameter("supervisor_id");
        String[] productoIds = request.getParameterValues("producto_id[]");
        String[] cantidades = request.getParameterValues("cantidad_producto[]");
        String[] trabajadoresIds = request.getParameterValues("trabajadores[]");

        Registro_CultivoDAO dao = new Registro_CultivoDAO();
        int cultivoId = dao.registrar(nombre, fechaSiembra, ciclo, estado);

        if (cultivoId > 0) {
            if (supervisorId != null && !supervisorId.isEmpty()) {
                dao.asignarSupervisor(cultivoId, Integer.parseInt(supervisorId));
            }
            if (productoIds != null) {
                for (int i = 0; i < productoIds.length; i++) {
                    if (productoIds[i] != null && !productoIds[i].isEmpty()) {
                        int cantidad = (cantidades != null && i < cantidades.length) ? Integer.parseInt(cantidades[i]) : 1;
                        dao.asignarProducto(cultivoId, Integer.parseInt(productoIds[i]), cantidad);
                    }
                }
            }
            if (trabajadoresIds != null) {
                for (String trabajadorId : trabajadoresIds) {
                    if (trabajadorId != null && !trabajadorId.isEmpty()) {
                        dao.asignarTrabajador(cultivoId, Integer.parseInt(trabajadorId));
                    }
                }
            }
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Cultivos_Registrados.jsp?registro=exitoso");
        } else {
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Registro_Cultivos.jsp?error=true");
        }
    }
}