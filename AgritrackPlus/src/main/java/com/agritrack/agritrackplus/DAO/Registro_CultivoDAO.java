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
import java.sql.Connection;
import java.sql.SQLException;

public class Registro_CultivoDAO {

    public boolean editar(String id, String nombre, String fechaSiembra, String fechaCosecha, String ciclo, String estado, int supervisor_id) {
        try (Connection conn = Conexion.getConexion()) {
            String sql = "UPDATE cultivos SET nombre=?, fecha_siembra=?, fecha_cosecha=?, ciclo=?, estado=? ,supervisor_id=? WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setString(2, fechaSiembra);
            ps.setString(3, (fechaCosecha != null && !fechaCosecha.isEmpty()) ? fechaCosecha : null);
            ps.setString(4, ciclo);
            ps.setString(5, estado);
            
            if (supervisor_id == 0)
            {
                ps.setNull(6, java.sql.Types.INTEGER);
            }else {
                ps.setInt (6, supervisor_id);
            }
            ps.setInt(7, Integer.parseInt(id));
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace(); 
            return false; }
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

    public String registrarCultivoCompleto(String nombre, String fechaSiembra, String ciclo,
        int supervisorId, String[] productoIds, String[] cantidades, String[] trabajadoresIds) {

        Connection conn = null;
        List<String> productosAgotados = new ArrayList<>();

        try {
            conn = Conexion.getConexion();
            conn.setAutoCommit(false); // Iniciar transacción

            // 1. Insertar el cultivo principal
            String sql = "INSERT INTO cultivos (nombre, fecha_siembra, ciclo, supervisor_id, estado) VALUES (?,?,?,?,'Activo')";
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

            // 2. Obtener el ID generado
            ResultSet rs = ps.getGeneratedKeys();
            int cultivoId = 0;
            if (rs.next()) {
                cultivoId = rs.getInt(1);
            }

            // 3. Asignar Productos, descontar stock y detectar agotados
            if (productoIds != null && cultivoId > 0) {
                for (int i = 0; i < productoIds.length; i++) {
                    if (productoIds[i] == null || productoIds[i].isEmpty()) continue;

                    int pId = Integer.parseInt(productoIds[i]);
                    int cant = (cantidades != null && i < cantidades.length && !cantidades[i].isEmpty())
                               ? Integer.parseInt(cantidades[i]) : 1;

                    // 3a. Verificar stock disponible
                    String sqlStock = "SELECT nombre, cantidad FROM productos WHERE id = ?";
                    try (PreparedStatement psStock = conn.prepareStatement(sqlStock)) {
                        psStock.setInt(1, pId);
                        ResultSet rsStock = psStock.executeQuery();
                        if (rsStock.next()) {
                            int disponible = rsStock.getInt("cantidad");
                            String nombreProducto = rsStock.getString("nombre");

                            if (disponible < cant) {
                                // Stock insuficiente: revertir todo
                                conn.rollback();
                                return "insuficiente:" + nombreProducto + " (disponible: " + disponible + ")";
                            }
                        }
                    }

                    // 3b. Insertar en stock_cultivo
                    String sqlProd = "INSERT INTO stock_cultivo(cultivo_id, producto_id, cantidad) VALUES(?,?,?)";
                    try (PreparedStatement psProd = conn.prepareStatement(sqlProd)) {
                        psProd.setInt(1, cultivoId);
                        psProd.setInt(2, pId);
                        psProd.setInt(3, cant);
                        psProd.executeUpdate();
                    }

                    // 3c. Descontar del inventario
                    String sqlDescontar = "UPDATE productos SET cantidad = cantidad - ? WHERE id = ?";
                    try (PreparedStatement psDesc = conn.prepareStatement(sqlDescontar)) {
                        psDesc.setInt(1, cant);
                        psDesc.setInt(2, pId);
                        psDesc.executeUpdate();
                    }

                    // 3d. Verificar si quedó en 0
                    String sqlVerificar = "SELECT nombre, cantidad FROM productos WHERE id = ?";
                    try (PreparedStatement psVer = conn.prepareStatement(sqlVerificar)) {
                        psVer.setInt(1, pId);
                        ResultSet rsVer = psVer.executeQuery();
                        if (rsVer.next() && rsVer.getInt("cantidad") <= 0) {
                            productosAgotados.add(rsVer.getString("nombre"));
                        }
                    }
                }
            }

            // 4. Asignar Trabajadores
            if (trabajadoresIds != null && cultivoId > 0) {
                for (String tId : trabajadoresIds) {
                    if (tId != null && !tId.isEmpty()) {
                        String sqlTrab = "INSERT INTO cultivo_trabajador(cultivo_id, usuario_id) VALUES(?,?)";
                        try (PreparedStatement psTrab = conn.prepareStatement(sqlTrab)) {
                            psTrab.setInt(1, cultivoId);
                            psTrab.setInt(2, Integer.parseInt(tId));
                            psTrab.executeUpdate();
                        }
                    }
                }
            }

            conn.commit(); // Confirmar todo

            // 5. Retornar resultado
            if (!productosAgotados.isEmpty()) {
                return "agotado:" + String.join(",", productosAgotados);
            }
            return "ok";

        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return "error";
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) {}
            }
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
        String sql = "SELECT p.id AS producto_id, p.nombre, sc.cantidad, p.unidad_medida, t.tipo_nombre " +
                     "FROM productos p " +
                     "JOIN stock_cultivo sc ON p.id = sc.producto_id " +
                     "JOIN tipo_producto t ON p.tipo_producto_id = t.id " +
                     "WHERE sc.cultivo_id = ?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, String> m = new HashMap<>();
                m.put("id", rs.getString("producto_id")); // ← alias
                m.put("nombre", rs.getString("nombre"));
                m.put("cantidad", rs.getString("cantidad"));
                m.put("unidad_medida", rs.getString("unidad_medida"));
                m.put("tipo_nombre", rs.getString("tipo_nombre"));
                lista.add(m);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }
    public Map<String, String> obtenerSupervisorCultivo(int id) {
        Map<String, String> supervisor = new HashMap<>();
        // CORREGIDO: Consulta a la tabla 'supervisor' vinculada con 'usuarios'
        String sql = "SELECT u.nombre FROM usuarios u " +
                     "INNER JOIN cultivos c ON u.id = c.supervisor_id WHERE c.id = ?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                supervisor.put("nombre", rs.getString("nombre"));
            }
        } catch (Exception e) {
            e.printStackTrace(); 
        }
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
    public boolean cambiarEstado(int id, String nuevoEstado) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Conexion.getConnection();
            String sql = "UPDATE cultivos SET estado = ? WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, nuevoEstado);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            // Usa tu método de cerrar conexiones aquí
            try { if(ps != null) ps.close(); if(conn != null) conn.close(); } catch(Exception ex){}
        }
    }
    public boolean eliminarCultivo(int cultivoId) {
        Connection conn = null;
        try {
            conn = Conexion.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            // Orden de eliminación para respetar llaves foráneas según tu SQL
            String[] sqls = {
                "DELETE FROM supervisor WHERE cultivo_id = ?",
                "DELETE FROM stock_cultivo WHERE cultivo_id = ?",
                "DELETE FROM cultivo_trabajador WHERE cultivo_id = ?",
                "DELETE FROM usuario_tarea WHERE cultivo_id = ?",
                "DELETE FROM cultivos WHERE id = ?"
            };

            for (String sql : sqls) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, cultivoId);
                    ps.executeUpdate();
                }
            }

            conn.commit(); // Confirmar cambios
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            // Usa tu método cerrar o cierra manualmente aquí
            try { if(conn != null) conn.close(); } catch(Exception ex){}
        }
    }
    public Map<String, String> obtenerCultivoPorId(int id) {
        Map<String, String> cultivo = new HashMap<>();
        String sql = "SELECT * FROM cultivos WHERE id = ?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cultivo.put("id", String.valueOf(rs.getInt("id")));
                    cultivo.put("nombre", rs.getString("nombre"));
                    cultivo.put("fecha_siembra", rs.getString("fecha_siembra"));
                    cultivo.put("fecha_cosecha", rs.getString("fecha_cosecha"));
                    cultivo.put("ciclo", rs.getString("ciclo"));
                    cultivo.put("estado", rs.getString("estado"));
                    String supId = rs.getString("supervisor_id");
                    cultivo.put("supervisor_id", supId != null ? supId : "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cultivo;
    }
    public String editarCultivoCompleto(String id, String nombre, String fechaSiembra, 
        String fechaCosecha, String ciclo, String estado, int supervisorId,
        String[] productoIds, String[] cantidades, String[] trabajadoresIds) {

        Connection conn = null;
        List<String> productosAgotados = new ArrayList<>();
        int cultivoId = Integer.parseInt(id);

        try {
            conn = Conexion.getConexion();
            conn.setAutoCommit(false);

            // 1. Actualizar datos principales del cultivo
            String sqlUpdate = "UPDATE cultivos SET nombre=?, fecha_siembra=?, fecha_cosecha=?, ciclo=?, estado=?, supervisor_id=? WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                ps.setString(1, nombre);
                ps.setString(2, fechaSiembra);
                ps.setString(3, (fechaCosecha != null && !fechaCosecha.isEmpty()) ? fechaCosecha : null);
                ps.setString(4, ciclo);
                ps.setString(5, estado);
                if (supervisorId == 0) {
                    ps.setNull(6, java.sql.Types.INTEGER);
                } else {
                    ps.setInt(6, supervisorId);
                }
                ps.setInt(7, cultivoId);
                ps.executeUpdate();
            }

            // 2. Devolver al inventario las cantidades anteriores del cultivo
            String sqlRecuperar = "SELECT producto_id, cantidad FROM stock_cultivo WHERE cultivo_id = ?";
            try (PreparedStatement psRec = conn.prepareStatement(sqlRecuperar)) {
                psRec.setInt(1, cultivoId);
                ResultSet rsRec = psRec.executeQuery();
                while (rsRec.next()) {
                    int pId = rsRec.getInt("producto_id");
                    int cantAnterior = rsRec.getInt("cantidad");

                    String sqlDevolver = "UPDATE productos SET cantidad = cantidad + ? WHERE id = ?";
                    try (PreparedStatement psDev = conn.prepareStatement(sqlDevolver)) {
                        psDev.setInt(1, cantAnterior);
                        psDev.setInt(2, pId);
                        psDev.executeUpdate();
                    }
                }
            }

            // 3. Borrar productos y trabajadores anteriores del cultivo
            String sqlBorrarProd = "DELETE FROM stock_cultivo WHERE cultivo_id = ?";
            try (PreparedStatement psBP = conn.prepareStatement(sqlBorrarProd)) {
                psBP.setInt(1, cultivoId);
                psBP.executeUpdate();
            }

            String sqlBorrarTrab = "DELETE FROM cultivo_trabajador WHERE cultivo_id = ?";
            try (PreparedStatement psBT = conn.prepareStatement(sqlBorrarTrab)) {
                psBT.setInt(1, cultivoId);
                psBT.executeUpdate();
            }

            // 4. Reinsertar productos con nuevas cantidades y descontar stock
            if (productoIds != null) {
                for (int i = 0; i < productoIds.length; i++) {
                    if (productoIds[i] == null || productoIds[i].isEmpty()) continue;

                    int pId = Integer.parseInt(productoIds[i]);
                    int cant = (cantidades != null && i < cantidades.length && !cantidades[i].isEmpty())
                               ? Integer.parseInt(cantidades[i]) : 1;

                    // 4a. Verificar stock disponible
                    String sqlStock = "SELECT nombre, cantidad FROM productos WHERE id = ?";
                    try (PreparedStatement psStock = conn.prepareStatement(sqlStock)) {
                        psStock.setInt(1, pId);
                        ResultSet rsStock = psStock.executeQuery();
                        if (rsStock.next()) {
                            int disponible = rsStock.getInt("cantidad");
                            String nombreProducto = rsStock.getString("nombre");

                            if (disponible < cant) {
                                conn.rollback(); // Revertir TODO, incluyendo las devoluciones del paso 2
                                return "insuficiente:" + nombreProducto + " (disponible: " + disponible + ")";
                            }
                        }
                    }

                    // 4b. Insertar en stock_cultivo
                    String sqlProd = "INSERT INTO stock_cultivo(cultivo_id, producto_id, cantidad) VALUES(?,?,?)";
                    try (PreparedStatement psProd = conn.prepareStatement(sqlProd)) {
                        psProd.setInt(1, cultivoId);
                        psProd.setInt(2, pId);
                        psProd.setInt(3, cant);
                        psProd.executeUpdate();
                    }

                    // 4c. Descontar del inventario
                    String sqlDescontar = "UPDATE productos SET cantidad = cantidad - ? WHERE id = ?";
                    try (PreparedStatement psDesc = conn.prepareStatement(sqlDescontar)) {
                        psDesc.setInt(1, cant);
                        psDesc.setInt(2, pId);
                        psDesc.executeUpdate();
                    }

                    // 4d. Verificar si quedó en 0
                    String sqlVerificar = "SELECT nombre, cantidad FROM productos WHERE id = ?";
                    try (PreparedStatement psVer = conn.prepareStatement(sqlVerificar)) {
                        psVer.setInt(1, pId);
                        ResultSet rsVer = psVer.executeQuery();
                        if (rsVer.next() && rsVer.getInt("cantidad") <= 0) {
                            productosAgotados.add(rsVer.getString("nombre"));
                        }
                    }
                }
            }

            // 5. Reinsertar trabajadores
            if (trabajadoresIds != null) {
                for (String tId : trabajadoresIds) {
                    if (tId != null && !tId.isEmpty()) {
                        String sqlTrab = "INSERT INTO cultivo_trabajador(cultivo_id, usuario_id) VALUES(?,?)";
                        try (PreparedStatement psTrab = conn.prepareStatement(sqlTrab)) {
                            psTrab.setInt(1, cultivoId);
                            psTrab.setInt(2, Integer.parseInt(tId));
                            psTrab.executeUpdate();
                        }
                    }
                }
            }

            conn.commit();

            if (!productosAgotados.isEmpty()) {
                return "agotado:" + String.join(",", productosAgotados);
            }
            return "ok";

        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return "error";
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) {}
            }
        }
    }
}