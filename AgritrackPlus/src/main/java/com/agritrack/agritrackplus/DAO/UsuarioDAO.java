package com.agritrack.agritrackplus.DAO;

import com.agritrack.agritrackplus.db.Conexion;
import com.agritrack.agritrackplus.modelo.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsuarioDAO {

    private String encriptarMD5(String pass) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(pass.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (Exception e) {
            return pass;
        }
    }

    private void cerrar(ResultSet rs, PreparedStatement ps, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean existeCorreo(String correo) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement("SELECT id FROM correo WHERE email = ?");
            ps.setString(1, correo);
            rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            cerrar(rs, ps, conn);
        }
    }

    private boolean existeCorreo(String correo, int usuarioIdExcluir) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement("SELECT id FROM correo WHERE email = ? AND usuario_id != ?");
            ps.setString(1, correo);
            ps.setInt(2, usuarioIdExcluir);
            rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            cerrar(rs, ps, conn);
        }
    }

    public boolean existeDocumento(String documento) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement("SELECT id FROM usuarios WHERE documento = ?");
            ps.setString(1, documento);
            rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            cerrar(rs, ps, conn);
        }
    }

    public Map<String, Object> validarAcceso(String email, String pass) {
        String sql = "SELECT u.id, u.nombre, r.nombre AS rol_nombre " +
                     "FROM correo c " +
                     "JOIN usuarios u ON c.usuario_id = u.id " +
                     "JOIN roles_usuarios ru ON u.id = ru.usuario_id " +
                     "JOIN roles r ON ru.rol_id = r.id " +
                     "WHERE c.email = ? AND u.pass = MD5(?)"; 

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            ps.setString(2, pass.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> datos = new HashMap<>();
                    datos.put("id", rs.getInt("id"));
                    datos.put("nombre", rs.getString("nombre"));
                    datos.put("rol", rs.getString("rol_nombre"));
                    return datos;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // MODIFICADO: Se quitó el parámetro 'estado' y se puso 'Activo' fijo en el SQL
    public boolean crear(String nombre, String pass, String documento, String direccion,
                         String correo, String telefono, int rolId, String foto) {
        if (existeCorreo(correo) || existeDocumento(documento)) return false;

        Connection conn = null;
        try {
            conn = Conexion.getConnection();
            conn.setAutoCommit(false);
            int usuarioId = 0;

            String sqlU = "INSERT INTO usuarios (nombre, pass, documento, direccion, estado) VALUES (?, ?, ?, ?, 'Activo')";
            try (PreparedStatement ps = conn.prepareStatement(sqlU, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, nombre);
                ps.setString(2, encriptarMD5(pass));
                ps.setString(3, documento);
                ps.setString(4, direccion);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) usuarioId = rs.getInt(1);
                }
            }

            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO correo (email, usuario_id) VALUES (?, ?)")) {
                ps.setString(1, correo);
                ps.setInt(2, usuarioId);
                ps.executeUpdate();
            }

            // Cambiar 'telefono' por 'numero' si tu tabla telefono usa esa columna
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO telefono (numero, usuario_id) VALUES (?, ?)")) {
                ps.setString(1, telefono);
                ps.setInt(2, usuarioId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO roles_usuarios (usuario_id, rol_id) VALUES (?, ?)")) {
                ps.setInt(1, usuarioId);
                ps.setInt(2, rolId);
                ps.executeUpdate();
            }

            if (foto != null && !foto.isEmpty()) {
                try (PreparedStatement ps = conn.prepareStatement("INSERT INTO fotos_usuario (usuario_id, ruta) VALUES (?, ?)")) {
                    ps.setInt(1, usuarioId);
                    ps.setString(2, foto);
                    ps.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            return false;
        } finally {
            cerrar(null, null, conn);
        }
    }
    public List<Map<String, String>> listarUsuarios() {
        List<Map<String, String>> lista = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConnection();
            // Usamos LEFT JOIN en todas para que si falta un dato, el usuario NO desaparezca
            String sql = "SELECT u.id, u.nombre, u.documento, u.direccion, u.estado, " +
                         "COALESCE(f.ruta, 'asset/imagenes/default-avatar.png') AS foto, " +
                         "COALESCE(c.email, 'Sin correo') AS email, " +
                         "COALESCE(t.numero, 'Sin teléfono') AS telefono, " +
                         "COALESCE(r.nombre, 'Sin rol') AS rol " + 
                         "FROM usuarios u " +
                         "LEFT JOIN fotos_usuario f ON u.id = f.usuario_id " +
                         "LEFT JOIN correo c ON u.id = c.usuario_id " +
                         "LEFT JOIN telefono t ON u.id = t.usuario_id " +
                         "LEFT JOIN roles_usuarios ru ON u.id = ru.usuario_id " +
                         "LEFT JOIN roles r ON ru.rol_id = r.id " +
                         "ORDER BY u.id DESC";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, String> u = new HashMap<>();
                u.put("id", String.valueOf(rs.getInt("id")));
                u.put("nombre", rs.getString("nombre"));
                u.put("documento", rs.getString("documento"));
                u.put("direccion", rs.getString("direccion"));
                u.put("estado", rs.getString("estado"));
                u.put("foto", rs.getString("foto"));
                u.put("correo", rs.getString("email")); 
                u.put("telefono", rs.getString("telefono"));
                u.put("rol", rs.getString("rol"));
                lista.add(u);
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally { 
            cerrar(rs, ps, conn); 
        }
        return lista;
    }

    public boolean editarUsuario(String id, String nombre, String documento, String direccion,
                                String correo, String telefono, String pass, String rolId,
                                String estado, String nombreFoto) {
        Connection conn = null;
        try {
            int usuarioId = Integer.parseInt(id);
            conn = Conexion.getConnection();
            conn.setAutoCommit(false);
            if (existeCorreo(correo, usuarioId)) return false;

            try (PreparedStatement ps = conn.prepareStatement("UPDATE usuarios SET nombre=?, documento=?, direccion=?, estado=? WHERE id=?")) {
                ps.setString(1, nombre);
                ps.setString(2, documento);
                ps.setString(3, direccion);
                ps.setString(4, estado);
                ps.setInt(5, usuarioId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement("UPDATE correo SET email=? WHERE usuario_id=?")) {
                ps.setString(1, correo); ps.setInt(2, usuarioId); ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement("UPDATE telefono SET numero=? WHERE usuario_id=?")) {
                ps.setString(1, telefono); ps.setInt(2, usuarioId); ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement("UPDATE roles_usuarios SET rol_id=? WHERE usuario_id=?")) {
                ps.setInt(1, Integer.parseInt(rolId)); ps.setInt(2, usuarioId); ps.executeUpdate();
            }

            if (pass != null && !pass.trim().isEmpty()) {
                try (PreparedStatement ps = conn.prepareStatement("UPDATE usuarios SET pass=? WHERE id=?")) {
                    ps.setString(1, encriptarMD5(pass)); ps.setInt(2, usuarioId); ps.executeUpdate();
                }
            }

            if (nombreFoto != null && !nombreFoto.trim().isEmpty()) {
                try (PreparedStatement ps = conn.prepareStatement("INSERT INTO fotos_usuario (usuario_id, ruta) VALUES (?, ?) ON DUPLICATE KEY UPDATE ruta=?")) {
                    ps.setInt(1, usuarioId); ps.setString(2, nombreFoto); ps.setString(3, nombreFoto); ps.executeUpdate();
                }
            }
            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            return false;
        } finally { cerrar(null, null, conn); }
    }

    public Map<String, String> obtenerPorId(String id) {
        Map<String, String> u = new HashMap<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConnection();
            
            ps = conn.prepareStatement("SELECT u.*, c.email, t.numero AS telefono, r.nombre as rol FROM usuarios u " +
                "LEFT JOIN correo c ON c.usuario_id = u.id " +
                "LEFT JOIN telefono t ON t.usuario_id = u.id " +
                "LEFT JOIN roles_usuarios ru ON ru.usuario_id = u.id " +
                "LEFT JOIN roles r ON r.id = ru.rol_id WHERE u.id = ?");

            ps.setInt(1, Integer.parseInt(id));
            rs = ps.executeQuery();

            if (rs.next()) {
                u.put("id", id);
                u.put("nombre", rs.getString("nombre"));
                u.put("documento", rs.getString("documento"));
                u.put("direccion", rs.getString("direccion"));
                u.put("estado", rs.getString("estado"));
                u.put("correo", rs.getString("email")); 
                u.put("telefono", rs.getString("telefono"));
                u.put("rol", rs.getString("rol"));
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally { 
            cerrar(rs, ps, conn); 
        }
        return u;
    }

    public boolean actualizarEstado(int usuarioId, String nuevoEstado) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement("UPDATE usuarios SET estado = ? WHERE id = ?");
            ps.setString(1, nuevoEstado); ps.setInt(2, usuarioId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { return false; }
        finally { cerrar(null, ps, conn); }
    }

    public boolean eliminarUsuario(int usuarioId) {
        Connection conn = null;
        try {
            conn = Conexion.getConnection();
            conn.setAutoCommit(false);
            String[] sqls = {
                "DELETE FROM cultivo_trabajador WHERE usuario_id = ?",
                "DELETE FROM roles_usuarios WHERE usuario_id = ?",
                "DELETE FROM correo WHERE usuario_id = ?",
                "DELETE FROM telefono WHERE usuario_id = ?",
                "DELETE FROM fotos_usuario WHERE usuario_id = ?",
                "DELETE FROM usuarios WHERE id = ?"
            };
            for (String sql : sqls) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, usuarioId); ps.executeUpdate();
                }
            }
            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            return false;
        } finally { cerrar(null, null, conn); }
    }

    public boolean actualizarPerfil(int id, String nombre, String documento, String direccion, String pass, String correo, String telefono) {
        Connection conn = null;
        try {
            conn = Conexion.getConnection();
            conn.setAutoCommit(false);

            String sqlUser;
            if (pass == null || pass.trim().isEmpty()) {
                sqlUser = "UPDATE usuarios SET nombre = ?, documento = ?, direccion = ? WHERE id = ?";
            } else {
                sqlUser = "UPDATE usuarios SET nombre = ?, documento = ?, direccion = ?, pass = MD5(?) WHERE id = ?";
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlUser)) {
                ps.setString(1, nombre);
                ps.setString(2, documento);
                ps.setString(3, direccion);
                if (pass == null || pass.trim().isEmpty()) {
                    ps.setInt(4, id);
                } else {
                    ps.setString(4, pass);
                    ps.setInt(5, id);
                }
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement("UPDATE correo SET email = ? WHERE usuario_id = ?")) {
                ps.setString(1, correo);
                ps.setInt(2, id);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement("UPDATE telefono SET numero = ? WHERE usuario_id = ?")) {
                ps.setString(1, telefono);
                ps.setInt(2, id);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            return false;
        } finally {
            cerrar(null, null, conn);
        }
    }

    public Map<String, Integer> obtenerProgresoPorCultivo(int idUsuario) {
        Map<String, Integer> progreso = new HashMap<>();
        String sql = "SELECT nombre_cultivo, " +
                     "ROUND((COUNT(CASE WHEN estado = 'Completada' THEN 1 END) * 100.0) / COUNT(*)) as porcentaje " +
                     "FROM tareas WHERE id_usuario = ? " +
                     "GROUP BY nombre_cultivo";

        try (Connection con = Conexion.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    progreso.put(rs.getString("nombre_cultivo"), rs.getInt("porcentaje"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return progreso;
    }

    public List<Map<String, Object>> obtenerTareasPorRol(int idUsuario, String rol) {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql;

        if ("administrador".equalsIgnoreCase(rol)) {
            sql = "SELECT ut.id, t.nombre as tarea, c.nombre as cultivo, u.nombre as trabajador, " +
                  "ut.descripcion_actividad, ut.estado, ut.jornada, ut.fecha_asignacion " +
                  "FROM usuario_tarea ut " +
                  "JOIN tareas t ON ut.tarea_id = t.id " +
                  "JOIN cultivos c ON ut.cultivo_id = c.id " +
                  "JOIN usuarios u ON ut.usuario_id = u.id";
        } else {
            sql = "SELECT ut.id, t.nombre as tarea, c.nombre as cultivo, u.nombre as trabajador, " +
                  "ut.descripcion_actividad, ut.estado, ut.jornada, ut.fecha_asignacion " +
                  "FROM usuario_tarea ut " +
                  "JOIN tareas t ON ut.tarea_id = t.id " +
                  "JOIN cultivos c ON ut.cultivo_id = c.id " +
                  "JOIN usuarios u ON ut.usuario_id = u.id " +
                  "JOIN supervisor s ON s.cultivo_id = c.id " +
                  "WHERE s.usuario_id = ?";
        }

        try (Connection con = Conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            if (!"administrador".equalsIgnoreCase(rol)) ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("tarea", rs.getString("tarea"));
                    map.put("cultivo", rs.getString("cultivo"));
                    map.put("trabajador", rs.getString("trabajador"));
                    map.put("descripcion", rs.getString("descripcion_actividad"));
                    map.put("estado", rs.getString("estado"));
                    map.put("jornada", rs.getString("jornada"));
                    map.put("fecha", rs.getString("fecha_asignacion"));
                    lista.add(map);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }

    public int contarUsuarios() {
        int total = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement("SELECT COUNT(*) FROM usuarios");
            rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cerrar(rs, ps, conn);
        }
        return total;
    }



    public Map<String, Integer> obtenerResumenTareas(int idUsuario) {
        Map<String, Integer> resumen = new HashMap<>();
        resumen.put("Completada", 0); 
        resumen.put("Proceso", 0); 
        resumen.put("Pendiente", 0);
        
        String sql = "SELECT estado, COUNT(*) as total FROM usuario_tarea WHERE usuario_id = ? GROUP BY estado";
        
        try (Connection con = Conexion.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { 
                    resumen.put(rs.getString("estado"), rs.getInt("total")); 
                }
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return resumen;
    }

    public List<Map<String, String>> listarTrabajadores() {
        List<Map<String, String>> lista = new ArrayList<>();
        String sql = "SELECT u.id, u.nombre " +
                     "FROM usuarios u " +
                     "JOIN roles_usuarios ru ON u.id = ru.usuario_id " +
                     "JOIN roles r ON ru.rol_id = r.id " +
                     "WHERE r.nombre IN ('trabajador', 'supervisor') AND u.estado = 'Activo'";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, String> t = new HashMap<>();
                t.put("id", String.valueOf(rs.getInt("id")));
                t.put("nombre", rs.getString("nombre"));
                lista.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Map<String, String>> listarSoloSupervisores() {
        List<Map<String, String>> lista = new ArrayList<>();
        String sql = "SELECT u.id, u.nombre FROM usuarios u " +
                     "JOIN roles_usuarios ru ON u.id = ru.usuario_id " +
                     "JOIN roles r ON ru.rol_id = r.id " +
                     "WHERE r.nombre = 'supervisor' AND u.estado = 'Activo'";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, String> m = new HashMap<>();
                m.put("id", String.valueOf(rs.getInt("id")));
                m.put("nombre", rs.getString("nombre"));
                lista.add(m);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }

    public List<Map<String, String>> listarSoloTrabajadores() {
        List<Map<String, String>> lista = new ArrayList<>();
        String sql = "SELECT u.id, u.nombre FROM usuarios u " +
                     "JOIN roles_usuarios ru ON u.id = ru.usuario_id " +
                     "JOIN roles r ON ru.rol_id = r.id " +
                     "WHERE r.nombre = 'trabajador' AND u.estado = 'Activo'";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, String> m = new HashMap<>();
                m.put("id", String.valueOf(rs.getInt("id")));
                m.put("nombre", rs.getString("nombre"));
                lista.add(m);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }
}