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
        // Usamos un JOIN para traer el nombre del tipo de producto desde la tabla tipo_producto
        String sql = "SELECT p.*, t.tipo_nombre FROM productos p " +
                     "JOIN tipo_producto t ON p.tipo_producto_id = t.id";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, String> p = new HashMap<>();
                p.put("id", rs.getString("id"));
                p.put("nombre", rs.getString("nombre"));
                p.put("unidad_medida", rs.getString("unidad_medida"));
                p.put("precio", rs.getString("precio"));
                p.put("cantidad", rs.getString("cantidad"));
                p.put("fecha_compra", rs.getString("fecha_compra"));
                p.put("fecha_vencimiento", rs.getString("fecha_vencimiento"));
                p.put("tipo_producto", rs.getString("tipo_nombre"));

                // Ahora lee el estado real de la base de datos
                p.put("estado", rs.getString("estado")); 

                lista.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean editarProducto(int id, String nombre, String unidad, double precio, int cantidad, int tipoId) {
        // Método actualizado para que la edición sea funcional con todos los campos
        String sql = "UPDATE productos SET nombre = ?, unidad_medida = ?, precio = ?, cantidad = ?, tipo_producto_id = ? WHERE id = ?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, unidad);
            ps.setDouble(3, precio);
            ps.setInt(4, cantidad);
            ps.setInt(5, tipoId);
            ps.setInt(6, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertarProducto(String nombre, String tipoId, String unidad, double precio, int cantidad, String fechaCompra, String fechaVencimiento) {
        // Se agregó 'estado' al INSERT para que no sea nulo al crear
        String sql = "INSERT INTO productos (nombre, tipo_producto_id, unidad_medida, precio, cantidad, fecha_compra, fecha_vencimiento, estado) VALUES (?, ?, ?, ?, ?, ?, ?, 'Activo')";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, Integer.parseInt(tipoId));
            ps.setString(3, unidad);
            ps.setDouble(4, precio);
            ps.setInt(5, cantidad);
            ps.setString(6, fechaCompra);
            ps.setString(7, fechaVencimiento);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para eliminar producto
    public boolean eliminarProducto(int id) {
        String sql = "DELETE FROM productos WHERE id = ?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para cambiar el estado (Activo/Inactivo)
    public boolean cambiarEstado(int id, String nuevoEstado) {
        String sql = "UPDATE productos SET estado = ? WHERE id = ?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public Map<String, String> buscarProductoPorId(int id) {
        Map<String, String> p = new HashMap<>();
        // SQL para obtener todos los campos de un producto específico
        String sql = "SELECT * FROM productos WHERE id = ?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    p.put("id", rs.getString("id"));
                    p.put("nombre", rs.getString("nombre"));
                    p.put("unidad_medida", rs.getString("unidad_medida"));
                    p.put("precio", rs.getString("precio"));
                    p.put("cantidad", rs.getString("cantidad"));
                    p.put("fecha_compra", rs.getString("fecha_compra"));
                    p.put("fecha_vencimiento", rs.getString("fecha_vencimiento"));
                    p.put("tipo_producto_id", rs.getString("tipo_producto_id"));
                    p.put("estado", rs.getString("estado"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p;
    }
}