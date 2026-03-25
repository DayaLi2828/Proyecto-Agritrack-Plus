<%@ page import="com.agritrack.agritrackplus.DAO.PagoDAO" %>
<%@ page contentType="application/json; charset=UTF-8" %>
<%
    String nombre = request.getParameter("nombre");
    String documento = request.getParameter("documento");
    String totalStr = request.getParameter("total");

    // Validamos que el documento no sea nulo, es más confiable que el nombre
    if (documento == null || totalStr == null || documento.trim().isEmpty()) {
        out.print("{\"status\":\"error\", \"message\":\"Documento o total faltantes\"}");
        return;
    }

    try {
        // Limpiamos el total por si viene con símbolos o comas
        double total = Double.parseDouble(totalStr.replace(",", "."));
        
        PagoDAO dao = new PagoDAO();
        
        // IMPORTANTE: Asegúrate de que tu método registrarPago use el documento 
        // para buscar el ID del usuario 6 (Elena) antes de hacer el INSERT.
        boolean guardado = dao.registrarPago(nombre, documento, total);

        if (guardado) {
            out.print("{\"status\":\"success\"}");
        } else {
            // Si llega aquí, es porque el DAO devolvió false (posiblemente no encontró al usuario)
            out.print("{\"status\":\"error\", \"message\":\"No se encontró al usuario con documento " + documento + "\"}");
        }
    } catch (NumberFormatException e) {
        out.print("{\"status\":\"error\", \"message\":\"El formato del total es inválido\"}");
    } catch (Exception e) {
        out.print("{\"status\":\"error\", \"message\":\"" + e.getMessage().replace("\"", "'") + "\"}");
    }
%>