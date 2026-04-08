package com.agritrack.agritrackplus.DAO;
// Define la ubicación del archivo dentro del paquete de Objetos de Acceso a Datos (DAO).

import com.agritrack.agritrackplus.db.Conexion;
// Importa tu clase personalizada para gestionar la conexión con MySQL.

import java.sql.*;
// Importa las clases necesarias de JDBC para manejar SQL (Connection, PreparedStatement, etc.).

import java.util.*;
// Importa las utilidades de Java como List, Map, ArrayList y HashMap.
/*
Gestiona la lógica de acceso a datos para productos en una base de datos. Incluye métodos para agregar, listar, editar, eliminar y buscar productos, así como para manejar 
el stock de productos (descontar cantidad y cambiar estado). Utiliza JDBC para realizar operaciones SQL, asegurando que las interacciones con la base de datos sean seguras y 
eficientes. Además, gestiona errores y excepciones adecuadamente, proporcionando mensajes de estado sobre las operaciones realizadas.
*/
public class ProductoDAO {
// Inicia la clase ProductoDAO, encargada de toda la lógica de inventario (CRUD de productos).

    public boolean agregar(String nombre, String unidadMedida, double precio, String fechaCompra, String fechaVencimiento, int tipoProductoId, int cantidad) {
    // Método para registrar un nuevo producto en el sistema.

        String sql = "INSERT INTO productos(nombre, unidad_medida, precio, fecha_compra, fecha_vencimiento, tipo_producto_id, stock) VALUES(?,?,?,?,?,?,?)";//marcadores de posición de inserción
        // Sentencia SQL para insertar los datos. 

        try (Connection conn = com.agritrack.agritrackplus.db.Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
        // Abre conexión y prepara la sentencia usando un try-with-resources (se cierra solo al terminar).

            ps.setString(1, nombre);
            // Asigna el nombre del producto (ej: Fertilizante X).

            ps.setString(2, unidadMedida);
            // Asigna la unidad (ej: Litros, Kilos).

            ps.setDouble(3, precio);
            // Asigna el costo unitario del producto.

            ps.setString(4, fechaCompra);
            // Asigna la fecha en que se adquirió el producto.

            ps.setString(5, fechaVencimiento); 
            // Asigna la fecha de expiración (puede ser null si el producto no vence).

            ps.setInt(6, tipoProductoId);
            // Asigna el ID de la categoría (Fertilizante, Semilla, etc.).

            ps.setInt(7, cantidad);
            // Asigna la cantidad inicial que entra a bodega.

            return ps.executeUpdate() > 0;
            // Ejecuta la inserción y retorna 'true' si se afectó al menos una fila.

        } catch (Exception e) {
            e.printStackTrace();
            return false;
            // Si hay un error (ej: datos inválidos), lo imprime y retorna 'false'.
        }
    }

