package com.agritrack.agritrackplus.DAO;

import com.agritrack.agritrackplus.db.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Registro_CultivoDAO {

    public boolean editar(String id, String nombre, String fechaSiembra,
            String fechaCosecha, String ciclo, String estado) {

        try {

            Connection conn = Conexion.getConexion();

            String sql = "UPDATE cultivos SET nombre=?, fecha_siembra=?, fecha_cosecha=?, ciclo=?, estado=? WHERE id=?";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, nombre);
            ps.setString(2, fechaSiembra);
            ps.setString(3, fechaCosecha);
            ps.setString(4, ciclo);
            ps.setString(5, estado);
            ps.setInt(6, Integer.parseInt(id));

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void eliminarTrabajadoresCultivo(int idCultivo) {

        try {

            Connection conn = Conexion.getConexion();

            String sql = "DELETE FROM cultivo_trabajador WHERE cultivo_id=?";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, idCultivo);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void asignarTrabajador(int cultivoId, int trabajadorId) {

        try {

            Connection conn = Conexion.getConexion();

            String sql = "INSERT INTO cultivo_trabajador(cultivo_id, usuario_id) VALUES(?,?)";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, cultivoId);
            ps.setInt(2, trabajadorId);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void eliminarProductosCultivo(int idCultivo) {

        try {

            Connection conn = Conexion.getConexion();

            String sql = "DELETE FROM cultivo_producto WHERE cultivo_id=?";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, idCultivo);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void asignarProducto(int cultivoId, int productoId) {

        try {

            Connection conn = Conexion.getConexion();

            String sql = "INSERT INTO cultivo_producto(cultivo_id, producto_id) VALUES(?,?)";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, cultivoId);
            ps.setInt(2, productoId);

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
    public void asignarProducto(int cultivoId, int productoId, int cantidad) {
        try {
            Connection conn = Conexion.getConexion();
            String sql = "INSERT INTO cultivo_producto(cultivo_id, producto_id, cantidad) VALUES(?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, cultivoId);
            ps.setInt(2, productoId);
            ps.setInt(3, cantidad);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean registrarCultivoCompleto(String nombre,
        String fechaSiembra,
        String ciclo,
        int supervisorId,
        String[] productoIds,
        String[] cantidades,
        String[] trabajadoresIds) {

        try {

            Connection conn = Conexion.getConexion();

            String sql = "INSERT INTO cultivos (nombre, fecha_siembra, ciclo, supervisor_id) VALUES (?,?,?,?)";

            PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            ps.setString(1, nombre);
            ps.setString(2, fechaSiembra);
            ps.setString(3, ciclo);

            if (supervisorId == 0) {
                ps.setNull(4, java.sql.Types.INTEGER);
            } else {
                ps.setInt(4, supervisorId);
            }

            ps.executeUpdate();

            java.sql.ResultSet rs = ps.getGeneratedKeys();

            int cultivoId = 0;

            if (rs.next()) {
                cultivoId = rs.getInt(1);
            }

            // Guardar productos
            if (productoIds != null) {

                for (int i = 0; i < productoIds.length; i++) {

                    if (productoIds[i] != null && !productoIds[i].isEmpty()) {

                        int productoId = Integer.parseInt(productoIds[i]);

                        int cant = (cantidades != null && i < cantidades.length)
                                ? Integer.parseInt(cantidades[i])
                                : 1;

                        asignarProducto(cultivoId, productoId, cant);
                    }
                }
            }

            // Guardar trabajadores
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

        try {

            Connection conn = Conexion.getConexion();

            String sql = "SELECT * FROM cultivos";

            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Map<String, String> cultivo = new HashMap<>();

                cultivo.put("id", rs.getString("id"));
                cultivo.put("nombre", rs.getString("nombre"));
                cultivo.put("fecha_siembra", rs.getString("fecha_siembra"));
                cultivo.put("fecha_cosecha", rs.getString("fecha_cosecha"));
                cultivo.put("estado", rs.getString("estado"));

                lista.add(cultivo);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;

    }
}