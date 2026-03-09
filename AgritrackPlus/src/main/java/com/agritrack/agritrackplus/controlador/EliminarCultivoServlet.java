package com.agritrack.agritrackplus.controlador;

import com.agritrack.agritrackplus.DAO.Registro_CultivoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "EliminarCultivoServlet", urlPatterns = {"/EliminarCultivoServlet"})
public class EliminarCultivoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Obtener el ID del cultivo a eliminar
        String idStr = request.getParameter("id");
        
        if (idStr != null && !idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                Registro_CultivoDAO dao = new Registro_CultivoDAO();
                
                // 2. Llamar al método de eliminación del DAO
                boolean eliminado = dao.eliminarCultivo(id);
                
                if (eliminado) {
                    System.out.println("DEBUG: Cultivo " + id + " eliminado con éxito.");
                }
            } catch (NumberFormatException e) {
                System.err.println("Error de formato en ID: " + e.getMessage());
            }
        }

        // 3. Redirigir de vuelta a la lista con el mensaje para la alerta
        response.sendRedirect(request.getContextPath() + "/public/Administrador/Cultivos_Registrados.jsp?mensaje=eliminado");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirección de seguridad si intentan entrar por URL
        response.sendRedirect(request.getContextPath() + "/public/Administrador/Cultivos_Registrados.jsp");
    }
}