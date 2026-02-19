package com.agritrack.agritrackplus.Dao;

import com.agritrack.agritrackplus.db.Conexion;
import com.agritrack.agritrackplus.modelo.Usuario;
import java.sql.*;

public class UsuarioDAO {

    // Consultas SQL
    private static final String SQL_INSERT_USUARIO = "INSERT INTO usuarios (nombre, documento, direccion, estado) VALUES (?, ?, ?, ?)";
    private static final String SQL_INSERT_CORREO = "INSERT INTO correo (correo, usuario_id) VALUES (?, ?)";
    private static final String SQL_INSERT_TELEFONO = "INSERT INTO telefono (telefono, usuario_id) VALUES (?, ?)";

    public boolean registrar(Usuario user) {
        Connection conn = null;
        PreparedStatement psUser = null;
        PreparedStatement psCorreo = null;
        PreparedStatement psTelefono = null;
        ResultSet rs = null;

        try {
            conn = Conexion.getConnection();
            conn.setAutoCommit(false); // IMPORTANTE: Para que si algo falla, no guarde nada (Transacción)

            // 1. Insertar en la tabla 'usuarios'
            psUser = conn.prepareStatement(SQL_INSERT_USUARIO, Statement.RETURN_GENERATED_KEYS);
            psUser.setString(1, user.getNombre());
            psUser.setString(2, user.getDocumento());
            psUser.setString(3, user.getDireccion());
            psUser.setString(4, "Activo"); // Estado por defecto
            psUser.executeUpdate();

            // Obtener el ID que MySQL le asignó a este usuario
            rs = psUser.getGeneratedKeys();
            int idGenerado = 0;
            if (rs.next()) {
                idGenerado = rs.getInt(1);
            }

            // 2. Insertar el Correo usando el ID generado
            psCorreo = conn.prepareStatement(SQL_INSERT_CORREO);
            psCorreo.setString(1, user.getCorreo());
            psCorreo.setInt(2, idGenerado);
            psCorreo.executeUpdate();

            // 3. Insertar el Teléfono usando el ID generado
            psTelefono = conn.prepareStatement(SQL_INSERT_TELEFONO);
            psTelefono.setString(1, user.getTelefono());
            psTelefono.setInt(2, idGenerado);
            psTelefono.executeUpdate();

            // Si todo salió bien, guardamos los cambios definitivamente
            conn.commit();
            return true;

        } catch (SQLException | ClassNotFoundException e) {
            // Si hubo un error, cancelamos todo lo que se alcanzó a hacer
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            // Cerramos todas las conexiones
            try {
                if (rs != null) rs.close();
                if (psUser != null) psUser.close();
                if (psCorreo != null) psCorreo.close();
                if (psTelefono != null) psTelefono.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
            ps.setString(2, pass);
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
}