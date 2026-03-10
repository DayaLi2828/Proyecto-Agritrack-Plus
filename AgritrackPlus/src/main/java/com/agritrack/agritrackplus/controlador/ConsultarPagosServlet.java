package com.agritrack.agritrackplus.controlador;

import com.agritrack.agritrackplus.DAO.PagoDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/ConsultarPagosServlet")
public class ConsultarPagosServlet extends HttpServlet {

    /**
     * Procesa las peticiones GET para buscar tareas de un trabajador específico.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Configurar la respuesta como JSON y con codificación UTF-8
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // 2. Obtener el parámetro de búsqueda (Nombre o Documento)
        String criterio = request.getParameter("criterio");
        PrintWriter out = response.getWriter();
        
        // 3. Validar que el criterio no sea nulo o vacío
        if (criterio == null || criterio.trim().isEmpty()) {
            out.print("[]"); // Devolvemos un array vacío si no hay búsqueda
            return;
        }

        try {
            // 4. Consultar al DAO (Asegúrate de tener el PagoDAO que creamos antes)
            PagoDAO dao = new PagoDAO();
            List<Map<String, String>> tareas = dao.buscarTareasPorTrabajador(criterio);

            // 5. Construir el JSON manualmente (Estructura: [{id, tarea, jornada, estado}, ...])
            StringBuilder json = new StringBuilder();
            json.append("[");
            
            for (int i = 0; i < tareas.size(); i++) {
                Map<String, String> t = tareas.get(i);
                json.append("{");
                json.append("\"id\":\"").append(t.get("id")).append("\",");
                json.append("\"tarea\":\"").append(t.get("tarea").replace("\"", "\\\"")).append("\",");
                json.append("\"jornada\":\"").append(t.get("jornada")).append("\",");
                json.append("\"estado\":\"").append(t.get("estado")).append("\"");
                json.append("}");
                
                if (i < tareas.size() - 1) {
                    json.append(",");
                }
            }
            
            json.append("]");

            // 6. Enviar la respuesta al cliente
            out.print(json.toString());
            
        } catch (Exception e) {
            e.printStackTrace();
            // En caso de error crítico, devolvemos un array vacío para no romper el JS
            out.print("[]");
        } finally {
            out.close();
        }
    }
}