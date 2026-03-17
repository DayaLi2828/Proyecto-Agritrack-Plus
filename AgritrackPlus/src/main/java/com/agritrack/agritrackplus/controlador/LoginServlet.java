package com.agritrack.agritrackplus.controlador;

import com.agritrack.agritrackplus.DAO.TareaDAO;
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

        System.out.println("---------- INTENTO DE LOGIN ----------");

        try {
            UsuarioDAO dao = new UsuarioDAO();
            Map<String, Object> user = dao.validarAcceso(email, pass);

            if (user != null) {
                HttpSession session = request.getSession();
                int idUsuario = (int) user.get("id");
                String rol = (String) user.get("rol");

                // Datos básicos de sesión
                session.setAttribute("usuario_id", idUsuario);
                session.setAttribute("usuario_nombre", user.get("nombre"));
                session.setAttribute("rol", rol);

                // --- CARGA DE DATOS PARA TRABAJADOR ---
                if ("trabajador".equalsIgnoreCase(rol)) {
                    // Usamos TareaDAO para obtener las estadísticas frescas
                    TareaDAO tareaDAO = new TareaDAO();
                    
                    // 1. Resumen de tareas (Líneas)
                    session.setAttribute("datosGrafico", tareaDAO.obtenerConteosPorEstado(idUsuario));
                    
                    // 2. Cumplimiento por cultivo (Barras)
                    session.setAttribute("datosCultivos", tareaDAO.obtenerCumplimientoCultivos(idUsuario));
                    
                    // 3. Resumen de pagos (Dona)
                    session.setAttribute("datosPago", tareaDAO.obtenerResumenPagos(idUsuario));

                    response.sendRedirect(request.getContextPath() + "/public/Trabajador/Trabajador.jsp");
                } 
                else if ("administrador".equalsIgnoreCase(rol)) {
                    response.sendRedirect(request.getContextPath() + "/public/Administrador/Admin.jsp");
                } 
                else if ("supervisor".equalsIgnoreCase(rol)) {
                    response.sendRedirect(request.getContextPath() + "/public/Supervisor/Supervisor.jsp");
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