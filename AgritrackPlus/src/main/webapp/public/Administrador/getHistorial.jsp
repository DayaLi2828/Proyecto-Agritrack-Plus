<%@ page import="com.agritrack.agritrackplus.DAO.PagoDAO" %>
<%@ page import="java.util.*" %>
<%@ page contentType="application/json; charset=UTF-8" %>
<%
    String criterio = request.getParameter("usuario");

    if (criterio == null || criterio.trim().isEmpty()) {
        out.print("[]");
        return;
    }

    PagoDAO dao = new PagoDAO();
    List<Map<String, String>> facturas = dao.buscarHistorialPorTrabajador(criterio);

    StringBuilder json = new StringBuilder("[");
    for (int i = 0; i < facturas.size(); i++) {
        Map<String, String> f = facturas.get(i);

        String monto            = (f.get("total")      != null && !f.get("total").isEmpty()) ? f.get("total")      : "0";
        String idFactura        = (f.get("id")          != null)                              ? f.get("id")         : "0";
        String fechaPago        = (f.get("fecha")       != null)                              ? f.get("fecha")      : "Sin fecha";
        String nombreTrabajador = (f.get("trabajador")  != null)                              ? f.get("trabajador") : "Desconocido";
        String rol              = (f.get("rol")         != null)                              ? f.get("rol")        : "trabajador";

        json.append("{");
        json.append("\"id\":\"").append(idFactura).append("\",");
        json.append("\"fecha\":\"").append(fechaPago).append("\",");
        json.append("\"total\":").append(monto).append(",");
        json.append("\"trabajador\":\"").append(nombreTrabajador.replace("\"", "\\\"")).append("\",");
        json.append("\"rol\":\"").append(rol).append("\"");
        json.append("}");

        if (i < facturas.size() - 1) json.append(",");
    }
    json.append("]");
    out.print(json.toString());
%>