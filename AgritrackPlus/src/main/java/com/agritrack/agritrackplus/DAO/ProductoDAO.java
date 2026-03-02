package com.agritrack.agritrackplus.DAO;

import com.agritrack.agritrackplus.db.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductoDAO {

    // SQL actualizado sin el campo estado
    private static final String SQL_INSERT =
        "INSERT INTO productos (nombre, unidad_medida, precio, fecha_compra, fecha_vencimiento, tipo_producto_id, cantidad) VALUES (?, ?, ?, ?, ?, ?, ?)";

    public boolean agregar(String nombre, String unidadMedida, double precio, String fechaCompra, String fechaVencimiento, int tipoProductoId, int cantidad) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_INSERT);
            ps.setString(1, nombre);
            ps.setString(2, unidadMedida);
            ps.setDouble(3, precio);
            ps.setDate(4, java.sql.Date.valueOf(fechaCompra));
            
            if (fechaVencimiento != null && !fechaVencimiento.trim().isEmpty()) {
                ps.setDate(5, java.sql.Date.valueOf(fechaVencimiento));
            } else {
                ps.setNull(5, java.sql.Types.DATE);
            }
            
            ps.setInt(6, tipoProductoId);
            ps.setInt(7, cantidad);
            ps.executeUpdate();
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        } finally {
            cerrarRecursos(conn, ps, null);
        }
    }

    public List<Map<String, String>> listarProductos() {
        List<Map<String, String>> lista = new ArrayList<>();
        String sql = "SELECT p.id, p.nombre, p.unidad_medida, p.precio, p.fecha_compra, " +
                     "p.fecha_vencimiento, p.cantidad, t.tipo_nombre " + 
                     "FROM productos p " +
                     "JOIN tipo_producto t ON t.id = p.tipo_producto_id";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, String> producto = new HashMap<>();
                producto.put("id", String.valueOf(rs.getInt("id")));
                producto.put("nombre", rs.getString("nombre"));
                producto.put("unidad_medida", rs.getString("unidad_medida"));
                producto.put("precio", String.valueOf(rs.getDouble("precio")));
                producto.put("cantidad", String.valueOf(rs.getInt("cantidad"))); 
                producto.put("fecha_compra", rs.getString("fecha_compra") != null ? rs.getString("fecha_compra") : "");
                producto.put("fecha_vencimiento", rs.getString("fecha_vencimiento") != null ? rs.getString("fecha_vencimiento") : "N/A");
                producto.put("tipo", rs.getString("tipo_nombre"));
                lista.add(producto);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Map<String, String>> filtrarProductos(String busqueda, String filtroTipo) {
        List<Map<String, String>> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT p.id, p.nombre, p.unidad_medida, p.precio, p.fecha_compra, " +
            "p.fecha_vencimiento, p.cantidad, t.tipo_nombre " +
            "FROM productos p " +
            "JOIN tipo_producto t ON t.id = p.tipo_producto_id " +
            "WHERE 1=1"
        );
        
        List<Object> parametros = new ArrayList<>();
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            sql.append(" AND p.nombre LIKE ?");
            parametros.add("%" + busqueda + "%");
        }
        if (filtroTipo != null && !"todos".equals(filtroTipo)) {
            sql.append(" AND t.tipo_nombre = ?");
            parametros.add(filtroTipo);
        }
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> producto = new HashMap<>();
                    producto.put("id", String.valueOf(rs.getInt("id")));
                    producto.put("nombre", rs.getString("nombre"));
                    producto.put("unidad_medida", rs.getString("unidad_medida"));
                    producto.put("precio", String.valueOf(rs.getDouble("precio")));
                    producto.put("cantidad", String.valueOf(rs.getInt("cantidad")));
                    producto.put("fecha_compra", rs.getString("fecha_compra") != null ? rs.getString("fecha_compra") : "");
                    producto.put("fecha_vencimiento", rs.getString("fecha_vencimiento") != null ? rs.getString("fecha_vencimiento") : "N/A");
                    producto.put("tipo", rs.getString("tipo_nombre"));
                    lista.add(producto);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private void cerrarRecursos(Connection conn, PreparedStatement ps, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}