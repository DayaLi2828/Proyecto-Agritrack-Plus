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
                int idUsuario = (int) user.get("id");
                String rol = (String) user.get("rol");

                // Datos comunes guardados en sesión
                session.setAttribute("usuario_id", idUsuario);
                session.setAttribute("usuario_nombre", user.get("nombre"));
                session.setAttribute("rol", rol);

                // --- Lógica de redirección por Roles ---

                if ("trabajador".equalsIgnoreCase(rol)) {
                    session.setAttribute("datosGrafico", dao.obtenerResumenTareas(idUsuario));
                    response.sendRedirect(request.getContextPath() + "/public/Trabajador/Trabajador.jsp");
                } 
                else if ("administrador".equalsIgnoreCase(rol)) {
                    // El Administrador va a su panel global
                    response.sendRedirect(request.getContextPath() + "/public/Administrador/Admin.jsp");
                } 
                else if ("supervisor".equalsIgnoreCase(rol)) {
                    // El Supervisor va a su nueva página dedicada
                    response.sendRedirect(request.getContextPath() + "/public/Supervisor/Supervisor.jsp");
                } 
                else {
                    // Si el rol no coincide con ninguno de los anteriores
                    response.sendRedirect(request.getContextPath() + "/index.jsp?error=role");
                }
            } else {
                // Usuario o contraseña incorrectos
                response.sendRedirect(request.getContextPath() + "/index.jsp?error=true");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/index.jsp?error=db");
        }
    }
}