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
        
        request.setCharacterEncoding("UTF-8");
        String idStr = request.getParameter("id");
        
        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                int usuarioId = Integer.parseInt(idStr);
                UsuarioDAO dao = new UsuarioDAO();
                
                Map<String, String> usuario = dao.obtenerPorId(idStr);
                if (usuario != null) {
                    String estadoActual = usuario.get("estado");
                    String nuevoEstado = "Activo".equals(estadoActual) ? "Inactivo" : "Activo";
                    
                    boolean exito = dao.actualizarEstado(usuarioId, nuevoEstado);
                    
                    if (exito) {
                        response.sendRedirect(request.getContextPath() + "/public/Administrador/Usuarios.jsp?mensaje=estado");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Usuarios.jsp");
        }
    }
}
