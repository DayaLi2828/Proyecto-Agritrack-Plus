package com.agritrack.agritrackplus.DAO;

import com.agritrack.agritrackplus.db.Conexion;
import com.agritrack.agritrackplus.modelo.Usuario;
import java.sql.*;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UsuarioDAO {

    private String encriptarMD5(String pass) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(pass.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return pass;
        }
    }

    private static final String SQL_LOGIN =
        "SELECT u.id, u.nombre, u.documento, u.direccion, u.estado, u.pass, " +
        "c.correo, t.telefono, r.nombre AS rol " +
        "FROM usuarios u " +
        "LEFT JOIN correo c ON c.usuario_id = u.id " +
        "LEFT JOIN telefono t ON t.usuario_id = u.id " +
        "LEFT JOIN roles_usuarios ru ON ru.usuario_id = u.id " +
        "LEFT JOIN roles r ON r.id = ru.rol_id " +
        "WHERE c.correo = ? AND u.pass = ?";

    public Usuario login(String correo, String pass) {
        Usuario user = null;
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_LOGIN)) {
            ps.setString(1, correo);
            ps.setString(2, encriptarMD5(pass));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new Usuario();
                    user.setId(rs.getInt("id"));
                    user.setNombre(rs.getString("nombre"));
                    user.setDocumento(rs.getString("documento"));
                    user.setDireccion(rs.getString("direccion"));
                    user.setEstado(rs.getString("estado"));
                    user.setPass(rs.getString("pass"));
                    user.setCorreo(rs.getString("correo"));
                    user.setTelefono(rs.getString("telefono"));
                    user.setRol(rs.getString("rol"));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return user;
    }

    public List<Map<String, String>> listarUsuarios() {
        List<Map<String, String>> lista = new ArrayList<>();
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT u.id, u.nombre, u.documento, u.direccion, u.estado, " +
                "c.correo, t.telefono, r.nombre AS rol " +
                "FROM usuarios u " +
                "LEFT JOIN correo c ON c.usuario_id = u.id " +
                "LEFT JOIN telefono t ON t.usuario_id = u.id " +
                "LEFT JOIN roles_usuarios ru ON ru.usuario_id = u.id " +
                "LEFT JOIN roles r ON r.id = ru.rol_id")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> usuario = new HashMap<>();
                    usuario.put("id", String.valueOf(rs.getInt("id")));
                    usuario.put("nombre", rs.getString("nombre"));
                    usuario.put("documento", rs.getString("documento"));
                    usuario.put("direccion", rs.getString("direccion"));
                    usuario.put("estado", rs.getString("estado"));
                    usuario.put("correo", rs.getString("correo"));
                    usuario.put("telefono", rs.getString("telefono"));
                    usuario.put("rol", rs.getString("rol"));
                    lista.add(usuario);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }
    public List<Map<String, String>> listarTrabajadores() {
        List<Map<String, String>> lista = new ArrayList<>();
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT u.id, u.nombre " +
                "FROM usuarios u " +
                "JOIN roles_usuarios ru ON ru.usuario_id = u.id " +
                "JOIN roles r ON r.id = ru.rol_id " +
                "WHERE r.nombre = 'Trabajador'")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> trabajador = new HashMap<>();
                    trabajador.put("id", String.valueOf(rs.getInt("id")));
                    trabajador.put("nombre", rs.getString("nombre"));
                    lista.add(trabajador);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }

    public boolean crear(String nombre, String pass, String documento, String direccion,
                         String estado, String correo, String telefono, int rolId, String rutaFoto) {
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO usuarios (nombre, pass, documento, direccion, estado) VALUES (?, ?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            ps.setString(2, encriptarMD5(pass));
            ps.setString(3, documento);
            ps.setString(4, direccion);
            ps.setString(5, estado);
            ps.executeUpdate();

            int usuarioId = 0;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) usuarioId = rs.getInt(1);
            }

            try (PreparedStatement psCorreo = conn.prepareStatement("INSERT INTO correo (correo, usuario_id) VALUES (?, ?)")) {
                psCorreo.setString(1, correo);
                psCorreo.setInt(2, usuarioId);
                psCorreo.executeUpdate();
            }

            try (PreparedStatement psTelefono = conn.prepareStatement("INSERT INTO telefono (telefono, usuario_id) VALUES (?, ?)")) {
                psTelefono.setString(1, telefono);
                psTelefono.setInt(2, usuarioId);
                psTelefono.executeUpdate();
            }

            try (PreparedStatement psRol = conn.prepareStatement("INSERT INTO roles_usuarios (usuario_id, rol_id) VALUES (?, ?)")) {
                psRol.setInt(1, usuarioId);
                psRol.setInt(2, rolId);
                psRol.executeUpdate();
            }

            if (rutaFoto != null && !rutaFoto.isEmpty()) {
                try (PreparedStatement psFoto = conn.prepareStatement("INSERT INTO fotos_usuario (usuario_id, ruta) VALUES (?, ?)")) {
                    psFoto.setInt(1, usuarioId);
                    psFoto.setString(2, rutaFoto);
                    psFoto.executeUpdate();
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean editarUsuario(String id, String nombre, String documento, String direccion,
                             String correo, String telefono, String pass, String rolId, String estado, String rutaFoto) {
    Connection conn = null;
    PreparedStatement ps = null;
    try {
        conn = Conexion.getConnection();

        // Actualizar tabla usuarios
        ps = conn.prepareStatement(
            "UPDATE usuarios SET nombre=?, documento=?, direccion=?, estado=?, pass=? WHERE id=?"
        );
        ps.setString(1, nombre);
        ps.setString(2, documento);
        ps.setString(3, direccion);
        ps.setString(4, estado);
        ps.setString(5, encriptarMD5(pass));
        ps.setInt(6, Integer.parseInt(id));
        ps.executeUpdate();
        ps.close();

        // Actualizar correo
        ps = conn.prepareStatement("UPDATE correo SET correo=? WHERE usuario_id=?");
        ps.setString(1, correo);
        ps.setInt(2, Integer.parseInt(id));
        ps.executeUpdate();
        ps.close();

        // Actualizar teléfono
        ps = conn.prepareStatement("UPDATE telefono SET telefono=? WHERE usuario_id=?");
        ps.setString(1, telefono);
        ps.setInt(2, Integer.parseInt(id));
        ps.executeUpdate();
        ps.close();

        // Actualizar rol
        ps = conn.prepareStatement("UPDATE roles_usuarios SET rol_id=? WHERE usuario_id=?");
        ps.setInt(1, Integer.parseInt(rolId));
        ps.setInt(2, Integer.parseInt(id));
        ps.executeUpdate();
        ps.close();

        // Actualizar foto si se subió una nueva
        if (rutaFoto != null && !rutaFoto.isEmpty()) {
            ps = conn.prepareStatement("UPDATE fotos_usuario SET ruta=? WHERE usuario_id=?");
            ps.setString(1, rutaFoto);
            ps.setInt(2, Integer.parseInt(id));
            int filas = ps.executeUpdate();
            ps.close();

            // Si no existía foto, insertar nueva
            if (filas == 0) {
                ps = conn.prepareStatement("INSERT INTO fotos_usuario (usuario_id, ruta) VALUES (?, ?)");
                ps.setInt(1, Integer.parseInt(id));
                ps.setString(2, rutaFoto);
                ps.executeUpdate();
                ps.close();
            }
        }

        return true;
    } catch (SQLException | ClassNotFoundException e) {
        e.printStackTrace();
        return false;
    } finally {
        try {
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


    public Map<String, String> obtenerPorId(String id) {
        Map<String, String> usuario = new HashMap<>();
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT u.id, u.nombre, u.documento, u.direccion, u.estado, " +
                "c.correo, t.telefono, r.nombre AS rol " +
                "FROM usuarios u " +
                "LEFT JOIN correo c ON c.usuario_id = u.id " +
                "LEFT JOIN telefono t ON t.usuario_id = u.id " +
                "LEFT JOIN roles_usuarios ru ON ru.usuario_id = u.id " +
                "LEFT JOIN roles r ON r.id = ru.rol_id WHERE u.id = ?")) {
            ps.setInt(1, Integer.parseInt(id));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario.put("id", String.valueOf(rs.getInt("id")));
                    usuario.put("nombre", rs.getString("nombre"));
                    usuario.put("documento", rs.getString("documento"));
                    usuario.put("direccion", rs.getString("direccion"));
                    usuario.put("estado", rs.getString("estado"));
                    usuario.put("correo", rs.getString("correo"));
                    usuario.put("telefono", rs.getString("telefono"));
                    usuario.put("rol", rs.getString("rol"));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return usuario;
    }

    public String obtenerFoto(int usuarioId) {
        String ruta = null;
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT ruta FROM fotos_usuario WHERE usuario_id = ?")) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) ruta = rs.getString("ruta");
            }
        } catch (Exception e) { e.printStackTrace(); }
        return ruta;
    }
}
