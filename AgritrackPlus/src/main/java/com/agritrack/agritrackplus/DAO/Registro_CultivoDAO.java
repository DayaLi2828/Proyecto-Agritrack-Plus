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

      
     public int registrar(String nombre, String fechaSiembra, String ciclo, String estado) {
         Connection conn = null;
         PreparedStatement ps = null;
         ResultSet rs = null;
         try {
             conn = Conexion.getConnection();
             ps = conn.prepareStatement(
                 "INSERT INTO cultivos (nombre, fecha_siembra, ciclo, estado) VALUES (?, ?, ?, ?)",
                 PreparedStatement.RETURN_GENERATED_KEYS
             );
             ps.setString(1, nombre);
             ps.setString(2, fechaSiembra);
             ps.setString(3, ciclo);
             ps.setString(4, estado);
             ps.executeUpdate();
             rs = ps.getGeneratedKeys();
             if (rs.next()) {
                 return rs.getInt(1);
             }
             return 0;
         } catch (SQLException | ClassNotFoundException e) {
             e.printStackTrace();
             return 0;
         } finally {
             try {
                 if (rs != null) rs.close();
                 if (ps != null) ps.close();
                 if (conn != null) conn.close();
             } catch (SQLException e) {
                 e.printStackTrace();
             }
         }
     }
     public void asignarTrabajador(int cultivoId, int usuarioId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(
                "INSERT INTO cultivo_trabajador (cultivo_id, usuario_id) VALUES (?, ?)"
            );
            ps.setInt(1, cultivoId);
            ps.setInt(2, usuarioId);
            ps.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        }   public void eliminarTrabajadoresCultivo(int cultivoId) {
            Connection conn = null;
            PreparedStatement ps = null;
            try {
                conn = Conexion.getConnection();
                ps = conn.prepareStatement("DELETE FROM cultivo_trabajador WHERE cultivo_id = ?");
                ps.setInt(1, cultivoId);
                ps.executeUpdate();
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ps != null) ps.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }


    public List<Map<String, String>> obtenerTrabajadoresCultivo(int cultivoId) {
        List<Map<String, String>> lista = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(
                "SELECT u.id, u.nombre, u.foto FROM cultivo_trabajador ct " +
                "JOIN usuarios u ON u.id = ct.usuario_id " +
                "WHERE ct.cultivo_id = ?"
            );
            ps.setInt(1, cultivoId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, String> trabajador = new HashMap<>();
                trabajador.put("id", String.valueOf(rs.getInt("id")));
                trabajador.put("nombre", rs.getString("nombre"));
                trabajador.put("foto", rs.getString("foto"));
                lista.add(trabajador);
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
    public List<Map<String, String>> obtenerProductosCultivo(int cultivoId) {
        List<Map<String, String>> lista = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(
                "SELECT p.nombre, sc.cantidad, p.unidad_medida " +
                "FROM stock_cultivo sc " +
                "JOIN productos p ON p.id = sc.producto_id " +
                "WHERE sc.cultivo_id = ?"
            );
            ps.setInt(1, cultivoId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, String> producto = new HashMap<>();
                producto.put("nombre", rs.getString("nombre"));
                producto.put("cantidad", String.valueOf(rs.getInt("cantidad")));
                producto.put("unidad_medida", rs.getString("unidad_medida"));
                lista.add(producto);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return lista;
    }

    public Map<String, String> obtenerSupervisorCultivo(int cultivoId) {
        Map<String, String> supervisor = new HashMap<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(
                "SELECT u.nombre FROM supervisor s " +
                "JOIN usuarios u ON u.id = s.usuario_id " +
                "WHERE s.cultivo_id = ?"
            );
            ps.setInt(1, cultivoId);
            rs = ps.executeQuery();
            if (rs.next()) {
                supervisor.put("nombre", rs.getString("nombre"));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return supervisor;
    }
         // Asignar supervisor al cultivo
    public void asignarSupervisor(int cultivoId, int usuarioId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(
                "INSERT INTO supervisor (cultivo_id, usuario_id) VALUES (?, ?)"
            );
            ps.setInt(1, cultivoId);
            ps.setInt(2, usuarioId);
            ps.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
     // Asignar producto al cultivo
    public void asignarProducto(int cultivoId, int productoId, int cantidad) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Conexion.getConnection();
            ps = conn.prepareStatement(
                "INSERT INTO stock_cultivo (cultivo_id, producto_id, cantidad) VALUES (?, ?, ?)"
            );
            ps.setInt(1, cultivoId);
            ps.setInt(2, productoId);
            ps.setInt(3, cantidad);
            ps.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
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