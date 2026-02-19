package com.agritrack.agritrackplus.DAO;

import com.agritrack.agritrackplus.db.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class Registro_CultivoDAO {

    private static final String SQL_INSERT_CULTIVO =
    "INSERT INTO cultivos (nombre, fecha_siembra, fecha_cosecha, ciclo, estado) VALUES (?, ?, ?, ?, ?)";

    public boolean registrar(String nombre, String fechaSiembra, String fechaCosecha, String ciclo, String estado) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_INSERT_CULTIVO);
            ps.setString(1, nombre);
            ps.setString(2, fechaSiembra);
            ps.setString(3, fechaCosecha);
            ps.setString(4, ciclo);
            ps.setString(5, estado);
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
}