<%@ page import="com.agritrack.agritrackplus.DAO.TareaDAO" %>
<%@ page import="com.agritrack.agritrackplus.modelo.Tarea" %>
<%@ page import="java.util.List" %>
<%@ page contentType="application/json; charset=UTF-8" %>
<%
    String cultivoIdStr = request.getParameter("cultivoId");
    if (cultivoIdStr == null || cultivoIdStr.trim().isEmpty()) {
        out.print("[]");
        return;
    }
    TareaDAO dao = new TareaDAO();
    List<Tarea> trabajadores = dao.listarTrabajadoresPorCultivo(Integer.parseInt(cultivoIdStr));
    StringBuilder json = new StringBuilder("[");
    for (int i = 0; i < trabajadores.size(); i++) {
        Tarea t = trabajadores.get(i);
        json.append("{");
        json.append("\"id\":").append(t.getId()).append(",");
        json.append("\"nombre\":\"").append(t.getNombreTrabajador()).append("\"");
        json.append("}");
        if (i < trabajadores.size() - 1) json.append(",");
    }
    json.append("]");
    out.print(json.toString());
%>