package com.agritrack.agritrackplus.DAO;

import com.agritrack.agritrackplus.db.Conexion;
import java.sql.*;
import java.util.*;

public class PagoDAO {

    public List<Map<String, String>> buscarTareasPorSupervisor(String criterio) {
        List<Map<String, String>> lista = new ArrayList<>();
        String sql = "SELECT c.nombre AS cultivo, t.nombre AS tarea, ut.estado " +
                     "FROM usuario_tarea ut " +
                     "JOIN cultivos c ON ut.cultivo_id = c.id " +
                     "JOIN tareas t ON ut.tarea_id = t.id " +
                     "JOIN usuarios u ON c.supervisor_id = u.id " +
                     "WHERE (u.nombre LIKE ? OR u.documento = ?) " +
                     "AND ut.estado IN ('Completada', 'En Proceso') " +
                     "ORDER BY c.nombre, t.nombre";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + criterio + "%");
            ps.setString(2, criterio);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> m = new HashMap<>();
                    m.put("cultivo", rs.getString("cultivo"));
                    m.put("tarea",   rs.getString("tarea"));
                    m.put("estado",  rs.getString("estado"));
                    lista.add(m);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }
    
    public List<Map<String, String>> buscarHistorialPorTrabajador(String criterio) {
        List<Map<String, String>> lista = new ArrayList<>();
        String sql = "SELECT p.id, p.fecha_pago, p.pago, u.nombre, r.nombre AS rol " +
                     "FROM pagos p " +
                     "JOIN usuarios u ON p.usuario_id = u.id " +
                     "JOIN roles_usuarios ru ON u.id = ru.usuario_id " +
                     "JOIN roles r ON ru.rol_id = r.id " +
                     "WHERE u.nombre LIKE ? OR u.documento LIKE ? " +
                     "ORDER BY p.id DESC";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String busqueda = "%" + criterio + "%";
            ps.setString(1, busqueda);
            ps.setString(2, busqueda);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> f = new HashMap<>();
                    f.put("id",         String.valueOf(rs.getInt("id")));
                    f.put("fecha",      rs.getString("fecha_pago"));
                    f.put("total",      String.valueOf(rs.getDouble("pago")));
                    f.put("trabajador", rs.getString("nombre"));
                    f.put("rol",        rs.getString("rol"));
                    lista.add(f);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    public boolean registrarPago(String nombre, String documento, double monto) {
        // 1. Declaramos la conexión fuera para que sea accesible
        Connection con = null; 

        // Consulta para obtener el ID
        String sqlBusqueda = "SELECT id FROM usuarios WHERE nombre = ?";
        // Consulta para insertar el pago
        String sqlInsert = "INSERT INTO pagos (usuario_id, fecha_pago, estado, pago) VALUES (?, CURDATE(), 'Activo', ?)";
        // Consulta para actualizar las tareas (LA QUE TE DIO ERROR)
        String sqlUpdateTareas = "UPDATE usuario_tarea SET estado = 'Pagado' " +
                         "WHERE usuario_id = ? AND estado IN ('Completada', 'En Proceso')";
        try {
            con = new Conexion().getConexion(); // <--- AQUÍ SE DEFINE 'con'
            int usuarioId = -1; // <--- AQUÍ SE DEFINE 'usuarioId'

            // PASO A: Buscar el ID del usuario
            try (PreparedStatement psBusqueda = con.prepareStatement(sqlBusqueda)) {
                psBusqueda.setString(1, nombre);
                try (ResultSet rs = psBusqueda.executeQuery()) {
                    if (rs.next()) {
                        usuarioId = rs.getInt("id");
                    }
                }
            }

            if (usuarioId == -1) return false;

            // PASO B: Insertar el registro de pago
            try (PreparedStatement psInsert = con.prepareStatement(sqlInsert)) {
                psInsert.setInt(1, usuarioId);
                psInsert.setDouble(2, monto);
                psInsert.executeUpdate();
            }

            // PASO C: Actualizar tareas a 'Pagado' (USANDO LAS VARIABLES YA DEFINIDAS)
            try (PreparedStatement psUpdate = con.prepareStatement(sqlUpdateTareas)) {
                psUpdate.setInt(1, usuarioId); // Ahora sí reconoce 'usuarioId'
                psUpdate.executeUpdate();
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            // Siempre cerrar la conexión manual si no usas try-with-resources en 'con'
            if (con != null) try { con.close(); } catch (SQLException e) {}
        }
    }
    public List<Map<String, Object>> obtenerHistorialPagos(String criterio) {
        List<Map<String, Object>> lista = new ArrayList<>();
        // Buscamos por nombre o documento uniendo con la tabla usuarios
        String sql = "SELECT p.id, p.fecha_pago, p.pago, p.estado " +
                     "FROM pagos p " +
                     "JOIN usuarios u ON p.usuario_id = u.id " +
                     "WHERE u.nombre LIKE ? OR u.documento LIKE ? " +
                     "ORDER BY p.fecha_pago DESC";

        try (Connection con = new Conexion().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String busqueda = "%" + criterio + "%";
            ps.setString(1, busqueda);
            ps.setString(2, busqueda);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> pago = new HashMap<>();
                    pago.put("id", rs.getInt("id"));
                    pago.put("fecha", rs.getString("fecha_pago"));
                    pago.put("total", rs.getDouble("pago"));
                    pago.put("estado", rs.getString("estado")); // Aquí vendrá 'Activo'
                    lista.add(pago);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    public Map<String, String> buscarSupervisor(String criterio) {
        Map<String, String> resultado = new HashMap<>();
        String sql = "SELECT u.nombre, COUNT(c.id) AS totalCultivos " +
                     "FROM usuarios u " +
                     "JOIN roles_usuarios ru ON u.id = ru.usuario_id " +
                     "JOIN roles r ON ru.rol_id = r.id " +
                     "LEFT JOIN cultivos c ON c.supervisor_id = u.id " +
                     "WHERE r.nombre = 'supervisor' " +
                     "AND (u.nombre LIKE ? OR u.documento = ?) " +
                     "GROUP BY u.id, u.nombre " +
                     "LIMIT 1";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + criterio + "%");
            ps.setString(2, criterio);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    resultado.put("nombre", rs.getString("nombre"));
                    resultado.put("totalCultivos", String.valueOf(rs.getInt("totalCultivos")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultado;
    }
    public List<Map<String, String>> buscarTareasPorTrabajador(String criterio) {
        List<Map<String, String>> tareas = new ArrayList<>();
        String sql = "SELECT ut.id, t.nombre AS tarea, ut.estado, ut.jornada, c.nombre AS cultivo " +
                     "FROM usuario_tarea ut " +
                     "JOIN usuarios u ON ut.usuario_id = u.id " +
                     "JOIN tareas t ON ut.tarea_id = t.id " +
                     "JOIN cultivos c ON ut.cultivo_id = c.id " +
                     "WHERE (u.nombre LIKE ? OR u.documento = ?) " +
                     "AND ut.estado IN ('Completada', 'En Proceso')";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + criterio + "%");
            ps.setString(2, criterio);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> tarea = new HashMap<>();
                    tarea.put("id",      rs.getString("id"));
                    tarea.put("tarea",   rs.getString("tarea"));
                    tarea.put("estado",  rs.getString("estado"));
                    tarea.put("cultivo", rs.getString("cultivo"));
                    String jornada = rs.getString("jornada");
                    tarea.put("jornada", (jornada != null) ? jornada : "Medio Dia");
                    tareas.add(tarea);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en buscarTareasPorTrabajador: " + e.getMessage());
        }
        return tareas;
    }
}
