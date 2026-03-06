package com.agritrack.agritrackplus.DAO;

import com.agritrack.agritrackplus.db.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Registro_CultivoDAO {

    public boolean agregar(String nombre, String unidadMedida, double precio, String fechaCompra, String fechaVencimiento, int tipoProductoId, int cantidad) {
        String sql = "INSERT INTO productos(nombre, unidad_medida, precio, fecha_compra, fecha_vencimiento, tipo_producto_id, stock) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = com.agritrack.agritrackplus.db.Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, unidadMedida);
            ps.setDouble(3, precio);
            ps.setString(4, fechaCompra);
            ps.setString(5, fechaVencimiento); // El servlet ya lo pone como null si viene vacío
            ps.setInt(6, tipoProductoId);
            ps.setInt(7, cantidad);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean editar(String id, String nombre, String fechaSiembra, String fechaCosecha, String ciclo, String estado) {
        try (Connection conn = Conexion.getConexion()) {
            String sql = "UPDATE cultivos SET nombre=?, fecha_siembra=?, fecha_cosecha=?, ciclo=?, estado=? WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setString(2, fechaSiembra);
            ps.setString(3, (fechaCosecha != null && !fechaCosecha.isEmpty()) ? fechaCosecha : null);
            ps.setString(4, ciclo);
            ps.setString(5, estado);
            ps.setInt(6, Integer.parseInt(id));
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    
    public void eliminarTrabajadoresCultivo(int idCultivo) {
        try (Connection conn = Conexion.getConexion()) {
            String sql = "DELETE FROM cultivo_trabajador WHERE cultivo_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idCultivo);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

   public void asignarTrabajador(int cultivoId, int trabajadorId) {
        try (Connection conn = Conexion.getConexion()) {
            String sql = "INSERT INTO cultivo_trabajador(cultivo_id, usuario_id) VALUES(?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, cultivoId);
            ps.setInt(2, trabajadorId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void eliminarProductosCultivo(int idCultivo) {
        try (Connection conn = Conexion.getConexion()) {
            // CORREGIDO: Nombre de tabla stock_cultivo
            String sql = "DELETE FROM stock_cultivo WHERE cultivo_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idCultivo);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void asignarProducto(int cultivoId, int productoId) {
        String sql = "INSERT INTO stock_cultivo(cultivo_id, producto_id, cantidad) VALUES(?,?,?)";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cultivoId);
            ps.setInt(2, productoId);
            ps.setInt(3, 1); // <-- Cambia el 0 por 1 para que aparezca al menos una unidad
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminarSupervisorCultivo(int cultivoId) {
        try {
            Connection conn = Conexion.getConexion();
            String sql = "UPDATE cultivos SET supervisor_id=NULL WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, cultivoId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void asignarSupervisor(int cultivoId, int supervisorId) {
        try {
            Connection conn = Conexion.getConexion();
            String sql = "UPDATE cultivos SET supervisor_id=? WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, supervisorId);
            ps.setInt(2, cultivoId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean registrarCultivoCompleto(String nombre, String fechaSiembra, String ciclo,
            int supervisorId, String[] productoIds, String[] cantidades, String[] trabajadoresIds) {
        try {
            Connection conn = Conexion.getConexion();
            String sql = "INSERT INTO cultivos (nombre, fecha_siembra, ciclo, supervisor_id) VALUES (?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, nombre);
            ps.setString(2, fechaSiembra);
            ps.setString(3, ciclo);

            if (supervisorId == 0) {
                ps.setNull(4, java.sql.Types.INTEGER);
            } else {
                ps.setInt(4, supervisorId);
            }

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            int cultivoId = 0;
            if (rs.next()) {
                cultivoId = rs.getInt(1);
            }

            if (productoIds != null) {
                for (int i = 0; i < productoIds.length; i++) {
                    if (productoIds[i] != null && !productoIds[i].isEmpty()) {
                        int pId = Integer.parseInt(productoIds[i]);
                        int cant = (cantidades != null && i < cantidades.length) ? Integer.parseInt(cantidades[i]) : 1;
                        asignarProducto(cultivoId, pId);
                    }
                }
            }

            if (trabajadoresIds != null) {
                for (String tId : trabajadoresIds) {
                    if (tId != null && !tId.isEmpty()) {
                        asignarTrabajador(cultivoId, Integer.parseInt(tId));
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<Map<String, String>> listarCultivos() {
        List<Map<String, String>> lista = new ArrayList<>();
        try (Connection conn = Conexion.getConexion()) {
            String sql = "SELECT * FROM cultivos";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                Map<String, String> c = new HashMap<>();
                c.put("id", rs.getString("id"));
                c.put("nombre", rs.getString("nombre"));
                c.put("fecha_siembra", rs.getString("fecha_siembra"));
                c.put("fecha_cosecha", rs.getString("fecha_cosecha") != null ? rs.getString("fecha_cosecha") : "Pendiente");
                c.put("estado", rs.getString("estado"));
                lista.add(c);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }

    public Map<String, String> obtenerPorId(String id) {
        Map<String, String> cultivo = new HashMap<>();
        String sql = "SELECT * FROM cultivos WHERE id = ?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(id));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                cultivo.put("id", rs.getString("id"));
                cultivo.put("nombre", rs.getString("nombre"));
                cultivo.put("fecha_siembra", rs.getString("fecha_siembra"));
                cultivo.put("fecha_cosecha", rs.getString("fecha_cosecha"));
                cultivo.put("ciclo", rs.getString("ciclo"));
                cultivo.put("estado", rs.getString("estado"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return cultivo;
    }
   
    public List<Map<String, String>> obtenerProductosCultivo(int id) {
        List<Map<String, String>> lista = new ArrayList<>();
        // CORREGIDO: Tabla 'stock_cultivo' según tu SQL
        String sql = "SELECT p.nombre, sc.cantidad, p.unidad_medida FROM productos p " +
                     "JOIN stock_cultivo sc ON p.id = sc.producto_id WHERE sc.cultivo_id = ?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, String> m = new HashMap<>();
                m.put("nombre", rs.getString("nombre"));
                m.put("cantidad", rs.getString("cantidad"));
                m.put("unidad_medida", rs.getString("unidad_medida"));
                lista.add(m);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }

    public Map<String, String> obtenerSupervisorCultivo(int id) {
        Map<String, String> supervisor = new HashMap<>();
        // CORREGIDO: Consulta a la tabla 'supervisor' vinculada con 'usuarios'
        String sql = "SELECT u.nombre FROM usuarios u " +
                     "JOIN supervisor s ON u.id = s.usuario_id WHERE s.cultivo_id = ?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                supervisor.put("nombre", rs.getString("nombre"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return supervisor;
    }

        public List<Map<String, String>> obtenerTrabajadoresCultivo(int id) {
            List<Map<String, String>> lista = new ArrayList<>();
            // SQL: Agregamos u.id para poder identificar al trabajador en el JSP
            String sql = "SELECT u.id, u.nombre, f.ruta FROM usuarios u " +
                         "JOIN cultivo_trabajador ct ON u.id = ct.usuario_id " +
                         "LEFT JOIN fotos_usuario f ON u.id = f.usuario_id WHERE ct.cultivo_id = ?";
            try (Connection conn = com.agritrack.agritrackplus.db.Conexion.getConexion();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Map<String, String> m = new HashMap<>();
                    m.put("id", rs.getString("id")); // <--- ESTO ES LO QUE FALTA
                    m.put("nombre", rs.getString("nombre"));
                    m.put("foto", rs.getString("ruta"));
                    lista.add(m);
                }
            } catch (Exception e) { e.printStackTrace(); }
            return lista;
        }
}