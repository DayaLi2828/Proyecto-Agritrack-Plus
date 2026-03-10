package com.agritrack.agritrackplus.DAO;

import com.agritrack.agritrackplus.db.Conexion;
import java.sql.*;
import java.util.*;

public class PagoDAO {

    public List<Map<String, String>> buscarTareasPorTrabajador(String criterio) {
        List<Map<String, String>> tareas = new ArrayList<>();
        // SQL actualizado para filtrar SOLO completadas
        String sql = "SELECT ut.id, t.nombre AS tarea, ut.estado, ut.jornada " +
                     "FROM usuario_tarea ut " +
                     "JOIN usuarios u ON ut.usuario_id = u.id " +
                     "JOIN tareas t ON ut.tarea_id = t.id " +
                     "WHERE (u.nombre LIKE ? OR u.documento = ?) " +
                     "AND ut.estado = 'Completada'"; // <-- CAMBIO AQUÍ

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
                tarea.put("jornada", rs.getString("jornada"));
                tareas.add(tarea);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tareas;
    }
}