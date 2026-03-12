<%@ page import="com.agritrack.agritrackplus.DAO.PagoDAO" %>
<%@ page contentType="application/json; charset=UTF-8" %>
<%
    String nombre = request.getParameter("nombre");
    String documento = request.getParameter("documento");
    String totalStr = request.getParameter("total");

    if (nombre == null || totalStr == null) {
        out.print("{\"status\":\"error\", \"message\":\"Datos incompletos\"}");
        return;
    }

    try {
        double total = Double.parseDouble(totalStr);
        PagoDAO dao = new PagoDAO();
        boolean guardado = dao.registrarPago(nombre, documento, total);

        if (guardado) {
            out.print("{\"status\":\"success\"}");
        } else {
            out.print("{\"status\":\"error\", \"message\":\"No se pudo insertar en la BD\"}");
        }
    } catch (Exception e) {
        out.print("{\"status\":\"error\", \"message\":\"" + e.getMessage() + "\"}");
    }
%>