<%@ page import="com.agritrack.agritrackplus.DAO.PagoDAO" %>
<%@ page import="java.util.*" %>
<%@ page contentType="application/json; charset=UTF-8" %>
<%
    String criterio = request.getParameter("criterio");
    
    if (criterio == null || criterio.trim().isEmpty()) {
        out.print("[]");
        return;
    }

    PagoDAO dao = new PagoDAO();
    List<Map<String, String>> tareas = dao.buscarTareasPorTrabajador(criterio);
    StringBuilder json = new StringBuilder("[");

    for (int i = 0; i < tareas.size(); i++) {
        Map<String, String> t = tareas.get(i);

        String nombreTarea = t.get("tarea") != null ? t.get("tarea").replace("\"", "\\\"") : "";
        String jornada     = t.get("jornada")  != null ? t.get("jornada")  : "Medio Dia";
        String estado      = t.get("estado")   != null ? t.get("estado")   : "";
        String cultivo     = t.get("cultivo")  != null ? t.get("cultivo").replace("\"", "\\\"") : "";

        json.append("{");
        json.append("\"id\":\"").append(t.get("id")).append("\",");
        json.append("\"tarea\":\"").append(nombreTarea).append("\",");
        json.append("\"estado\":\"").append(estado).append("\",");
        json.append("\"jornada\":\"").append(jornada).append("\",");
        json.append("\"cultivo\":\"").append(cultivo).append("\"");
        json.append("}");

        if (i < tareas.size() - 1) json.append(",");
    }

    json.append("]");
    out.print(json.toString());
%>