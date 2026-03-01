package com.agritrack.agritrackplus.controlador;

import com.agritrack.agritrackplus.DAO.UsuarioDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet(name = "CambiarEstadoServlet", urlPatterns = {"/CambiarEstadoServlet"})
public class CambiarEstadoServlet extends HttpServlet {

   @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Evitar que cualquier filtro previo ensucie la respuesta
        if (response.isCommitted()) return;

        String idStr = request.getParameter("id");
        UsuarioDAO dao = new UsuarioDAO();

        try {
            if (idStr != null && !idStr.isEmpty()) {
                Map<String, String> usuario = dao.obtenerPorId(idStr);
                if (usuario != null) {
                    String nuevoEstado = "Activo".equalsIgnoreCase(usuario.get("estado")) ? "Inactivo" : "Activo";
                    dao.actualizarEstado(Integer.parseInt(idStr), nuevoEstado);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. REDIRECCIÓN MANUAL (Más segura que sendRedirect en algunos entornos)
        response.setStatus(HttpServletResponse.SC_SEE_OTHER);
        response.setHeader("Location", request.getContextPath() + "/public/Administrador/Usuarios.jsp?mensaje=estado");
        return; //  AQUÍ SE TERMINA TODO. No puede haber nada más abajo.
    }
}