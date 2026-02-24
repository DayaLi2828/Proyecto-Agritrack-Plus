package com.agritrack.agritrackplus.DAO;

import com.agritrack.agritrackplus.db.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductoDAO {


    private static final String SQL_INSERT =
        "INSERT INTO productos (nombre, unidad_medida, precio, fecha_compra, fecha_vencimiento, estado, tipo_producto_id, cantidad) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    public boolean agregar(String nombre, double unidadMedida, double precio, String fechaCompra, 
                          String fechaVencimiento, String estado, int tipoProductoId, int cantidad) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(SQL_INSERT);
            ps.setString(1, nombre);
            ps.setDouble(2, unidadMedida);
            ps.setDouble(3, precio);

            // Fecha de compra siempre requerida
            ps.setDate(4, java.sql.Date.valueOf(fechaCompra));

            // Fecha de vencimiento puede ser opcional
            if (fechaVencimiento != null && !fechaVencimiento.trim().isEmpty()) {
                ps.setDate(5, java.sql.Date.valueOf(fechaVencimiento));
            } else {
                ps.setNull(5, java.sql.Types.DATE);
            }

            ps.setString(6, estado);
            ps.setInt(7, tipoProductoId);
            ps.setInt(8, cantidad);

            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (Exception e) {
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
             // ✅ AGREGAR CANTIDAD AL SELECT
             ps = conn.prepareStatement(
                 "SELECT p.id, p.nombre, p.unidad_medida, p.precio, p.fecha_compra, " +
                 "p.fecha_vencimiento, p.estado, p.cantidad, t.tipo_nombre " +  // ✅ CANTIDAD AQUÍ
                 "FROM productos p " +
                 "JOIN tipo_producto t ON t.id = p.tipo_producto_id"
             );
             rs = ps.executeQuery();

             while (rs.next()) {
                 Map<String, String> producto = new HashMap<>();
                 producto.put("id", String.valueOf(rs.getInt("id")));
                 producto.put("nombre", rs.getString("nombre"));
                 producto.put("unidad_medida", rs.getString("unidad_medida"));
                 producto.put("precio", String.valueOf(rs.getDouble("precio")));
                 // ✅ CANTIDAD CORREGIDO
                 producto.put("cantidad", String.valueOf(rs.getInt("cantidad")));  // Línea 70
                 producto.put("fecha_compra", rs.getString("fecha_compra") != null ? rs.getString("fecha_compra") : "");
                 producto.put("fecha_vencimiento", rs.getString("fecha_vencimiento") != null ? rs.getString("fecha_vencimiento") : "");
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

    
    // Para filtrar productos (búsqueda y filtro por tipo)
    public List<Map<String, String>> filtrarProductos(String busqueda, String filtroTipo) {
        List<Map<String, String>> lista = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = Conexion.getConnection();
            
            // Construir consulta dinámica con parámetros seguros
            StringBuilder sql = new StringBuilder(
                "SELECT p.id, p.nombre, p.unidad_medida, p.precio, p.fecha_compra, " +
                "p.fecha_vencimiento, p.estado, t.tipo_nombre " +
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
            
            ps = conn.prepareStatement(sql.toString());
            
            // Asignar parámetros
            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }
            
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, String> producto = new HashMap<>();
                producto.put("id", String.valueOf(rs.getInt("id")));
                producto.put("nombre", rs.getString("nombre"));
                producto.put("unidad_medida", rs.getString("unidad_medida"));
                producto.put("precio", String.valueOf(rs.getDouble("precio")));
                producto.put("cantidad", String.valueOf(rs.getInt("cantidad")));
                Date fechaCompra = rs.getDate("fecha_compra");
                producto.put("fecha_compra", fechaCompra != null ? fechaCompra.toString() : "");
                
                Date fechaVencimiento = rs.getDate("fecha_vencimiento");
                producto.put("fecha_vencimiento", fechaVencimiento != null ? fechaVencimiento.toString() : "");
                
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