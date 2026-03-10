package com.agritrack.agritrackplus.DAO;

import com.agritrack.agritrackplus.db.Conexion;
import com.agritrack.agritrackplus.modelo.Tarea;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TareaDAO {

    public TareaDAO() {
        // Ya no abrimos la conexión aquí para evitar que se cierre sola
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
        // Según tu SQL: Rol 2 es Trabajador
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

    public boolean agregarTarea(int idCultivo, String descripcion, String nombreManual, String jornada, int idTrabajador) {
        String sqlTarea = "INSERT INTO tareas (nombre) VALUES (?)";
        String sqlAsignacion = "INSERT INTO usuario_tarea (cultivo_id, tarea_id, usuario_id, descripcion_actividad, jornada, estado) VALUES (?, ?, ?, ?, ?, 'Pendiente')";

        try (Connection con = new Conexion().getConexion()) {
            con.setAutoCommit(false); // Iniciamos una transacción

            try (PreparedStatement psTarea = con.prepareStatement(sqlTarea, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                psTarea.setString(1, nombreManual);
                psTarea.executeUpdate();

                // Recuperamos el ID que se le asignó a la nueva tarea
                ResultSet rs = psTarea.getGeneratedKeys();
                int idNuevaTarea = 0;
                if (rs.next()) {
                    idNuevaTarea = rs.getInt(1);
                }

                // Ahora insertamos en la tabla relacional usando ese ID
                try (PreparedStatement psAsign = con.prepareStatement(sqlAsignacion)) {
                    psAsign.setInt(1, idCultivo);
                    psAsign.setInt(2, idNuevaTarea);
                    psAsign.setInt(3, idTrabajador);
                    psAsign.setString(4, descripcion);
                    psAsign.setString(5, jornada);

                    psAsign.executeUpdate();
                }

                con.commit(); // Guardamos todo
                return true;
            } catch (SQLException e) {
                con.rollback(); // Si algo falla, deshacemos los cambios
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean agregarTareaManual(int idCultivo, String descripcion, String nombreTarea, String jornada, int idTrabajador) {
        // SQLs según tu script: 'usuario_tarea' requiere cultivo_id, tarea_id, usuario_id...
        String sqlTarea = "INSERT INTO tareas (nombre) VALUES (?)";
        String sqlAsignacion = "INSERT INTO usuario_tarea (cultivo_id, tarea_id, usuario_id, descripcion_actividad, jornada, estado) VALUES (?, ?, ?, ?, ?, 'Pendiente')";

        try (Connection con = new com.agritrack.agritrackplus.db.Conexion().getConexion()) {
            con.setAutoCommit(false); // IMPORTANTE: Transacción manual

            int idGenerado = 0;
            // 1. Insertar en 'tareas'
            try (PreparedStatement psT = con.prepareStatement(sqlTarea, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                psT.setString(1, nombreTarea);
                psT.executeUpdate();

                ResultSet rs = psT.getGeneratedKeys();
                if (rs.next()) {
                    idGenerado = rs.getInt(1);
                }
            }

            // 2. Insertar en 'usuario_tarea'
            try (PreparedStatement psA = con.prepareStatement(sqlAsignacion)) {
                psA.setInt(1, idCultivo);     // cultivo_id
                psA.setInt(2, idGenerado);    // tarea_id (el que acabamos de crear)
                psA.setInt(3, idTrabajador);  // usuario_id (el trabajador seleccionado)
                psA.setString(4, descripcion); // descripcion_actividad
                psA.setString(5, jornada);     // jornada
                psA.executeUpdate();
            }

            con.commit(); // Si llegamos aquí, se guarda todo en la DB
            return true;

        } catch (SQLException e) {
            System.err.println("ERROR SQL al crear tarea: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 5. MÉTODO PARA LISTAR EN LA TABLA PRINCIPAL
    public List<Tarea> listarTareas() {
        List<Tarea> lista = new ArrayList<>();
        String sql = "SELECT ut.id, c.nombre AS nombre_cultivo, t.nombre AS nombre_tarea, " +
                     "u.nombre AS nombre_trabajador, ut.descripcion_actividad AS descripcion, " +
                     "ut.jornada, ut.estado " +
                     "FROM usuario_tarea ut " +
                     "JOIN cultivos c ON ut.cultivo_id = c.id " +
                     "JOIN tareas t ON ut.tarea_id = t.id " +
                     "JOIN usuarios u ON ut.usuario_id = u.id " +
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
    public List<Tarea> listarCultivosPorSupervisor(int idSupervisor) {
        List<Tarea> lista = new ArrayList<>();
        // Consulta para obtener cultivos del supervisor y sus trabajadores asignados
        String sql = "SELECT c.id, c.nombre AS cultivo, c.estado, c.ciclo, " +
                     "GROUP_CONCAT(u.nombre SEPARATOR ', ') AS trabajadores " +
                     "FROM supervisor s " +
                     "JOIN cultivos c ON s.cultivo_id = c.id " +
                     "LEFT JOIN cultivo_trabajador ct ON c.id = ct.cultivo_id " +
                     "LEFT JOIN usuarios u ON ct.usuario_id = u.id " +
                     "WHERE s.usuario_id = ? " +
                     "GROUP BY c.id";

        try (Connection con = new com.agritrack.agritrackplus.db.Conexion().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSupervisor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Tarea t = new Tarea();
                    t.setCultivoId(rs.getInt("id"));
                    t.setNombreCultivo(rs.getString("cultivo"));
                    t.setEstado(rs.getString("estado"));
                    t.setJornada(rs.getString("ciclo")); // Usamos jornada para el ciclo temporalmente
                    t.setNombreTrabajador(rs.getString("trabajadores")); // Aquí van todos los nombres
                    lista.add(t);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}