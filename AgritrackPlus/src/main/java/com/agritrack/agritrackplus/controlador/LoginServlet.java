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

        // 1. Recibir parámetros del formulario en index.jsp
        String email = request.getParameter("email");
        String pass = request.getParameter("password");

        System.out.println("Intentando login con: " + email); 

        try {
            UsuarioDAO dao = new UsuarioDAO();
            // 2. Validar acceso con el método que usa JOIN
            Map<String, Object> user = dao.validarAcceso(email, pass);

            if (user != null) {
                System.out.println("Usuario encontrado: " + user.get("nombre")); 

                // 3. Crear sesión y guardar datos del usuario
                HttpSession session = request.getSession();
                session.setAttribute("usuario_id", user.get("id"));
                session.setAttribute("usuario_nombre", user.get("nombre"));
                session.setAttribute("rol", user.get("rol"));
                session.setAttribute("permisos", user.get("permisos"));

                String rol = (String) user.get("rol");

                // 4. Redirección según el ROL (Ignorando Mayúsculas/Minúsculas)
                if ("administrador".equalsIgnoreCase(rol) || "supervisor".equalsIgnoreCase(rol)) {
                    // Ruta hacia tu carpeta public/Administrador
                    response.sendRedirect("public/Administrador/dashboard.jsp");
                } else {
                    // Ruta hacia tu carpeta de Trabajador
                    response.sendRedirect("public/Trabajador/Trabajador.jsp");
                }
                
            } else {
                // 5. Si las credenciales no coinciden, volver al index.jsp
                System.out.println("Credenciales incorrectas.");
                response.sendRedirect("index.jsp?error=true");
            }
            
        } catch (Exception e) {
            // 6. Manejo de errores de base de datos o nulos
            System.out.println("ERROR CRÍTICO EN LOGIN: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("index.jsp?error=db");
        }
    }
}