    public List<Map<String, String>> listarProductos() {
    // Método para obtener la lista de todos los productos y mostrarlos en la tabla del frontend.

        List<Map<String, String>> lista = new ArrayList<>();
        // Crea la lista que almacenará cada producto como un mapa.

        String sql = "SELECT p.*, t.tipo_nombre FROM productos p " +
                     "JOIN tipo_producto t ON p.tipo_producto_id = t.id";
        // SQL con JOIN para que, en lugar de mostrar un ID de tipo, muestre el nombre real (ej: 'Herbicida').

        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
        // Ejecuta la consulta y obtiene el ResultSet (los datos de la DB).

            while (rs.next()) {
            // Recorre cada fila devuelta por MySQL:

                Map<String, String> p = new HashMap<>();
                // Crea un mapa para representar el producto actual.

                p.put("id", rs.getString("id"));
                // Guarda el ID único del producto.

                p.put("nombre", rs.getString("nombre"));
                // Guarda el nombre comercial.

                p.put("unidad_medida", rs.getString("unidad_medida"));
                // Guarda la unidad de empaque.

                p.put("precio", rs.getString("precio"));
                // Guarda el precio como texto para el mapa.

                p.put("cantidad", rs.getString("cantidad"));
                // Recupera el stock actual de la columna 'cantidad'.

                p.put("fecha_compra", rs.getString("fecha_compra"));
                // Recupera la fecha de adquisición.

                p.put("fecha_vencimiento", rs.getString("fecha_vencimiento"));
                // Recupera la fecha de caducidad.

                p.put("tipo_nombre", rs.getString("tipo_nombre"));
                // Guarda el nombre de la categoría (gracias al JOIN).

                p.put("estado", rs.getString("estado")); 
                // Lee si el producto está 'Activo' o 'Inactivo'.

                lista.add(p);
                // Añade este producto a la lista general.
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
        // Devuelve todos los productos encontrados.
    }

    public boolean editarProducto(int id, String nombre, String unidad, double precio, int cantidad, int tipoId) {
    // Método para modificar los datos de un producto ya existente.

        String sql = "UPDATE productos SET nombre = ?, unidad_medida = ?, precio = ?, cantidad = ?, tipo_producto_id = ? WHERE id = ?";
        // Sentencia SQL de actualización filtrada por el ID.

        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
        // Prepara la conexión y la sentencia.

            ps.setString(1, nombre);
            // Actualiza el nombre.

            ps.setString(2, unidad);
            // Actualiza la unidad de medida.

            ps.setDouble(3, precio);
            // Actualiza el precio.

            ps.setInt(4, cantidad);
            // Actualiza el stock manualmente si es necesario.

            ps.setInt(5, tipoId);
            // Cambia la categoría si hubo un error al registrarlo.

            ps.setInt(6, id);
            // El ID que identifica qué producto estamos editando.

            return ps.executeUpdate() > 0;
            // Retorna 'true' si la actualización fue exitosa.

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertarProducto(String nombre, String tipoId, String unidad, double precio, int cantidad, String fechaCompra, String fechaVencimiento) {
    // Versión alternativa de inserción que fuerza el estado 'Activo'.

        String sql = "INSERT INTO productos (nombre, tipo_producto_id, unidad_medida, precio, cantidad, fecha_compra, fecha_vencimiento, estado) VALUES (?, ?, ?, ?, ?, ?, ?, 'Activo')";
        // SQL que asegura que todo producto nuevo nazca con estado 'Activo'.

        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setInt(2, Integer.parseInt(tipoId));
            // Convierte el ID de la categoría de String a entero.

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

    public boolean eliminarProducto(int id) {
    // Método para borrar permanentemente un producto de la base de datos.

        String sql = "DELETE FROM productos WHERE id = ?";
        // Sentencia de eliminación física.

        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            // Indica cuál es el ID del producto a eliminar.

            return ps.executeUpdate() > 0;
            // Retorna verdadero si el producto fue borrado.

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean cambiarEstado(int id, String nuevoEstado) {
    // Método para realizar una "eliminación lógica" (Activo/Inactivo) sin borrar los datos.

        String sql = "UPDATE productos SET estado = ? WHERE id = ?";
        // Actualiza solo la columna de estado.

        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            // El nuevo valor (ej: 'Inactivo').

            ps.setInt(2, id);
            // El ID del producto afectado.

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, String> buscarProductoPorId(int id) {
    // Busca un solo producto. Se usa mucho para cargar los datos en el modal de edición.

        Map<String, String> p = new HashMap<>();
        // Mapa donde guardaremos los datos del producto encontrado.

        String sql = "SELECT * FROM productos WHERE id = ?";
        // Consulta directa por ID único.

        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            // Busca este ID específico.

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                // Si lo encuentra, llena el mapa con todos los campos:

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
        // Devuelve el mapa (estará vacío si no encontró nada).
    }

    public String descontarStock(int productoId, double cantidadUsada) {
    // Método de alta prioridad: resta insumos cuando se usan en un cultivo.

        Connection con = null;
        String mensaje = "ok";
        // Por defecto, asumimos que todo saldrá bien.

        String sqlSelect = "SELECT nombre, cantidad FROM productos WHERE id = ?";
        // SQL para verificar existencias antes de gastar.

        String sqlUpdate = "UPDATE productos SET cantidad = cantidad - ? WHERE id = ?";
        // SQL para aplicar la resta matemática en la base de datos.

        try {
            con = com.agritrack.agritrackplus.db.Conexion.getConexion();

            // 1. Verificar stock actual
            try (PreparedStatement psSelect = con.prepareStatement(sqlSelect)) {
                psSelect.setInt(1, productoId);
                ResultSet rs = psSelect.executeQuery();
                if (rs.next()) {
                    double actual = rs.getDouble("cantidad");
                    // Cuánto hay en bodega justo ahora.

                    String nombreP = rs.getString("nombre");

                    if (actual < cantidadUsada) {
                    // Si el usuario quiere gastar 10 y solo hay 5:

                        return "Stock insuficiente de " + nombreP + " (Disponible: " + actual + ")";
                        // Detiene el proceso y avisa del error.
                    }
                }
            }

            // 2. Ejecutar la resta
            try (PreparedStatement psUpdate = con.prepareStatement(sqlUpdate)) {
                psUpdate.setDouble(1, cantidadUsada);
                // El valor a restar.

                psUpdate.setInt(2, productoId);
                // El producto afectado.

                psUpdate.executeUpdate();
                // Aplica el descuento en la tabla 'productos'.
            }

            // 3. Verificar si quedó en 0 para la alerta
            try (PreparedStatement psCheck = con.prepareStatement(sqlSelect)) {
                psCheck.setInt(1, productoId);
                ResultSet rs2 = psCheck.executeQuery();
                if (rs2.next() && rs2.getDouble("cantidad") <= 0) {
                // Si después de la resta el stock llegó a cero:

                    mensaje = "agotado"; 
                    // Cambia el mensaje para que el sistema mande una alerta visual de "Insumo Agotado".
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "error";
        } finally {
            if (con != null) try { con.close(); } catch (SQLException e) {}
            // Cerramos la conexión manualmente para evitar fugas de memoria.
        }
        return mensaje;
        // Retorna "ok", "agotado" o el mensaje de stock insuficiente.
    }
}