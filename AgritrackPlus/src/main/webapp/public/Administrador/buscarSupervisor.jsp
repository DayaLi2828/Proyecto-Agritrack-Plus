<%@ page import="com.agritrack.agritrackplus.DAO.PagoDAO" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page contentType="application/json; charset=UTF-8" %>
<%
    String criterio = request.getParameter("criterio");

    if (criterio == null || criterio.trim().isEmpty()) {
        out.print("{\"error\":\"sin criterio\"}");
        return;
    }

    PagoDAO dao = new PagoDAO();
    Map<String, String> supervisor = dao.buscarSupervisor(criterio);

    if (supervisor == null || supervisor.isEmpty()) {
        out.print("{\"error\":\"no encontrado\"}");
        return;
    }

    List<Map<String, String>> tareas = dao.buscarTareasPorSupervisor(criterio);

    StringBuilder json = new StringBuilder("{");
    json.append("\"nombre\":\"")
        .append(supervisor.get("nombre")
            .replace("\\", "\\\\")
            .replace("\"", "\\\""))
        .append("\",");
    json.append("\"totalCultivos\":\"").append(supervisor.get("totalCultivos")).append("\",");
    json.append("\"tareas\":[");
    for (int i = 0; i < tareas.size(); i++) {
        Map<String, String> t = tareas.get(i);
        String cultivo = t.get("cultivo") != null ? t.get("cultivo").replace("\\", "\\\\").replace("\"", "\\\"") : "";
        String tarea   = t.get("tarea")   != null ? t.get("tarea").replace("\\", "\\\\").replace("\"", "\\\"")   : "";
        String estado  = t.get("estado")  != null ? t.get("estado")  : "";
        json.append("{");
        json.append("\"cultivo\":\"").append(cultivo).append("\",");
        json.append("\"tarea\":\"").append(tarea).append("\",");
        json.append("\"estado\":\"").append(estado).append("\"");
        json.append("}");
        if (i < tareas.size() - 1) json.append(",");
    }
    json.append("]}");
    out.print(json.toString());
%>