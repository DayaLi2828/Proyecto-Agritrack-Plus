<%@ page import="com.agritrack.agritrackplus.DAO.PagoDAO" %>
<%@ page import="java.util.*" %>
<%@ page contentType="application/json; charset=UTF-8" %>
<%
    // Usamos "usuario" como parámetro para diferenciarlo de "criterio" de tareas si prefieres
    String criterio = request.getParameter("usuario");
    
    if (criterio == null || criterio.trim().isEmpty()) {
        out.print("[]");
        return;
    }

    PagoDAO dao = new PagoDAO();
    // Este método lo crearemos en el siguiente paso
    List<Map<String, String>> facturas = dao.buscarHistorialPorTrabajador(criterio);

    StringBuilder json = new StringBuilder("[");
    for (int i = 0; i < facturas.size(); i++) {
        Map<String, String> f = facturas.get(i);
        
        json.append("{");
        json.append("\"id\":\"").append(f.get("id")).append("\",");
        json.append("\"fecha\":\"").append(f.get("fecha")).append("\",");
        json.append("\"total\":").append(f.get("total")).append(","); // Total suele ser número
        json.append("\"trabajador\":\"").append(f.get("trabajador").replace("\"", "\\\"")).append("\"");
        json.append("}");
        
        if (i < facturas.size() - 1) json.append(",");
    }
    json.append("]");

    out.print(json.toString());
%>