package com.agritrack.agritrackplus.DAO;

import com.agritrack.agritrackplus.db.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class ProductoDAO {

    private static final String SQL_INSERT =
        "INSERT INTO productos (nombre, unidad_medida, precio, fecha_compra, fecha_vencimiento, estado, tipo_producto_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

    public boolean agregar(String nombre, double unidadMedida, double precio, String fechaCompra, String fechaVencimiento, String estado, int tipoProductoId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_INSERT);
            ps.setString(1, nombre);
            ps.setDouble(2, unidadMedida);
            ps.setDouble(3, precio);
            ps.setString(4, fechaCompra);
            ps.setString(5, fechaVencimiento);
            ps.setString(6, estado);
            ps.setInt(7, tipoProductoId);
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
    public List<Map<String, String>> listarProductos() {
        List<Map<String, String>> lista = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
    try {
        conn = Conexion.getConnection();
        ps = conn.prepareStatement(
            "SELECT p.id, p.nombre, p.unidad_medida, p.precio, p.fecha_compra, " +
            "p.fecha_vencimiento, p.estado, t.tipo_nombre " +
            "FROM productos p " +
            "JOIN tipo_producto t ON t.id = p.tipo_producto_id"
        );
        rs = ps.executeQuery();
        while (rs.next()) {
            Map<String, String> producto = new HashMap<>();
            producto.put("id", String.valueOf(rs.getInt("id")));
            producto.put("nombre", rs.getString("nombre"));
            producto.put("unidad_medida", String.valueOf(rs.getDouble("unidad_medida")));
            producto.put("precio", String.valueOf(rs.getDouble("precio")));
            producto.put("fecha_compra", rs.getString("fecha_compra"));
            producto.put("fecha_vencimiento", rs.getString("fecha_vencimiento"));
            producto.put("estado", rs.getString("estado"));
            producto.put("tipo", rs.getString("tipo_nombre"));
            lista.add(producto);
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