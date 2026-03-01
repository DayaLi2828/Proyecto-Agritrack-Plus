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
            ps = conn.prepareStatement("SELECT id FROM correo WHERE correo = ?");
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
            ps = conn.prepareStatement(
                "SELECT c.id FROM correo c WHERE c.correo = ? AND c.usuario_id != ?"
            );
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

    public Usuario login(String correo, String pass) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(
                "SELECT u.id, u.nombre, u.documento, u.direccion, u.estado, u.pass, " +
                "COALESCE(f.ruta, 'default.png') AS foto, " +
                "c.correo, t.telefono, r.nombre AS rol " +
                "FROM usuarios u " +
                "LEFT JOIN fotos_usuario f ON f.usuario_id = u.id " +
                "JOIN correo c ON c.usuario_id = u.id " +
                "LEFT JOIN telefono t ON t.usuario_id = u.id " +
                "JOIN roles_usuarios ru ON ru.usuario_id = u.id " +
                "JOIN roles r ON r.id = ru.rol_id " +
                "WHERE c.correo = ? AND u.pass = ?"
            );
            ps.setString(1, correo);
            ps.setString(2, encriptarMD5(pass));
            rs = ps.executeQuery();
            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setDocumento(rs.getString("documento"));
                usuario.setDireccion(rs.getString("direccion"));
                usuario.setEstado(rs.getString("estado"));
                usuario.setPass(rs.getString("pass"));
                usuario.setCorreo(rs.getString("correo"));
                usuario.setTelefono(rs.getString("telefono"));
                usuario.setRol(rs.getString("rol"));
                System.out.println("LOGIN OK: " + usuario.getNombre());
                return usuario;
            }
            System.out.println("LOGIN FALLIDO");
            return null;
        } catch (Exception e) {
            System.err.println("ERROR login: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            cerrar(rs, ps, conn);
        }
    }

    public boolean crear(String nombre, String pass, String documento, String direccion,
                        String estado, String correo, String telefono, int rolId, String foto) {
        if (existeCorreo(correo)) {
            System.err.println("CORREO DUPLICADO: " + correo);
            return false;
        }
        if (existeDocumento(documento)) {
            System.err.println("DOCUMENTO DUPLICADO: " + documento);
            return false;
        }
        Connection conn = null;
        try {
            conn = Conexion.getConnection();
            int usuarioId = 0;
            try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO usuarios (nombre, pass, documento, direccion, estado) VALUES (?, ?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, nombre);
                ps.setString(2, encriptarMD5(pass));
                ps.setString(3, documento);
                ps.setString(4, direccion);
                ps.setString(5, estado);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) usuarioId = rs.getInt(1);
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO correo (correo, usuario_id) VALUES (?, ?)")) {
                ps.setString(1, correo);
                ps.setInt(2, usuarioId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO telefono (telefono, usuario_id) VALUES (?, ?)")) {
                ps.setString(1, telefono);
                ps.setInt(2, usuarioId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO roles_usuarios (usuario_id, rol_id) VALUES (?, ?)")) {
                ps.setInt(1, usuarioId);
                ps.setInt(2, rolId);
                ps.executeUpdate();
            }
            if (foto != null && !foto.isEmpty()) {
                try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO fotos_usuario (usuario_id, ruta) VALUES (?, ?)")) {
                    ps.setInt(1, usuarioId);
                    ps.setString(2, foto);
                    ps.executeUpdate();
                }
            }
            //com.agritrack.agritrackplus.util.EmailService.enviarRegistroExitoso(correo, nombre);
            return true;
        } catch (Exception e) {
            System.err.println("ERROR crear: " + e.getMessage());
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
            ps = conn.prepareStatement(
                "SELECT u.id, u.nombre, u.documento, u.direccion, u.estado, " +
                "COALESCE(f.ruta, 'default.png') AS foto, " +
                "c.correo, t.telefono, r.nombre AS rol " +
                "FROM usuarios u " +
                "LEFT JOIN fotos_usuario f ON f.usuario_id = u.id " +
                "LEFT JOIN correo c ON c.usuario_id = u.id " +
                "LEFT JOIN telefono t ON t.usuario_id = u.id " +
                "LEFT JOIN roles_usuarios ru ON ru.usuario_id = u.id " +
                "LEFT JOIN roles r ON r.id = ru.rol_id"
            );
            rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, String> usuario = new HashMap<>();
                usuario.put("id", String.valueOf(rs.getInt("id")));
                usuario.put("nombre", rs.getString("nombre"));
                usuario.put("documento", rs.getString("documento"));
                usuario.put("direccion", rs.getString("direccion"));
                usuario.put("estado", rs.getString("estado"));
                usuario.put("foto", rs.getString("foto"));
                usuario.put("correo", rs.getString("correo"));
                usuario.put("telefono", rs.getString("telefono"));
                usuario.put("rol", rs.getString("rol"));
                lista.add(usuario);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cerrar(rs, ps, conn);
        }
        return lista;
    }

    public List<Map<String, String>> listarTrabajadores() {
        List<Map<String, String>> lista = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(
                "SELECT u.id, u.nombre, COALESCE(f.ruta, 'default.png') AS foto " +
                "FROM usuarios u " +
                "JOIN roles_usuarios ru ON ru.usuario_id = u.id " +
                "JOIN roles r ON r.id = ru.rol_id " +
                "LEFT JOIN fotos_usuario f ON f.usuario_id = u.id " +
                "WHERE r.nombre = 'Trabajador'"
            );
            rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, String> usuario = new HashMap<>();
                usuario.put("id", String.valueOf(rs.getInt("id")));
                usuario.put("nombre", rs.getString("nombre"));
                usuario.put("foto", rs.getString("foto"));
                lista.add(usuario);
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
            if (existeCorreo(correo, usuarioId)) {
                System.err.println("CORREO DUPLICADO: " + correo);
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE usuarios SET nombre=?, documento=?, direccion=?, estado=? WHERE id=?")) {
                ps.setString(1, nombre);
                ps.setString(2, documento);
                ps.setString(3, direccion);
                ps.setString(4, estado);
                ps.setInt(5, usuarioId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE correo SET correo=? WHERE usuario_id=?")) {
                ps.setString(1, correo);
                ps.setInt(2, usuarioId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE telefono SET telefono=? WHERE usuario_id=?")) {
                ps.setString(1, telefono);
                ps.setInt(2, usuarioId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE roles_usuarios SET rol_id=? WHERE usuario_id=?")) {
                ps.setInt(1, Integer.parseInt(rolId));
                ps.setInt(2, usuarioId);
                ps.executeUpdate();
            }
            if (pass != null && !pass.trim().isEmpty()) {
                try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE usuarios SET pass=? WHERE id=?")) {
                    ps.setString(1, encriptarMD5(pass));
                    ps.setInt(2, usuarioId);
                    ps.executeUpdate();
                }
            }
            if (nombreFoto != null && !nombreFoto.trim().isEmpty()) {
                try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO fotos_usuario (usuario_id, ruta) VALUES (?, ?) ON DUPLICATE KEY UPDATE ruta=?")) {
                    ps.setInt(1, usuarioId);
                    ps.setString(2, nombreFoto);
                    ps.setString(3, nombreFoto);
                    ps.executeUpdate();
                }
            }
            return true;
        } catch (Exception e) {
            System.err.println("ERROR editar: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            cerrar(null, null, conn);
        }
    }

    public Map<String, String> obtenerPorId(String id) {
        Map<String, String> usuario = new HashMap<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(
                "SELECT u.id, u.nombre, u.documento, u.direccion, u.estado, " +
                "COALESCE(f.ruta, 'default.png') AS foto, " +
                "c.correo, t.telefono, r.nombre AS rol " +
                "FROM usuarios u " +
                "LEFT JOIN fotos_usuario f ON f.usuario_id = u.id " +
                "LEFT JOIN correo c ON c.usuario_id = u.id " +
                "LEFT JOIN telefono t ON t.usuario_id = u.id " +
                "LEFT JOIN roles_usuarios ru ON ru.usuario_id = u.id " +
                "LEFT JOIN roles r ON r.id = ru.rol_id " +
                "WHERE u.id = ?"
            );
            ps.setInt(1, Integer.parseInt(id));
            rs = ps.executeQuery();
            if (rs.next()) {
                usuario.put("id", String.valueOf(rs.getInt("id")));
                usuario.put("nombre", rs.getString("nombre"));
                usuario.put("documento", rs.getString("documento"));
                usuario.put("direccion", rs.getString("direccion"));
                usuario.put("estado", rs.getString("estado"));
                usuario.put("foto", rs.getString("foto"));
                usuario.put("correo", rs.getString("correo"));
                usuario.put("telefono", rs.getString("telefono"));
                usuario.put("rol", rs.getString("rol"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cerrar(rs, ps, conn);
        }
        return usuario;
    }

    public List<Map<String, String>> listarUsuariosCompletos() {
        List<Map<String, String>> lista = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(
                "SELECT u.id, u.nombre, u.documento, u.direccion, u.estado, " +
                "COALESCE(f.ruta, 'asset/imagenes/default-avatar.png') AS foto, " +
                "c.correo, t.telefono, r.nombre AS rol " +
                "FROM usuarios u " +
                "LEFT JOIN fotos_usuario f ON u.id = f.usuario_id " +
                "LEFT JOIN correo c ON u.id = c.usuario_id " +
                "LEFT JOIN telefono t ON u.id = t.usuario_id " +
                "LEFT JOIN roles_usuarios ru ON u.id = ru.usuario_id " +
                "LEFT JOIN roles r ON ru.rol_id = r.id " +
                "ORDER BY u.id DESC"
            );

            rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, String> usuario = new HashMap<>();
                usuario.put("id", String.valueOf(rs.getInt("id")));
                usuario.put("nombre", rs.getString("nombre"));
                usuario.put("documento", rs.getString("documento"));
                usuario.put("direccion", rs.getString("direccion"));
                usuario.put("estado", rs.getString("estado"));
                usuario.put("foto", rs.getString("foto"));  // ✅ NUNCA NULL
                usuario.put("correo", rs.getString("correo"));
                usuario.put("telefono", rs.getString("telefono"));
                usuario.put("rol", rs.getString("rol"));
                lista.add(usuario);
            }
            System.out.println("Listados " + lista.size() + " usuarios");
        } catch (Exception e) {
            System.err.println(" Error listar: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {}
        }
        return lista;
    }

        // MÉTODO 1: CAMBIAR ESTADO (Toggle Activo/Inactivo)
    public boolean actualizarEstado(int usuarioId, String nuevoEstado) {
        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement("UPDATE usuarios SET estado = ? WHERE id = ?");
            ps.setString(1, nuevoEstado);
            ps.setInt(2, usuarioId);
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (Exception e) {
            System.err.println("Error actualizar estado: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            cerrar(null, ps, conn);
        }
}
    public boolean eliminarUsuario(int usuarioId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Conexion.getConnection();
            conn.setAutoCommit(false); // Transacción para seguridad

            // 1. Borrar de tablas dependientes
            String[] tablasRelacionadas = {
                "DELETE FROM cultivo_trabajador WHERE usuario_id = ?",
                "DELETE FROM supervisor WHERE usuario_id = ?",
                "DELETE FROM roles_usuarios WHERE usuario_id = ?",
                "DELETE FROM correo WHERE usuario_id = ?",
                "DELETE FROM telefono WHERE usuario_id = ?",
                "DELETE FROM fotos_usuario WHERE usuario_id = ?",
                "DELETE FROM pagos WHERE usuario_id = ?"
            };

            for (String sql : tablasRelacionadas) {
                ps = conn.prepareStatement(sql);
                ps.setInt(1, usuarioId);
                ps.executeUpdate();
                ps.close();
            }

            // 2. Borrar de la tabla usuarios
            ps = conn.prepareStatement("DELETE FROM usuarios WHERE id = ?");
            ps.setInt(1, usuarioId);
            int filas = ps.executeUpdate();

            conn.commit();
            return filas > 0;
        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}

