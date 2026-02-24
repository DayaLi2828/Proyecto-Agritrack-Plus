package com.agritrack.agritrackplus.DAO;

import com.agritrack.agritrackplus.db.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class Registro_CultivoDAO {

    private static final String SQL_INSERT_CULTIVO =
    "INSERT INTO cultivos (nombre, fecha_siembra, ciclo, estado) VALUES (?, ?, ?, ?)";

    public boolean registrar(String nombre, String fechaSiembra, String ciclo, String estado) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_INSERT_CULTIVO);
            ps.setString(1, nombre);
            ps.setString(2, fechaSiembra);
            ps.setString(3, ciclo);
            ps.setString(4, estado);
            ps.executeUpdate();
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
    Map<String, String> cultivo = new HashMap<>();
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
        conn = Conexion.getConnection();
        ps = conn.prepareStatement("SELECT * FROM cultivos WHERE id = ?");
        ps.setInt(1, Integer.parseInt(id));
        rs = ps.executeQuery();
        if (rs.next()) {
            cultivo.put("id", String.valueOf(rs.getInt("id")));
            cultivo.put("nombre", rs.getString("nombre"));
            cultivo.put("fecha_siembra", rs.getString("fecha_siembra"));
            cultivo.put("fecha_cosecha", rs.getString("fecha_cosecha"));
            cultivo.put("ciclo", rs.getString("ciclo"));
            cultivo.put("estado", rs.getString("estado"));
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
    return cultivo;
}
    public List<Map<String, String>> listarCultivos() {
    List<Map<String, String>> lista = new ArrayList<>();
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
        conn = Conexion.getConnection();
        ps = conn.prepareStatement("SELECT id, nombre, fecha_siembra, estado FROM cultivos");
        rs = ps.executeQuery();
        while (rs.next()) {
            Map<String, String> cultivo = new HashMap<>();
            cultivo.put("id", String.valueOf(rs.getInt("id")));
            cultivo.put("nombre", rs.getString("nombre"));
            cultivo.put("fecha_siembra", rs.getString("fecha_siembra"));
            cultivo.put("estado", rs.getString("estado"));
            lista.add(cultivo);
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
    public boolean editar(String id, String nombre, String fechaSiembra, String fechaCosecha, String ciclo, String estado) {
    Connection conn = null;
    PreparedStatement ps = null;
    try {
        conn = Conexion.getConnection();
        ps = conn.prepareStatement(
            "UPDATE cultivos SET nombre=?, fecha_siembra=?, fecha_cosecha=?, ciclo=?, estado=? WHERE id=?"
        );
        ps.setString(1, nombre);
        ps.setString(2, fechaSiembra);
        ps.setString(3, fechaCosecha.isEmpty() ? null : fechaCosecha);
        ps.setString(4, ciclo);
        ps.setString(5, estado);
        ps.setInt(6, Integer.parseInt(id));
        ps.executeUpdate();
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
}