package com.agritrack.agritrackplus.controlador;

import com.agritrack.agritrackplus.DAO.UsuarioDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Map;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String pass = request.getParameter("password");

        try {
            UsuarioDAO dao = new UsuarioDAO();
            Map<String, Object> user = dao.validarAcceso(email, pass);

            if (user != null) {
                HttpSession session = request.getSession();
                
                // 1. Guardamos datos básicos en sesión
                int idUsuario = (int) user.get("id");
                session.setAttribute("usuario_id", idUsuario);
                session.setAttribute("usuario_nombre", user.get("nombre"));
                session.setAttribute("rol", user.get("rol"));

                String rol = (String) user.get("rol");

                // 2. Lógica específica para TRABAJADOR (Cargar datos de gráficas)
                if ("trabajador".equalsIgnoreCase(rol)) {
                    // Llamamos al método que añadiste en UsuarioDAO
                    Map<String, Integer> estadisticas = dao.obtenerResumenTareas(idUsuario);
                    
                    // Guardamos el mapa en la sesión para que el JSP lo lea al cargar
                    session.setAttribute("datosGrafico", estadisticas);
                    
                    response.sendRedirect(request.getContextPath() + "/public/Trabajador/Trabajador.jsp");
                } 
                
                // 3. Lógica para ADMINISTRADOR
                else if ("administrador".equalsIgnoreCase(rol)) {
                    response.sendRedirect(request.getContextPath() + "/public/Administrador/Admin.jsp");
                } 
                
                // 4. Lógica para SUPERVISOR
                else if ("supervisor".equalsIgnoreCase(rol)) {
                    response.sendRedirect(request.getContextPath() + "/public/Administrador/Admin.jsp");
                } 
                
                else {
                    response.sendRedirect(request.getContextPath() + "/index.jsp?error=role");
                }
                
            } else {
                response.sendRedirect(request.getContextPath() + "/index.jsp?error=true");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/index.jsp?error=db");
        }
    }
}