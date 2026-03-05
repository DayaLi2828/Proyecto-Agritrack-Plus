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

    public List<Map<String,String>> listarProductos(){

        List<Map<String,String>> lista = new ArrayList<>();

        try{

            Connection conn = Conexion.getConexion();

            String sql = "SELECT * FROM productos";

            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){

                Map<String,String> producto = new HashMap<>();

                producto.put("id", rs.getString("id"));
                producto.put("nombre", rs.getString("nombre"));
                producto.put("tipo", rs.getString("tipo"));
                producto.put("cantidad", rs.getString("cantidad"));

                lista.add(producto);

            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return lista;

    }
    public boolean agregar(String nombre, String unidadMedida, double precio,
        String fechaCompra, String fechaVencimiento,
        int tipoProductoId, int cantidad) {

        try {

            Connection conn = Conexion.getConexion();

            String sql = "INSERT INTO productos (nombre, unidad_medida, precio, fecha_compra, fecha_vencimiento, tipo_producto_id, cantidad) VALUES (?,?,?,?,?,?,?)";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, nombre);
            ps.setString(2, unidadMedida);
            ps.setDouble(3, precio);
            ps.setString(4, fechaCompra);

            if (fechaVencimiento == null) {
                ps.setNull(5, java.sql.Types.DATE);
            } else {
                ps.setString(5, fechaVencimiento);
            }

            ps.setInt(6, tipoProductoId);
            ps.setInt(7, cantidad);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}