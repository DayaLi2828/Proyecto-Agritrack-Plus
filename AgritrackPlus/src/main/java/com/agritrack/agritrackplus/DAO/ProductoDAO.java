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

    // Método para listar todos los productos (usado en los checkboxes del JSP)
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
}