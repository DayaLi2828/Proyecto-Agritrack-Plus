<%@ page import="java.sql.*" %>
<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String criterio = request.getParameter("usuario");
    if (criterio == null) criterio = "";

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
        // USAMOS LA RUTA COMPLETA DE TU PAQUETE DIRECTAMENTE
        conn = com.agritrack.agritrackplus.db.Conexion.getConexion(); 

        String sql = "SELECT p.id, p.pago, p.fecha_pago, u.nombre, " +
             "MAX(r.nombre) AS rol_nombre " +  // ← evita duplicados por múltiples roles
             "FROM pagos p " +
             "INNER JOIN usuarios u ON p.usuario_id = u.id " +
             "INNER JOIN roles_usuarios ru ON u.id = ru.usuario_id " +
             "INNER JOIN roles r ON ru.rol_id = r.id " +
             "WHERE (u.nombre LIKE ? OR u.documento LIKE ?) " +
             "GROUP BY p.id, p.pago, p.fecha_pago, u.nombre " +  // ← agrupa para no duplicar
             "ORDER BY p.fecha_pago DESC";

        ps = conn.prepareStatement(sql);
        ps.setString(1, "%" + criterio + "%");
        ps.setString(2, "%" + criterio + "%");
        rs = ps.executeQuery();

        StringBuilder json = new StringBuilder();
        json.append("[");
        boolean primero = true;
        while (rs.next()) {
            if (!primero) json.append(",");
            json.append("{")
                .append("\"id\":").append(rs.getInt("id")).append(",")
                .append("\"total\":").append(rs.getDouble("pago")).append(",")
                .append("\"fecha\":\"").append(rs.getString("fecha_pago")).append("\",")
                .append("\"nombre\":\"").append(rs.getString("nombre")).append("\",")
                .append("\"rol\":\"").append(rs.getString("rol_nombre")).append("\"")
                .append("}");
            primero = false;
        }
        json.append("]");
        out.print(json.toString());

    } catch (Exception e) {
        response.setStatus(500);
        out.print("[]");
        e.printStackTrace(); 
    } finally {
        try { if (rs != null) rs.close(); } catch (Exception e) {}
        try { if (ps != null) ps.close(); } catch (Exception e) {}
        try { if (conn != null) conn.close(); } catch (Exception e) {}
    }
%>