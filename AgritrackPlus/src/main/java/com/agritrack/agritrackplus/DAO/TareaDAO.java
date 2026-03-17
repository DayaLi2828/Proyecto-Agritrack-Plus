package com.agritrack.agritrackplus.DAO;

import com.agritrack.agritrackplus.db.Conexion;
import com.agritrack.agritrackplus.modelo.Tarea;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TareaDAO {

    public TareaDAO() {
        // Constructor vacío para manejar conexiones por método
    }

    // 1. MÉTODO PARA EL SELECT DE CULTIVOS
    public List<Tarea> listarCultivos() {
        List<Tarea> lista = new ArrayList<>();
        String sql = "SELECT id, nombre FROM cultivos WHERE estado = 'Activo'";
        try (Connection con = new Conexion().getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Tarea t = new Tarea();
                t.setCultivoId(rs.getInt("id"));
                t.setNombreCultivo(rs.getString("nombre"));
                lista.add(t);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // 2. MÉTODO PARA EL SELECT DE TRABAJADORES
    public List<Tarea> listarTrabajadores() {
        List<Tarea> lista = new ArrayList<>();
        String sql = "SELECT u.id, u.nombre FROM usuarios u " +
                     "JOIN roles_usuarios ru ON u.id = ru.usuario_id " +
                     "WHERE ru.rol_id = 2";
        try (Connection con = new Conexion().getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Tarea t = new Tarea();
                t.setId(rs.getInt("id")); 
                t.setNombreTrabajador(rs.getString("nombre"));
                lista.add(t);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // 3. MÉTODO PARA EL SELECT DE TIPOS DE TAREAS (Catálogo)
    public List<Tarea> listarCatalogoTareas() {
        List<Tarea> lista = new ArrayList<>();
        String sql = "SELECT id, nombre FROM tareas";
        try (Connection con = new Conexion().getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Tarea t = new Tarea();
                t.setId(rs.getInt("id"));
                t.setNombreTarea(rs.getString("nombre"));
                lista.add(t);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // 4. AGREGAR TAREA (Desde catálogo)
    public boolean agregarTarea(int idCultivo, String descripcion, String nombreManual, String jornada, int idTrabajador) {
        String sqlTarea = "INSERT INTO tareas (nombre) VALUES (?)";
        String sqlAsignacion = "INSERT INTO usuario_tarea (cultivo_id, tarea_id, usuario_id, descripcion_actividad, jornada, estado) VALUES (?, ?, ?, ?, ?, 'Pendiente')";

        try (Connection con = new Conexion().getConexion()) {
            con.setAutoCommit(false); 
            try (PreparedStatement psTarea = con.prepareStatement(sqlTarea, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                psTarea.setString(1, nombreManual);
                psTarea.executeUpdate();

                ResultSet rs = psTarea.getGeneratedKeys();
                int idNuevaTarea = 0;
                if (rs.next()) { idNuevaTarea = rs.getInt(1); }

                try (PreparedStatement psAsign = con.prepareStatement(sqlAsignacion)) {
                    psAsign.setInt(1, idCultivo);
                    psAsign.setInt(2, idNuevaTarea);
                    psAsign.setInt(3, idTrabajador);
                    psAsign.setString(4, descripcion);
                    psAsign.setString(5, jornada);
                    psAsign.executeUpdate();
                }
                con.commit(); 
                return true;
            } catch (SQLException e) {
                con.rollback(); 
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // 5. AGREGAR TAREA MANUAL
    public boolean agregarTareaManual(int idCultivo, String descripcion, String nombreTarea, String jornada, int idTrabajador) {
        String sqlTarea = "INSERT INTO tareas (nombre) VALUES (?)";
        String sqlAsignacion = "INSERT INTO usuario_tarea (cultivo_id, tarea_id, usuario_id, descripcion_actividad, jornada, estado) VALUES (?, ?, ?, ?, ?, 'Pendiente')";

        try (Connection con = new Conexion().getConexion()) {
            con.setAutoCommit(false); 
            int idGenerado = 0;
            try (PreparedStatement psT = con.prepareStatement(sqlTarea, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                psT.setString(1, nombreTarea);
                psT.executeUpdate();
                ResultSet rs = psT.getGeneratedKeys();
                if (rs.next()) idGenerado = rs.getInt(1);
            }
            try (PreparedStatement psA = con.prepareStatement(sqlAsignacion)) {
                psA.setInt(1, idCultivo);
                psA.setInt(2, idGenerado);
                psA.setInt(3, idTrabajador);
                psA.setString(4, descripcion);
                psA.setString(5, jornada);
                psA.executeUpdate();
            }
            con.commit(); 
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // 6. MÉTODO PARA LISTAR EN LA TABLA PRINCIPAL (CORREGIDO: OCULTA PAGADAS)
    public List<Tarea> listarTareas() {
        List<Tarea> lista = new ArrayList<>();
        String sql = "SELECT ut.id, c.nombre AS nombre_cultivo, t.nombre AS nombre_tarea, " +
                     "u.nombre AS nombre_trabajador, ut.descripcion_actividad AS descripcion, " +
                     "ut.jornada, ut.estado " +
                     "FROM usuario_tarea ut " +
                     "JOIN cultivos c ON ut.cultivo_id = c.id " +
                     "JOIN tareas t ON ut.tarea_id = t.id " +
                     "JOIN usuarios u ON ut.usuario_id = u.id " +
                     "WHERE UPPER(TRIM(ut.estado)) != 'PAGADO' " + 
                     "ORDER BY ut.id DESC"; 
        try (Connection con = new Conexion().getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Tarea t = new Tarea();
                t.setId(rs.getInt("id"));
                t.setNombreCultivo(rs.getString("nombre_cultivo"));
                t.setNombreTarea(rs.getString("nombre_tarea"));
                t.setNombreTrabajador(rs.getString("nombre_trabajador"));
                t.setDescripcion(rs.getString("descripcion"));
                t.setJornada(rs.getString("jornada"));
                t.setEstado(rs.getString("estado"));
                lista.add(t);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // 7. MÉTODO PARA LISTAR TAREAS DE UN TRABAJADOR ESPECÍFICO (CORREGIDO: OCULTA PAGADAS)
    public List<Tarea> listarTareasPorTrabajador(int idTrabajador) {
        List<Tarea> lista = new ArrayList<>();
        String sql = "SELECT ut.id, c.nombre AS nombre_cultivo, t.nombre AS nombre_tarea, " +
                     "ut.descripcion_actividad AS descripcion, ut.jornada, ut.estado " +
                     "FROM usuario_tarea ut " +
                     "JOIN cultivos c ON ut.cultivo_id = c.id " +
                     "JOIN tareas t ON ut.tarea_id = t.id " +
                     "WHERE ut.usuario_id = ? AND UPPER(TRIM(ut.estado)) != 'PAGADO' " +
                     "ORDER BY ut.id DESC";
        try (Connection con = new Conexion().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idTrabajador);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Tarea t = new Tarea();
                    t.setId(rs.getInt("id"));
                    t.setNombreCultivo(rs.getString("nombre_cultivo"));
                    t.setNombreTarea(rs.getString("nombre_tarea"));
                    t.setDescripcion(rs.getString("descripcion"));
                    t.setJornada(rs.getString("jornada"));
                    t.setEstado(rs.getString("estado"));
                    lista.add(t);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // 8. MÉTODO PARA ACTUALIZAR ESTADO DE UNA TAREA
    public boolean actualizarEstadoTarea(int idTarea, String nuevoEstado, String observacion) {
        String sql = "UPDATE usuario_tarea SET estado = ?, observacion = ? WHERE id = ?";
        try (Connection con = new Conexion().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setString(2, observacion);
            ps.setInt(3, idTarea);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // 9. MÉTODO PARA LISTAR CULTIVOS POR SUPERVISOR
    public List<Tarea> listarCultivosPorSupervisor(int idSupervisor) {
        List<Tarea> lista = new ArrayList<>();
        String sql = "SELECT c.id, c.nombre AS cultivo, c.estado, c.ciclo, " +
                     "GROUP_CONCAT(u.nombre SEPARATOR ', ') AS trabajadores " +
                     "FROM supervisor s " +
                     "JOIN cultivos c ON s.cultivo_id = c.id " +
                     "LEFT JOIN cultivo_trabajador ct ON c.id = ct.cultivo_id " +
                     "LEFT JOIN usuarios u ON ct.usuario_id = u.id " +
                     "WHERE s.usuario_id = ? " +
                     "GROUP BY c.id";
        try (Connection con = new Conexion().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idSupervisor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Tarea t = new Tarea();
                    t.setCultivoId(rs.getInt("id"));
                    t.setNombreCultivo(rs.getString("cultivo"));
                    t.setEstado(rs.getString("estado"));
                    t.setJornada(rs.getString("ciclo")); 
                    t.setNombreTrabajador(rs.getString("trabajadores"));
                    lista.add(t);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // 10. OBTENER CONTEOS POR ESTADO (CORREGIDO: NO CUENTA PAGADAS)
    public Map<String, Integer> obtenerConteosPorEstado(int idUsuario) {
        Map<String, Integer> conteos = new HashMap<>();
        conteos.put("Pendiente", 0);
        conteos.put("En Proceso", 0);
        conteos.put("Completada", 0);

        String sql = "SELECT TRIM(estado) as estado_limpio, COUNT(*) as total " +
                     "FROM usuario_tarea WHERE usuario_id = ? AND UPPER(TRIM(estado)) != 'PAGADO' GROUP BY estado";

        try (Connection con = new Conexion().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String estadoDB = rs.getString("estado_limpio");
                    if (estadoDB != null) {
                        if (estadoDB.equalsIgnoreCase("Pendiente")) conteos.put("Pendiente", rs.getInt("total"));
                        if (estadoDB.equalsIgnoreCase("En Proceso")) conteos.put("En Proceso", rs.getInt("total"));
                        if (estadoDB.equalsIgnoreCase("Completada")) conteos.put("Completada", rs.getInt("total"));
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return conteos;
    }

    // 11. CÁLCULO DE CUMPLIMIENTO (PAGADAS CUENTAN COMO ÉXITO)
    public Map<String, Integer> obtenerCumplimientoCultivos(int idUsuario) {
        Map<String, Integer> cumplimiento = new HashMap<>();
        String sql = "SELECT c.nombre, " +
                     "IFNULL(ROUND((SUM(CASE WHEN UPPER(TRIM(ut.estado)) IN ('COMPLETADA', 'PAGADO') THEN 1 ELSE 0 END) * 100) / COUNT(ut.id)), 0) as porcentaje " +
                     "FROM usuario_tarea ut " +
                     "JOIN cultivos c ON ut.cultivo_id = c.id " +
                     "WHERE ut.usuario_id = ? " +
                     "GROUP BY c.nombre";

        try (Connection con = new Conexion().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cumplimiento.put(rs.getString("nombre"), rs.getInt("porcentaje"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return cumplimiento;
    }

    // 12. RESUMEN DE PAGOS
    public Map<String, Double> obtenerResumenPagos(int idUsuario) {
        Map<String, Double> resumen = new HashMap<>();
        resumen.put("Pagado", 0.0);
        resumen.put("Pendiente", 0.0);

        String sql = "SELECT SUM(pago) as total FROM pagos WHERE usuario_id = ? AND (UPPER(TRIM(estado)) IN ('ACTIVO', 'PAGADO'))";

        try (Connection con = new Conexion().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    resumen.put("Pagado", rs.getDouble("total"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return resumen;
    }
}