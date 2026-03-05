package com.agritrack.agritrackplus.DAO;

import com.agritrack.agritrackplus.db.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CultivoDAO {

    // Método para listar TODOS los cultivos (Lo que no te salía)
    public List<Map<String, String>> listarCultivos() throws SQLException, ClassNotFoundException {
        List<Map<String, String>> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, fecha_siembra, fecha_cosecha, ciclo FROM cultivos ORDER BY id DESC";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, String> c = new HashMap<>();
                c.put("id", String.valueOf(rs.getInt("id")));
                c.put("nombre", rs.getString("nombre"));
                c.put("fecha_siembra", rs.getString("fecha_siembra"));
                
                // Si la fecha es NULL (porque no se ha determinado), ponemos "Pendiente"
                String cosecha = rs.getString("fecha_cosecha");
                c.put("fecha_cosecha", (cosecha == null) ? "Pendiente" : cosecha);
                
                c.put("ciclo", rs.getString("ciclo"));
                lista.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Método para registrar un cultivo (Maneja el error de fecha_cosecha)
    public boolean registrarCultivo(String nombre, String fechaSiembra, String ciclo) throws SQLException, ClassNotFoundException {
        // No insertamos fecha_cosecha porque por defecto es NULL en tu DB
        String sql = "INSERT INTO cultivos (nombre, fecha_siembra, ciclo) VALUES (?, ?, ?)";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nombre);
            ps.setString(2, fechaSiembra); // Formato YYYY-MM-DD
            ps.setString(3, ciclo);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
        public int contarCultivosPorRol(int idUsuario, String rol) {
        String sql = "administrador".equalsIgnoreCase(rol) ? "SELECT COUNT(*) FROM cultivos" : 
                     "SELECT COUNT(*) FROM supervisor WHERE usuario_id = ?";
        try (Connection con = Conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            if (!"administrador".equalsIgnoreCase(rol)) ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
       
}