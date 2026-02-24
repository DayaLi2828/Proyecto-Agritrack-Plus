package com.agritrack.agritrackplus.DAO;

import com.agritrack.agritrackplus.db.Conexion;
import com.agritrack.agritrackplus.modelo.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Usuario user = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_LOGIN);
            ps.setString(1, correo);
            ps.setString(2, encriptarMD5(pass));
            rs = ps.executeQuery();
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
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return user;
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
                "c.correo, t.telefono, r.nombre AS rol " +
                "FROM usuarios u " +
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
                usuario.put("correo", rs.getString("correo"));
                usuario.put("telefono", rs.getString("telefono"));
                usuario.put("rol", rs.getString("rol"));
                lista.add(usuario);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return lista;
    }

    public boolean crear(String nombre, String pass, String documento, String direccion, String estado, String correo, String telefono, int rolId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(
                "INSERT INTO usuarios (nombre, pass, documento, direccion, estado) VALUES (?, ?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, nombre);
            ps.setString(2, encriptarMD5(pass));
            ps.setString(3, documento);
            ps.setString(4, direccion);
            ps.setString(5, estado);
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            int usuarioId = 0;
            if (rs.next()) {
                usuarioId = rs.getInt(1);
            }

            PreparedStatement psCorreo = conn.prepareStatement(
                "INSERT INTO correo (correo, usuario_id) VALUES (?, ?)"
            );
            psCorreo.setString(1, correo);
            psCorreo.setInt(2, usuarioId);
            psCorreo.executeUpdate();

            PreparedStatement psTelefono = conn.prepareStatement(
                "INSERT INTO telefono (telefono, usuario_id) VALUES (?, ?)"
            );
            psTelefono.setString(1, telefono);
            psTelefono.setInt(2, usuarioId);
            psTelefono.executeUpdate();

            PreparedStatement psRol = conn.prepareStatement(
                "INSERT INTO roles_usuarios (usuario_id, rol_id) VALUES (?, ?)"
            );
            psRol.setInt(1, usuarioId);
            psRol.setInt(2, rolId);
            psRol.executeUpdate();

            return true;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}