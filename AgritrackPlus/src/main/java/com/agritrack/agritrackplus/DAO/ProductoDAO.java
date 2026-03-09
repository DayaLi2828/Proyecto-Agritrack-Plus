package com.agritrack.agritrackplus.DAO;

import com.agritrack.agritrackplus.db.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductoDAO {

   public List<Map<String, String>> listarProductos() {
    List<Map<String, String>> lista = new ArrayList<>();
    String sql = "SELECT * FROM productos";
    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            Map<String, String> p = new HashMap<>();
            p.put("id", rs.getString("id"));
            p.put("nombre", rs.getString("nombre"));
            p.put("unidad_medida", rs.getString("unidad_medida"));
            // AGREGA ESTAS LÍNEAS PARA TRAER EL RESTO DE DATOS:
            p.put("precio", rs.getString("precio")); 
            p.put("estado", rs.getString("estado"));
            p.put("tipo_producto", rs.getString("tipo_producto"));
            p.put("fecha_compra", rs.getString("fecha_compra"));
            p.put("fecha_vencimiento", rs.getString("fecha_vencimiento"));
            p.put("cantidad", rs.getString("cantidad"));
            
            lista.add(p);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return lista;
}

    public boolean editarProducto(int id, String nombre, String unidad) {
        String sql = "UPDATE productos SET nombre = ?, unidad_medida = ? WHERE id = ?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, unidad);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean insertarProducto(String nombre, String tipo, String unidad, double precio, int cantidad, String fechaCompra, String fechaVencimiento, String estado) {
        String sql = "INSERT INTO productos (nombre, tipo_producto, unidad_medida, precio, cantidad, fecha_compra, fecha_vencimiento, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, tipo);
            ps.setString(3, unidad);
            ps.setDouble(4, precio);
            ps.setInt(5, cantidad);
            ps.setString(6, fechaCompra);
            ps.setString(7, fechaVencimiento);
            ps.setString(8, estado);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}