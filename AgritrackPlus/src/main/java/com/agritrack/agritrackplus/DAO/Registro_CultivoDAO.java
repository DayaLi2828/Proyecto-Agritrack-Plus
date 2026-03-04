package com.agritrack.agritrackplus.DAO;

import com.agritrack.agritrackplus.db.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Registro_CultivoDAO {

    public boolean registrarCultivoCompleto(String nombre, String fecha, String ciclo, int supervisorId, String[] productos, String[] cantidades, String[] trabajadores) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = Conexion.getConnection();
            conn.setAutoCommit(false); 

      
            String sqlCultivo = "INSERT INTO cultivos (nombre, fecha_siembra, fecha_cosecha, ciclo) VALUES (?, ?, NULL, ?)";
            ps = conn.prepareStatement(sqlCultivo, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, nombre);
            ps.setString(2, fecha);
            ps.setString(3, ciclo);
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            int idCultivo = 0;
            if (rs.next()) {
                idCultivo = rs.getInt(1);
            }

            if (idCultivo > 0) {
                // 2. Insertar en la tabla 'supervisor'
                String sqlSup = "INSERT INTO supervisor (cultivo_id, usuario_id) VALUES (?, ?)";
                try (PreparedStatement psSup = conn.prepareStatement(sqlSup)) {
                    psSup.setInt(1, idCultivo);
                    psSup.setInt(2, supervisorId);
                    psSup.executeUpdate();
                }

                // 3. Insertar Productos
                if (productos != null) {
                    String sqlProd = "INSERT INTO stock_cultivo (cultivo_id, producto_id, cantidad) VALUES (?, ?, ?)";
                    try (PreparedStatement psProd = conn.prepareStatement(sqlProd)) {
                        for (int i = 0; i < productos.length; i++) {
                            if (productos[i] != null && !productos[i].isEmpty()) {
                                psProd.setInt(1, idCultivo);
                                psProd.setInt(2, Integer.parseInt(productos[i]));
                                psProd.setInt(3, Integer.parseInt(cantidades[i]));
                                psProd.addBatch();
                            }
                        }
                        psProd.executeBatch();
                    }
                }

                // 4. Insertar Trabajadores
                if (trabajadores != null) {
                    String sqlTrab = "INSERT INTO cultivo_trabajador (cultivo_id, usuario_id) VALUES (?, ?)";
                    try (PreparedStatement psTrab = conn.prepareStatement(sqlTrab)) {
                        for (String idT : trabajadores) {
                            if (idT != null && !idT.isEmpty()) {
                                psTrab.setInt(1, idCultivo);
                                psTrab.setInt(2, Integer.parseInt(idT));
                                psTrab.addBatch();
                            }
                        }
                        psTrab.executeBatch();
                    }
                }

                conn.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            return false;
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
    }

    public boolean editar(String id, String nombre, String fechaSiembra, String fechaCosecha, String ciclo, String estado) {
        // CORRECCIÓN 2: Si el error persiste, asegúrate de que 'estado' esté en la DB o quítalo de aquí también
        String sql = "UPDATE cultivos SET nombre=?, fecha_siembra=?, fecha_cosecha=?, ciclo=? WHERE id=?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, fechaSiembra);
            if (fechaCosecha == null || fechaCosecha.trim().isEmpty()) {
                ps.setNull(3, java.sql.Types.DATE);
            } else {
                ps.setString(3, fechaCosecha);
            }
            ps.setString(4, ciclo);
            ps.setInt(5, Integer.parseInt(id));
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public void asignarTrabajador(int cultivoId, int usuarioId) {
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO cultivo_trabajador (cultivo_id, usuario_id) VALUES (?, ?)")) {
            ps.setInt(1, cultivoId);
            ps.setInt(2, usuarioId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public List<Map<String, String>> obtenerTrabajadoresCultivo(int cultivoId) {
        List<Map<String, String>> lista = new ArrayList<>();
        String sql = "SELECT u.id, u.nombre, f.ruta AS foto FROM cultivo_trabajador ct " +
                     "JOIN usuarios u ON u.id = ct.usuario_id " +
                     "LEFT JOIN fotos_usuario f ON u.id = f.usuario_id WHERE ct.cultivo_id = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cultivoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> t = new HashMap<>();
                    t.put("id", String.valueOf(rs.getInt("id")));
                    t.put("nombre", rs.getString("nombre"));
                    t.put("foto", rs.getString("foto"));
                    lista.add(t);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }

    public Map<String, String> obtenerPorId(String id) {
        Map<String, String> cultivo = new HashMap<>();
        // CORRECCIÓN 3: Especificamos columnas seguras
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id, nombre, fecha_siembra, fecha_cosecha, ciclo FROM cultivos WHERE id = ?")) {
            ps.setInt(1, Integer.parseInt(id));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cultivo.put("id", String.valueOf(rs.getInt("id")));
                    cultivo.put("nombre", rs.getString("nombre"));
                    cultivo.put("fecha_siembra", rs.getString("fecha_siembra"));
                    cultivo.put("fecha_cosecha", rs.getString("fecha_cosecha"));
                    cultivo.put("ciclo", rs.getString("ciclo"));
                    cultivo.put("estado", "Activo"); // Valor manual
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return cultivo;
    }
    public List<Map<String, String>> obtenerProductosCultivo(int cultivoId) {
        List<Map<String, String>> lista = new ArrayList<>();
        // Asegúrate de que los nombres de las columnas (producto_id, cultivo_id) sean exactos a tu DB
        String sql = "SELECT p.nombre, sc.cantidad, p.unidad_medida " +
                     "FROM stock_cultivo sc " +
                     "JOIN productos p ON p.id = sc.producto_id " +
                     "WHERE sc.cultivo_id = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cultivoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> p = new HashMap<>();
                    p.put("nombre", rs.getString("nombre"));
                    p.put("cantidad", String.valueOf(rs.getInt("cantidad")));
                    p.put("unidad_medida", rs.getString("unidad_medida"));
                    lista.add(p);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }

    public Map<String, String> obtenerSupervisorCultivo(int cultivoId) {
        Map<String, String> supervisor = new HashMap<>();
        // Usamos LEFT JOIN para que si hay algún error con el usuario, al menos sepamos que algo falló y no devuelva vacío
        String sql = "SELECT u.nombre FROM supervisor s " +
                     "LEFT JOIN usuarios u ON u.id = s.usuario_id " +
                     "WHERE s.cultivo_id = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cultivoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String nombre = rs.getString("nombre");
                    supervisor.put("nombre", (nombre != null) ? nombre : "Usuario no encontrado");
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return supervisor;
    }
    
    public void asignarSupervisor(int cultivoId, int usuarioId) {
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO supervisor (cultivo_id, usuario_id) VALUES (?, ?)")) {
            ps.setInt(1, cultivoId);
            ps.setInt(2, usuarioId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void asignarProducto(int cultivoId, int productoId, int cantidad) {
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO stock_cultivo (cultivo_id, producto_id, cantidad) VALUES (?, ?, ?)")) {
            ps.setInt(1, cultivoId);
            ps.setInt(2, productoId);
            ps.setInt(3, cantidad);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void eliminarTrabajadoresCultivo(int cultivoId) {
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM cultivo_trabajador WHERE cultivo_id = ?")) {
            ps.setInt(1, cultivoId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void eliminarProductosCultivo(int cultivoId) {
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM stock_cultivo WHERE cultivo_id = ?")) {
            ps.setInt(1, cultivoId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void eliminarSupervisorCultivo(int cultivoId) {
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM supervisor WHERE cultivo_id = ?")) {
            ps.setInt(1, cultivoId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
    public boolean actualizarTrabajadoresCultivo(int cultivoId, String[] nuevosTrabajadoresIds) {
        Connection conn = null;
        try {
            conn = Conexion.getConnection();
            conn.setAutoCommit(false); // Empezamos una transacción segura

            // 1. Limpiamos los trabajadores viejos para ese cultivo
            String sqlDelete = "DELETE FROM cultivo_trabajador WHERE cultivo_id = ?";
            try (PreparedStatement psDelete = conn.prepareStatement(sqlDelete)) {
                psDelete.setInt(1, cultivoId);
                psDelete.executeUpdate();
            }

            // 2. Insertamos los nuevos (si es que seleccionaste alguno)
            if (nuevosTrabajadoresIds != null && nuevosTrabajadoresIds.length > 0) {
                String sqlInsert = "INSERT INTO cultivo_trabajador (cultivo_id, usuario_id) VALUES (?, ?)";
                try (PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {
                    for (String usuarioIdStr : nuevosTrabajadoresIds) {
                        int usuarioId = Integer.parseInt(usuarioIdStr);
                        psInsert.setInt(1, cultivoId);
                        psInsert.setInt(2, usuarioId);
                        psInsert.addBatch(); // Usamos batch para que sea más rápido
                    }
                    psInsert.executeBatch();
                }
            }

            conn.commit(); // SI TODO SALIÓ BIEN, GUARDAMOS
            return true;

        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (Exception ex) { } // SI FALLA, NO BORRES NADA
            e.printStackTrace();
            return false;
        }
    }
    // MÉTODO LISTAR CORREGIDO: Es el que dibuja tus 15 tarjetas
    public List<Map<String, String>> listarCultivos() {
        List<Map<String, String>> lista = new ArrayList<>();
      
        String sql = "SELECT id, nombre, fecha_siembra, fecha_cosecha, ciclo, estado FROM cultivos";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, String> c = new HashMap<>();
                c.put("id", String.valueOf(rs.getInt("id")));
                c.put("nombre", rs.getString("nombre"));
                c.put("fecha_siembra", rs.getString("fecha_siembra"));
                
                // Manejo de la fecha de cosecha (como aún no se determina, evitamos el null)
                String fCosecha = rs.getString("fecha_cosecha");
                c.put("fecha_cosecha", (fCosecha != null) ? fCosecha : "Pendiente");
                
                c.put("ciclo", rs.getString("ciclo"));
                
                // CORRECCIÓN 5: Ponemos 'Activo' manual para que la tarjeta no se rompa
                c.put("estado", "Activo"); 
                
                lista.add(c);
            }
        } catch (Exception e) { 
            System.err.println("Error en listarCultivos: " + e.getMessage());
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