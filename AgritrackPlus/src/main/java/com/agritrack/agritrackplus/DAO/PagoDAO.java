package com.agritrack.agritrackplus.DAO;

import com.agritrack.agritrackplus.db.Conexion;
import java.sql.*;
import java.util.*;

public class PagoDAO {

    public List<Map<String, String>> buscarTareasPorTrabajador(String criterio) {
        List<Map<String, String>> tareas = new ArrayList<>();
        // Usamos ILIKE o verificamos que el estado sea similar a lo que buscamos
        // Si quieres permitir 'En Proceso' para el pago del 50%, quita el filtro estricto
        String sql = "SELECT ut.id, t.nombre AS tarea, ut.estado, ut.jornada " +
                     "FROM usuario_tarea ut " +
                     "JOIN usuarios u ON ut.usuario_id = u.id " +
                     "JOIN tareas t ON ut.tarea_id = t.id " +
                     "WHERE (u.nombre LIKE ? OR u.documento = ?) " +
                     "AND ut.estado IN ('Completada', 'Completado', 'En Proceso')"; 

        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + criterio + "%");
            ps.setString(2, criterio);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, String> tarea = new HashMap<>();
                tarea.put("id", rs.getString("id"));
                tarea.put("tarea", rs.getString("tarea"));
                tarea.put("estado", rs.getString("estado"));

                // Verificación de seguridad para la jornada
                String jornada = rs.getString("jornada");
                tarea.put("jornada", (jornada != null) ? jornada : "Medio Dia");

                tareas.add(tarea);
            }
        } catch (SQLException e) {
            System.err.println("Error en PagoDAO: " + e.getMessage());
        }
        return tareas;
    }
    public List<Map<String, String>> buscarHistorialPorTrabajador(String criterio) {
        List<Map<String, String>> historial = new ArrayList<>();
        // Ajusta los nombres de las columnas según tu tabla 'pagos' o 'facturas'
        String sql = "SELECT id_pago, fecha_pago, total_pagado, trabajador_nombre FROM pagos " +
                     "WHERE trabajador_nombre LIKE ? OR trabajador_doc = ? ORDER BY fecha_pago DESC";

        try (Connection con = Conexion.getConnection(); // Usa tu clase Conexion
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + criterio + "%");
            ps.setString(2, criterio);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> f = new HashMap<>();
                    f.put("id", String.valueOf(rs.getInt("id_pago")));
                    f.put("fecha", rs.getString("fecha_pago"));
                    f.put("total", String.valueOf(rs.getDouble("total_pagado")));
                    f.put("trabajador", rs.getString("trabajador_nombre"));
                    historial.add(f);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return historial;
    }
    public boolean registrarPago(String nombre, String documento, double total) {
        String sql = "INSERT INTO pagos (trabajador_nombre, trabajador_doc, fecha_pago, total_pagado) VALUES (?, ?, CURDATE(), ?)";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, documento);
            ps.setDouble(3, total);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}