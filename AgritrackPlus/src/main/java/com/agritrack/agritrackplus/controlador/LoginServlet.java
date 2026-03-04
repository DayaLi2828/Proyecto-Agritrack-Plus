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

        // 1. Capturar datos del formulario
        String email = request.getParameter("email");
        String pass = request.getParameter("password");

        // --- BLOQUE DE DEBUG (Míralo en la consola de tu IDE) ---
        System.out.println("---------- INTENTO DE LOGIN ----------");
        System.out.println("DEBUG: Email recibido del JSP: [" + email + "]");
        System.out.println("DEBUG: Password recibido del JSP: [" + pass + "]");
        // -------------------------------------------------------

        try {
            UsuarioDAO dao = new UsuarioDAO();
            Map<String, Object> user = dao.validarAcceso(email, pass);

            if (user != null) {
                // Si entra aquí, significa que el DAO encontró al usuario
                System.out.println("DEBUG: Usuario encontrado: " + user.get("nombre") + " con Rol: " + user.get("rol"));
                
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
                    response.sendRedirect(request.getContextPath() + "/public/Administrador/Admin.jsp");
                } 
                else if ("supervisor".equalsIgnoreCase(rol)) {
                    response.sendRedirect(request.getContextPath() + "/public/Supervisor/Supervisor.jsp");
                } 
                else {
                    System.out.println("DEBUG: El rol [" + rol + "] no tiene una redirección definida.");
                    response.sendRedirect(request.getContextPath() + "/index.jsp?error=role");
                }
            } else {
                // Si user es null, el DAO no encontró coincidencia en la BD
                System.out.println("DEBUG: Credenciales incorrectas o usuario Inactivo.");
                response.sendRedirect(request.getContextPath() + "/index.jsp?error=true");
            }
        } catch (Exception e) {
            System.err.println("DEBUG ERROR: Fallo en el proceso de Login.");
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/index.jsp?error=db");
        }
        System.out.println("--------------------------------------");
    }
}