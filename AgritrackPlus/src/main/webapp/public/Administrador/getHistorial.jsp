<%@ page import="com.agritrack.agritrackplus.DAO.PagoDAO" %>
<%@ page import="java.util.*" %>
<%@ page contentType="application/json; charset=UTF-8" %>
<%
    // Obtener el nombre o documento enviado desde el historial
    String criterio = request.getParameter("usuario");
    
    // Si no hay criterio, devolvemos un array vacío para no dar error en el fetch
    if (criterio == null || criterio.trim().isEmpty()) {
        out.print("[]");
        return;
    }

    PagoDAO dao = new PagoDAO();
    // Este método debe devolver List<Map<String, String>> en el DAO
    List<Map<String, String>> facturas = dao.buscarHistorialPorTrabajador(criterio);

    StringBuilder json = new StringBuilder("[");
    for (int i = 0; i < facturas.size(); i++) {
        Map<String, String> f = facturas.get(i);
        
        // CORRECCIÓN: Validar que el total no sea nulo para no romper el formato JSON
        String monto = (f.get("total") != null && !f.get("total").isEmpty()) ? f.get("total") : "0";
        // CORRECCIÓN: Validar que el ID y fecha existan
        String idFactura = (f.get("id") != null) ? f.get("id") : "0";
        String fechaPago = (f.get("fecha") != null) ? f.get("fecha") : "Sin fecha";
        String nombreTrabajador = (f.get("trabajador") != null) ? f.get("trabajador") : "Desconocido";

        json.append("{");
        json.append("\"id\":\"").append(idFactura).append("\",");
        json.append("\"fecha\":\"").append(fechaPago).append("\",");
        json.append("\"total\":").append(monto).append(","); 
        json.append("\"trabajador\":\"").append(nombreTrabajador.replace("\"", "\\\"")).append("\"");
        json.append("}");
        
        // Si no es el último elemento, añadimos una coma
        if (i < facturas.size() - 1) {
            json.append(",");
        }
    }
    json.append("]");

    // Enviamos el JSON final al cliente
    out.print(json.toString());
%>