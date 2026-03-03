package com.agritrack.agritrackplus.controlador;

import com.agritrack.agritrackplus.DAO.UsuarioDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ActualizarPerfilServlet", urlPatterns = {"/ActualizarPerfilServlet"})
public class ActualizarPerfilServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Obtener la sesión para validar el ID del usuario logueado
        HttpSession session = request.getSession();
        Integer usuarioId = (Integer) session.getAttribute("id_usuario"); 

        // 2. Capturar los datos del formulario de la tarjeta
        String nombre = request.getParameter("nombre");
        String documento = request.getParameter("documento");
        String direccion = request.getParameter("direccion");
        String correo = request.getParameter("correo");
        String telefono = request.getParameter("telefono");
        String nuevaPass = request.getParameter("pass"); // Puede venir vacío

        // 3. Validar que el ID exista (por seguridad)
        if (usuarioId == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        UsuarioDAO dao = new UsuarioDAO();
        
        // 4. Llamar al método en español que creamos en el DAO
        boolean exito = dao.actualizarPerfil(usuarioId, nombre, documento, direccion, nuevaPass, correo, telefono);

        // 5. Redirigir según el resultado
        if (exito) {
            // Si el nombre cambió, actualizamos también el nombre en la sesión
            session.setAttribute("nombre_usuario", nombre);
            
            // Redirigir a la interfaz que corresponda (Trabajador o Supervisor)
            // Puedes usar request.getContextPath() para evitar errores de ruta
            response.sendRedirect(request.getContextPath() + "/public/Trabajador/Trabajador.jsp?update=success");
        } else {
            response.sendRedirect(request.getContextPath() + "/public/Trabajador/Trabajador.jsp?update=error");
        }
    }
}