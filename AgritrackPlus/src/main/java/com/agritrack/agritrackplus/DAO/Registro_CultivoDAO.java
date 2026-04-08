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
/*
maneja todo lo relacionado con los cultivos en la base de datos: permite crear, editar, consultar y eliminar cultivos, incluyendo la gestión de sus trabajadores,
productos/insumos y supervisor asignado. Lo más importante es que al registrar o editar un cultivo descuenta automáticamente del inventario los insumos usados, y si
algo falla revierte todos los cambios para no dejar datos inconsistentes.
*/
public class Registro_CultivoDAO {

   public boolean editar(String id, String nombre, String fechaSiembra, String fechaCosecha, String ciclo, String estado, int supervisor_id) {
    // Método que actualiza un cultivo existente en la base de datos.
    // Recibe como parámetros los datos que vienen del formulario JSP/Servlet:
    // id, nombre, fechas, ciclo, estado y supervisor_id.

    try (Connection conn = Conexion.getConexion()) {
        // Abre una conexión a la base de datos usando tu clase personalizada Conexion.
        // El bloque try-with-resources asegura que la conexión se cierre automáticamente al terminar.

        String sql = "UPDATE cultivos SET nombre=?, fecha_siembra=?, fecha_cosecha=?, ciclo=?, estado=? ,supervisor_id=? WHERE id=?";
        // Sentencia SQL que actualiza los campos de la tabla "cultivos".
        // Los signos de interrogación (?) son parámetros que se llenarán después.

        PreparedStatement ps = conn.prepareStatement(sql);
        // Prepara la sentencia SQL para poder insertar los valores de forma segura.

        ps.setString(1, nombre);
        // Sustituye el primer "?" con el valor del parámetro "nombre" recibido desde el formulario.

        ps.setString(2, fechaSiembra);
        // Sustituye el segundo "?" con la fecha de siembra.

        ps.setString(3, (fechaCosecha != null && !fechaCosecha.isEmpty()) ? fechaCosecha : null);
        // Sustituye el tercer "?" con la fecha de cosecha.
        // Si el valor es nulo o vacío, se guarda como NULL en la base de datos.

        ps.setString(4, ciclo);
        // Sustituye el cuarto "?" con el ciclo del cultivo.

        ps.setString(5, estado);
        // Sustituye el quinto "?" con el estado del cultivo (ej. Activo, Inactivo).

        if (supervisor_id == 0) {
            ps.setNull(6, java.sql.Types.INTEGER);
            // Si supervisor_id es 0, significa que no hay supervisor asignado.
            // Se guarda NULL en la columna supervisor_id.
        } else {
            ps.setInt(6, supervisor_id);
            // Si supervisor_id tiene un valor válido, se guarda ese número en la columna supervisor_id.
        }

        ps.setInt(7, Integer.parseInt(id));
        // Sustituye el último "?" con el ID del cultivo, convertido a entero.
        // Esto indica qué registro específico se debe actualizar.

        return ps.executeUpdate() > 0;
        // Ejecuta la sentencia SQL.
        // Devuelve true si al menos una fila fue afectada (es decir, si el cultivo se actualizó correctamente).

    } catch (Exception e) {
        e.printStackTrace();
        // Si ocurre un error (ej. conexión fallida, SQL incorrecto), se imprime el error en consola.

        return false;
        // Devuelve false para indicar que la actualización no se realizó.
    }
}

    
  public void eliminarTrabajadoresCultivo(int idCultivo) {
    // Método que elimina todos los trabajadores asociados a un cultivo específico.
    // Recibe como parámetro el ID del cultivo que viene desde el Servlet o controlador.

    try (Connection conn = Conexion.getConexion()) {
        // Abre una conexión a la base de datos usando tu clase personalizada Conexion.
        // El try-with-resources asegura que la conexión se cierre automáticamente al terminar.

        String sql = "DELETE FROM cultivo_trabajador WHERE cultivo_id=?";
        // Sentencia SQL que elimina todos los registros de la tabla "cultivo_trabajador"
        // donde el campo cultivo_id coincida con el ID recibido.

        PreparedStatement ps = conn.prepareStatement(sql);
        // Prepara la sentencia SQL para poder insertar parámetros de forma segura.

        ps.setInt(1, idCultivo);
        // Sustituye el primer "?" de la sentencia SQL con el valor del parámetro idCultivo.
        // Este valor viene del controlador, que a su vez lo recibió del formulario o lógica de negocio.

        ps.executeUpdate();
        // Ejecuta la sentencia preparada. En este caso, borra todos los registros que coincidan.
        // No devuelve resultados, solo el número de filas afectadas.

    } catch (Exception e) { 
        e.printStackTrace(); 
        // Si ocurre algún error (ej. conexión fallida, SQL mal escrito),
        // se imprime el stack trace en la consola para depuración.
    }
}


  public void asignarTrabajador(int cultivoId, int trabajadorId) {
    // Este método sirve para asignar un trabajador a un cultivo.
    // Recibe dos datos:
    // - cultivoId: el identificador del cultivo.
    // - trabajadorId: el identificador del trabajador.

    try (Connection conn = Conexion.getConexion()) {
        // Abre una conexión con la base de datos usando tu clase Conexion.
        // El "try(...)" asegura que la conexión se cierre sola al terminar.

        String sql = "INSERT INTO cultivo_trabajador(cultivo_id, usuario_id) VALUES(?,?)";
        // Aquí se escribe la consulta SQL que va a insertar un nuevo registro
        // en la tabla "cultivo_trabajador". Esa tabla guarda la relación
        // entre cultivos y trabajadores.
        // Los signos ? son espacios que luego se llenan con valores reales.

        PreparedStatement ps = conn.prepareStatement(sql);
        // Se prepara la consulta para poder reemplazar los ? con datos concretos.

        ps.setInt(1, cultivoId);
        // El primer ? se reemplaza con el ID del cultivo recibido como parámetro.

        ps.setInt(2, trabajadorId);
        // El segundo ? se reemplaza con el ID del trabajador recibido como parámetro.

        ps.executeUpdate();
        // Se ejecuta la consulta preparada.
        // Esto inserta una nueva fila en la tabla "cultivo_trabajador"
        // con los valores de cultivoId y trabajadorId.

    } catch (Exception e) { 
        e.printStackTrace(); 
        // Si ocurre un error (por ejemplo, problema de conexión o SQL mal escrito),
        // se imprime el error en la consola para poder revisarlo.
    }
}


    public void eliminarProductosCultivo(int idCultivo) {
    // Este método elimina todos los productos asociados a un cultivo específico.
    // Recibe como parámetro el ID del cultivo (idCultivo).

    try (Connection conn = Conexion.getConexion()) {
        // Abre una conexión con la base de datos usando tu clase Conexion.
        // El "try(...)" asegura que la conexión se cierre automáticamente al terminar.

        String sql = "DELETE FROM stock_cultivo WHERE cultivo_id=?";
        // Se define la consulta SQL que va a borrar registros de la tabla "stock_cultivo".
        // Esa tabla guarda qué productos están vinculados a cada cultivo.
        // El signo ? es un espacio que luego se reemplaza con un valor real.

        PreparedStatement ps = conn.prepareStatement(sql);
        // Se prepara la consulta para poder asignar valores al ? de forma segura.

        ps.setInt(1, idCultivo);
        // Reemplaza el primer ? con el ID del cultivo recibido como parámetro.
        // Así la consulta sabe qué productos borrar (los que pertenecen a ese cultivo).

        ps.executeUpdate();
        // Ejecuta la consulta preparada.
        // Esto elimina todas las filas de la tabla "stock_cultivo" que tengan ese cultivo_id.

    } catch (Exception e) { 
        e.printStackTrace(); 
        // Si ocurre un error (por ejemplo, problema de conexión o SQL mal escrito),
        // se imprime el error en la consola para poder revisarlo.
    }
}


   public void asignarProducto(int cultivoId, int productoId) {
    // Este método sirve para asignar un producto a un cultivo.
    // Recibe dos datos:
    // - cultivoId: el identificador del cultivo.
    // - productoId: el identificador del producto.

    String sql = "INSERT INTO stock_cultivo(cultivo_id, producto_id, cantidad) VALUES(?,?,?)";
    // Se define la consulta SQL que va a insertar un nuevo registro
    // en la tabla "stock_cultivo". Esa tabla guarda qué productos
    // están asociados a cada cultivo y en qué cantidad.
    // Los signos ? son espacios que luego se llenan con valores reales.

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        // Se abre una conexión con la base de datos usando tu clase Conexion.
        // También se prepara la consulta SQL para poder reemplazar los ? con datos concretos.
        // El "try(...)" asegura que tanto la conexión como el PreparedStatement
        // se cierren automáticamente al terminar.

        ps.setInt(1, cultivoId);
        // El primer ? se reemplaza con el ID del cultivo recibido como parámetro.

        ps.setInt(2, productoId);
        // El segundo ? se reemplaza con el ID del producto recibido como parámetro.

        ps.setInt(3, 1); 
        // El tercer ? se reemplaza con la cantidad inicial.
        // Aquí se pone 1 para que siempre aparezca al menos una unidad del producto.

        ps.executeUpdate();
        // Se ejecuta la consulta preparada.
        // Esto inserta una nueva fila en la tabla "stock_cultivo"
        // con los valores de cultivoId, productoId y cantidad=1.

    } catch (Exception e) {
        e.printStackTrace();
        // Si ocurre un error (por ejemplo, problema de conexión o SQL mal escrito),
        // se imprime el error en la consola para poder revisarlo.
    }
}


   public void eliminarSupervisorCultivo(int cultivoId) {
    // Este método elimina al supervisor asignado a un cultivo.
    // Recibe como parámetro el ID del cultivo.

    try {
        Connection conn = Conexion.getConexion();
        // Abre una conexión con la base de datos usando tu clase Conexion.

        String sql = "UPDATE cultivos SET supervisor_id=NULL WHERE id=?";
        // Se define la consulta SQL que actualiza la tabla "cultivos".
        // Cambia el campo supervisor_id a NULL para el cultivo con el ID indicado.
        // Esto significa que el cultivo queda sin supervisor asignado.

        PreparedStatement ps = conn.prepareStatement(sql);
        // Se prepara la consulta para poder reemplazar el ? con un valor real.

        ps.setInt(1, cultivoId);
        // Reemplaza el ? con el ID del cultivo recibido como parámetro.

        ps.executeUpdate();
        // Ejecuta la consulta preparada.
        // Esto actualiza la fila del cultivo y elimina la relación con el supervisor.

    } catch (Exception e) {
        e.printStackTrace();
        // Si ocurre un error (ejemplo: problema de conexión o SQL mal escrito),
        // se imprime el error en la consola para depuración.
    }
}

public void asignarSupervisor(int cultivoId, int supervisorId) {
    // Este método asigna un supervisor a un cultivo.
    // Recibe dos parámetros:
    // - cultivoId: el identificador del cultivo.
    // - supervisorId: el identificador del supervisor.

    try {
        Connection conn = Conexion.getConexion();
        // Abre una conexión con la base de datos usando tu clase Conexion.

        String sql = "UPDATE cultivos SET supervisor_id=? WHERE id=?";
        // Se define la consulta SQL que actualiza la tabla "cultivos".
        // Cambia el campo supervisor_id al valor recibido, para el cultivo con el ID indicado.

        PreparedStatement ps = conn.prepareStatement(sql);
        // Se prepara la consulta para poder reemplazar los ? con valores reales.

        ps.setInt(1, supervisorId);
        // El primer ? se reemplaza con el ID del supervisor recibido como parámetro.

        ps.setInt(2, cultivoId);
        // El segundo ? se reemplaza con el ID del cultivo recibido como parámetro.

        ps.executeUpdate();
        // Ejecuta la consulta preparada.
        // Esto actualiza la fila del cultivo y asigna el supervisor indicado.

    } catch (Exception e) {
        e.printStackTrace();
        // Si ocurre un error (ejemplo: problema de conexión o SQL mal escrito),
        // se imprime el error en la consola para depuración.
    }
}


    public String registrarCultivoCompleto(String nombre, String fechaSiembra, String ciclo,
        int supervisorId, String[] productoIds, String[] cantidades, String[] trabajadoresIds) {
    // Este método registra un cultivo completo en la base de datos.
    // Recibe:
    // - nombre, fechaSiembra, ciclo: datos principales del cultivo.
    // - supervisorId: el ID del supervisor asignado (puede ser 0 si no hay).
    // - productoIds: lista de productos que se asignan al cultivo.
    // - cantidades: lista de cantidades de cada producto.
    // - trabajadoresIds: lista de trabajadores que se asignan al cultivo.
    // Devuelve un String con el resultado: "ok", "agotado:..." o "insuficiente:...".

    Connection conn = null;
    List<String> productosAgotados = new ArrayList<>();
    // Se declara la conexión y una lista para guardar los nombres de productos que queden agotados.

    try {
        conn = Conexion.getConexion();
        conn.setAutoCommit(false); // Iniciar transacción
        // Se abre la conexión y se desactiva el auto-commit.
        // Esto significa que los cambios se aplican todos juntos al final (transacción).

        // 1. Insertar el cultivo principal
        String sql = "INSERT INTO cultivos (nombre, fecha_siembra, ciclo, supervisor_id, estado) VALUES (?,?,?,?,'Activo')";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        // Se define la consulta SQL para insertar un nuevo cultivo.
        // Se pide que devuelva la clave generada (el ID del cultivo).

        ps.setString(1, nombre);       // Primer ? → nombre del cultivo.
        ps.setString(2, fechaSiembra); // Segundo ? → fecha de siembra.
        ps.setString(3, ciclo);        // Tercer ? → ciclo del cultivo.
        if (supervisorId == 0) {
            ps.setNull(4, java.sql.Types.INTEGER); // Si no hay supervisor, guarda NULL.
        } else {
            ps.setInt(4, supervisorId); // Si hay supervisor, guarda su ID.
        }
        ps.executeUpdate(); // Ejecuta la inserción del cultivo.

        // 2. Obtener el ID generado
        ResultSet rs = ps.getGeneratedKeys();
        int cultivoId = 0;
        if (rs.next()) {
            cultivoId = rs.getInt(1); // Se obtiene el ID del cultivo recién insertado.
        }

        // 3. Asignar Productos, descontar stock y detectar agotados
        if (productoIds != null && cultivoId > 0) {
            for (int i = 0; i < productoIds.length; i++) {
                if (productoIds[i] == null || productoIds[i].isEmpty()) continue;
                // Si el producto está vacío, se salta.

                int pId = Integer.parseInt(productoIds[i]); // ID del producto.
                int cant = (cantidades != null && i < cantidades.length && !cantidades[i].isEmpty())
                           ? Integer.parseInt(cantidades[i]) : 1;
                // Cantidad del producto (si no se especifica, se usa 1).

                // 3a. Verificar stock disponible
                String sqlStock = "SELECT nombre, cantidad FROM productos WHERE id = ?";
                // Define la consulta SQL para obtener el nombre y la cantidad del producto con el ID especificado.
                try (PreparedStatement psStock = conn.prepareStatement(sqlStock)) { 
                // Prepara la consulta SQL usando un PreparedStatement, asegurando que se cerrará automáticamente al final.
                    psStock.setInt(1, pId); 
                    // Establece el valor del primer parámetro (ID del producto) en la consulta SQL.
                    ResultSet rsStock = psStock.executeQuery(); // Ejecuta la consulta y obtiene los resultados en un ResultSet.
                    if (rsStock.next()) { 
                    // Verifica si hay un registro en el ResultSet (es decir, si se encontró el producto).
                        int disponible = rsStock.getInt("cantidad"); 
                        // Obtiene la cantidad disponible del producto desde el ResultSet.
                        String nombreProducto = rsStock.getString("nombre");
                        // Obtiene el nombre del producto desde el ResultSet.

                        if (disponible < cant) { // Verifica si la cantidad disponible es menor que la cantidad solicitada.
                            // Si no hay suficiente stock, se revierte todo.
                            conn.rollback(); // Revierte cualquier cambio realizado en la base de datos durante la transacción actual.
                            return "insuficiente:" + nombreProducto + " (disponible: " + disponible + ")"; 
                            // Devuelve un mensaje indicando que no hay suficiente stock, junto con el nombre del producto y la cantidad disponible.
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
                        // Si el producto quedó en 0, se agrega a la lista de agotados.
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
        // Si todo salió bien, se confirman los cambios en la base de datos.

        // 5. Retornar resultado
        if (!productosAgotados.isEmpty()) {
            return "agotado:" + String.join(",", productosAgotados);
            // Si hubo productos que quedaron en 0, se devuelve "agotado" con sus nombres.
        }
        return "ok"; // Si todo salió bien, se devuelve "ok".

    } catch (Exception e) {
        if (conn != null) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            // Si ocurre un error, se revierte la transacción para no dejar datos incompletos.
        }
        e.printStackTrace();
        return "error"; // Devuelve "error" si algo falló.
    } finally {
        if (conn != null) {
            try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) {}
            // Al final, se vuelve a activar el auto-commit y se cierra la conexión.
        }
    }
}



   public List<Map<String, String>> listarCultivos() {
    // Este método devuelve una lista de cultivos desde la base de datos.
    // Cada cultivo se guarda como un Map (clave-valor) con sus datos principales.

    List<Map<String, String>> lista = new ArrayList<>();
    // Se crea una lista vacía donde se irán guardando los cultivos encontrados.

    try (Connection conn = Conexion.getConexion()) {
        // Abre una conexión con la base de datos usando tu clase Conexion.
        // El "try(...)" asegura que la conexión se cierre automáticamente al terminar.

        String sql = "SELECT * FROM cultivos";
        // Se define la consulta SQL que selecciona todos los registros de la tabla "cultivos".

        ResultSet rs = conn.createStatement().executeQuery(sql);
        // Se crea un Statement y se ejecuta la consulta.
        // El resultado se guarda en un ResultSet (rs), que contiene todas las filas obtenidas.

        while (rs.next()) {
            // Se recorre cada fila del resultado (cada cultivo).

            Map<String, String> c = new HashMap<>();
            // Se crea un nuevo Map para guardar los datos de un cultivo.

            c.put("id", rs.getString("id"));
            // Se obtiene el valor de la columna "id" y se guarda en el Map con la clave "id".

            c.put("nombre", rs.getString("nombre"));
            // Se obtiene el nombre del cultivo y se guarda en el Map.

            c.put("fecha_siembra", rs.getString("fecha_siembra"));
            // Se obtiene la fecha de siembra y se guarda en el Map.

            c.put("fecha_cosecha", rs.getString("fecha_cosecha") != null ? rs.getString("fecha_cosecha") : "Pendiente");
            // Se obtiene la fecha de cosecha.
            // Si está vacía o nula, se guarda la palabra "Pendiente" en lugar de NULL.

            c.put("estado", rs.getString("estado"));
            // Se obtiene el estado del cultivo (ejemplo: Activo, Inactivo).

            lista.add(c);
            // Se agrega el Map del cultivo a la lista.
        }
    } catch (Exception e) { 
        e.printStackTrace(); 
        // Si ocurre un error (ejemplo: problema de conexión o SQL mal escrito),
        // se imprime el error en la consola para depuración.
    }

    return lista;
    // Finalmente, se devuelve la lista con todos los cultivos encontrados.
}


   public Map<String, String> obtenerPorId(String id) {
    // Este método busca un cultivo en la base de datos por su ID.
    // Devuelve un Map con los datos del cultivo encontrado.

    Map<String, String> cultivo = new HashMap<>();
    // Se crea un Map vacío donde se guardarán los datos del cultivo.

    String sql = "SELECT * FROM cultivos WHERE id = ?";
    // Se define la consulta SQL que selecciona un cultivo específico
    // usando su ID como filtro.

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        // Se abre una conexión con la base de datos.
        // Se prepara la consulta SQL para poder reemplazar el ? con un valor real.

        ps.setInt(1, Integer.parseInt(id));
        // El ? de la consulta se reemplaza con el ID recibido como parámetro.
        // El ID llega como String, por eso se convierte a entero.

        ResultSet rs = ps.executeQuery();
        // Se ejecuta la consulta preparada.
        // El resultado se guarda en un ResultSet (rs).

        if (rs.next()) {
            // Si la consulta devuelve una fila (es decir, el cultivo existe):

            cultivo.put("id", rs.getString("id"));
            // Se obtiene el valor de la columna "id" y se guarda en el Map.

            cultivo.put("nombre", rs.getString("nombre"));
            // Se obtiene el nombre del cultivo y se guarda en el Map.

            cultivo.put("fecha_siembra", rs.getString("fecha_siembra"));
            // Se obtiene la fecha de siembra y se guarda en el Map.

            cultivo.put("fecha_cosecha", rs.getString("fecha_cosecha"));
            // Se obtiene la fecha de cosecha y se guarda en el Map.

            cultivo.put("ciclo", rs.getString("ciclo"));
            // Se obtiene el ciclo del cultivo y se guarda en el Map.

            cultivo.put("estado", rs.getString("estado"));
            // Se obtiene el estado del cultivo (ejemplo: Activo, Inactivo).
        }
    } catch (Exception e) { 
        e.printStackTrace(); 
        // Si ocurre un error (ejemplo: problema de conexión o SQL mal escrito),
        // se imprime el error en la consola para depuración.
    }

    return cultivo;
    // Finalmente, se devuelve el Map con los datos del cultivo encontrado.
}

   
   public List<Map<String, String>> obtenerProductosCultivo(int id) {
    // Devuelve una lista de productos asociados a un cultivo específico.
    // Cada producto se guarda en un Map con sus datos.

    List<Map<String, String>> lista = new ArrayList<>();
    // Se crea una lista vacía para almacenar los productos.

    String sql = "SELECT p.id AS producto_id, p.nombre, sc.cantidad, p.unidad_medida, t.tipo_nombre " +
                 "FROM productos p " +
                 "JOIN stock_cultivo sc ON p.id = sc.producto_id " +
                 "JOIN tipo_producto t ON p.tipo_producto_id = t.id " +
                 "WHERE sc.cultivo_id = ?";
    // Consulta SQL que obtiene:
    // - ID del producto (con alias producto_id),
    // - nombre del producto,
    // - cantidad asignada al cultivo,
    // - unidad de medida,
    // - tipo de producto.
    // Se hace JOIN con stock_cultivo y tipo_producto para traer toda la información.

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        // Abre conexión y prepara la consulta.

        ps.setInt(1, id);
        // Reemplaza el ? con el ID del cultivo recibido.

        ResultSet rs = ps.executeQuery();
        // Ejecuta la consulta y guarda los resultados.

        while (rs.next()) {
            // Recorre cada fila (cada producto asociado al cultivo).

            Map<String, String> m = new HashMap<>();
            // Crea un Map para guardar los datos del producto.

            m.put("id", rs.getString("producto_id")); // Se guarda el ID del producto.
            m.put("nombre", rs.getString("nombre")); // Se guarda el nombre del producto.
            m.put("cantidad", rs.getString("cantidad")); // Se guarda la cantidad asignada.
            m.put("unidad_medida", rs.getString("unidad_medida")); // Se guarda la unidad de medida.
            m.put("tipo_nombre", rs.getString("tipo_nombre")); // Se guarda el tipo de producto.

            lista.add(m);
            // Se agrega el producto a la lista.
        }
    } catch (Exception e) { 
        e.printStackTrace(); 
        // Si ocurre un error, se imprime en consola.
    }

    return lista;
    // Devuelve la lista con todos los productos del cultivo.
}

    public Map<String, String> obtenerSupervisorCultivo(int id) {
    // Devuelve el supervisor asignado a un cultivo.
    // El resultado es un Map con el nombre del supervisor.

    Map<String, String> supervisor = new HashMap<>();
    // Se crea un Map vacío para guardar los datos.

    String sql = "SELECT u.nombre FROM usuarios u " +
                 "INNER JOIN cultivos c ON u.id = c.supervisor_id WHERE c.id = ?";
    // Consulta SQL que obtiene el nombre del supervisor
    // haciendo un INNER JOIN entre usuarios y cultivos.

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        // Abre conexión y prepara la consulta.

        ps.setInt(1, id);
        // Reemplaza el ? con el ID del cultivo.

        ResultSet rs = ps.executeQuery();
        // Ejecuta la consulta.

        if (rs.next()) {
            supervisor.put("nombre", rs.getString("nombre"));
            // Si hay resultado, guarda el nombre del supervisor en el Map.
        }
    } catch (Exception e) {
        e.printStackTrace(); 
        // Si ocurre un error, se imprime en consola.
    }

    return supervisor;
    // Devuelve el Map con el nombre del supervisor.
}


        public List<Map<String, String>> obtenerTrabajadoresCultivo(int id) {
    // Devuelve una lista de trabajadores asociados a un cultivo.
    // Cada trabajador se guarda en un Map con su ID, nombre y foto.

    List<Map<String, String>> lista = new ArrayList<>();
    // Se crea una lista vacía para almacenar los trabajadores.

    String sql = "SELECT u.id, u.nombre, f.ruta FROM usuarios u " +
                 "JOIN cultivo_trabajador ct ON u.id = ct.usuario_id " +
                 "LEFT JOIN fotos_usuario f ON u.id = f.usuario_id WHERE ct.cultivo_id = ?";
    // Consulta SQL que obtiene:
    // - ID y nombre del usuario,
    // - ruta de la foto (si existe).
    // Se hace JOIN con cultivo_trabajador y fotos_usuario.

    try (Connection conn = com.agritrack.agritrackplus.db.Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        // Abre conexión y prepara la consulta.

        ps.setInt(1, id);
        // Reemplaza el ? con el ID del cultivo.

        ResultSet rs = ps.executeQuery();
        // Ejecuta la consulta.

        while (rs.next()) {
            // Recorre cada fila (cada trabajador asociado al cultivo).

            Map<String, String> m = new HashMap<>();
            m.put("id", rs.getString("id")); // Se guarda el ID del trabajador.
            m.put("nombre", rs.getString("nombre")); // Se guarda el nombre.
            m.put("foto", rs.getString("ruta")); // Se guarda la ruta de la foto.
            lista.add(m);
            // Se agrega el trabajador a la lista.
        }
    } catch (Exception e) { 
        e.printStackTrace(); 
        // Si ocurre un error, se imprime en consola.
    }

    return lista;
    // Devuelve la lista con todos los trabajadores del cultivo.
}

    public boolean cambiarEstado(int id, String nuevoEstado) {
    // Cambia el estado de un cultivo (ejemplo: Activo, Inactivo).
    // Devuelve true si se actualizó correctamente.

    Connection conn = null;
    PreparedStatement ps = null;
    // Se declaran la conexión y el PreparedStatement.

    try {
        conn = Conexion.getConnection();
        // Abre conexión con la base de datos.

        String sql = "UPDATE cultivos SET estado = ? WHERE id = ?";
        // Consulta SQL que actualiza el estado de un cultivo.

        ps = conn.prepareStatement(sql);
        ps.setString(1, nuevoEstado); // Primer ? → nuevo estado.
        ps.setInt(2, id);             // Segundo ? → ID del cultivo.

        return ps.executeUpdate() > 0;
        // Ejecuta la consulta y devuelve true si se actualizó al menos una fila.

    } catch (Exception e) {
        e.printStackTrace();
        return false;
        // Si ocurre un error, devuelve false.
    } finally {
        // Cierra recursos abiertos.
        try { if(ps != null) ps.close(); if(conn != null) conn.close(); } catch(Exception ex){}
    }
}

    public boolean eliminarCultivo(int cultivoId) {
    // Método que elimina un cultivo y todas sus relaciones en la base de datos.
    // Recibe como parámetro el ID del cultivo y devuelve true si se eliminó correctamente.

    Connection conn = null;
    // Se declara la variable de conexión, inicialmente nula.

    try {
        conn = Conexion.getConnection();
        // Se abre una conexión con la base de datos usando tu clase Conexion.

        conn.setAutoCommit(false); // Iniciar transacción
        // Se desactiva el auto-commit para manejar todo como una transacción.
        // Esto asegura que todos los pasos se ejecuten juntos y si algo falla se pueda revertir.

        // Orden de eliminación para respetar llaves foráneas según tu SQL
        String[] sqls = {
            "DELETE FROM supervisor WHERE cultivo_id = ?",
            "DELETE FROM stock_cultivo WHERE cultivo_id = ?",
            "DELETE FROM cultivo_trabajador WHERE cultivo_id = ?",
            "DELETE FROM usuario_tarea WHERE cultivo_id = ?",
            "DELETE FROM cultivos WHERE id = ?"
        };
        // Se define un arreglo con todas las consultas SQL necesarias para eliminar
        // las relaciones del cultivo en otras tablas antes de eliminarlo de la tabla principal.
        // El orden es importante para no violar restricciones de llaves foráneas.

        for (String sql : sqls) {
            // Se recorre cada consulta del arreglo.

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                // Se prepara la consulta actual.

                ps.setInt(1, cultivoId);
                // Se reemplaza el ? con el ID del cultivo recibido como parámetro.

                ps.executeUpdate();
                // Se ejecuta la consulta preparada.
                // Esto elimina los registros relacionados con el cultivo en cada tabla.
            }
        }

        conn.commit(); // Confirmar cambios
        // Si todas las consultas se ejecutaron correctamente, se confirma la transacción.
        // Esto aplica definitivamente los cambios en la base de datos.

        return true;
        // Devuelve true indicando que el cultivo y sus relaciones fueron eliminados.

    } catch (Exception e) {
        // Si ocurre algún error durante la ejecución:

        if (conn != null) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            // Se revierte la transacción para deshacer cualquier cambio parcial.
        }

        e.printStackTrace();
        // Se imprime el error en la consola para depuración.

        return false;
        // Devuelve false indicando que la eliminación no se completó.
    } finally {
        // Bloque que siempre se ejecuta, haya error o no.

        // Usa tu método cerrar o cierra manualmente aquí
        try { if(conn != null) conn.close(); } catch(Exception ex){}
        // Se cierra la conexión si estaba abierta.
    }
}

    public Map<String, String> obtenerCultivoPorId(int id) {
    // Método que obtiene un cultivo específico de la base de datos usando su ID.
    // Devuelve un Map con los datos del cultivo.

    Map<String, String> cultivo = new HashMap<>();
    // Se crea un Map vacío donde se guardarán los datos del cultivo.

    String sql = "SELECT * FROM cultivos WHERE id = ?";
    // Se define la consulta SQL que selecciona todos los campos de la tabla "cultivos"
    // filtrando por el ID recibido como parámetro.

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        // Se abre una conexión con la base de datos usando tu clase Conexion.
        // Se prepara la consulta SQL para poder reemplazar el ? con un valor real.

        ps.setInt(1, id);
        // Se reemplaza el primer ? de la consulta con el ID del cultivo recibido como parámetro.

        try (ResultSet rs = ps.executeQuery()) {
            // Se ejecuta la consulta preparada y se guarda el resultado en un ResultSet.
            // El ResultSet contiene las filas que cumplen la condición (en este caso, solo una).

            if (rs.next()) {
                // Si la consulta devuelve una fila (es decir, el cultivo existe):

                cultivo.put("id", String.valueOf(rs.getInt("id")));
                // Se obtiene el valor de la columna "id" y se guarda en el Map.

                cultivo.put("nombre", rs.getString("nombre"));
                // Se obtiene el nombre del cultivo y se guarda en el Map.

                cultivo.put("fecha_siembra", rs.getString("fecha_siembra"));
                // Se obtiene la fecha de siembra y se guarda en el Map.

                cultivo.put("fecha_cosecha", rs.getString("fecha_cosecha"));
                // Se obtiene la fecha de cosecha y se guarda en el Map.

                cultivo.put("ciclo", rs.getString("ciclo"));
                // Se obtiene el ciclo del cultivo y se guarda en el Map.

                cultivo.put("estado", rs.getString("estado"));
                // Se obtiene el estado del cultivo (ejemplo: Activo, Inactivo).

                String supId = rs.getString("supervisor_id");
                // Se obtiene el ID del supervisor asignado al cultivo.
                // Puede ser NULL si no hay supervisor.

                cultivo.put("supervisor_id", supId != null ? supId : "");
                // Se guarda el ID del supervisor en el Map.
                // Si es NULL, se guarda un string vacío "".
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
        // Si ocurre un error (ejemplo: problema de conexión o SQL mal escrito),
        // se imprime el error en la consola para depuración.
    }

    return cultivo;
    // Finalmente, se devuelve el Map con los datos del cultivo encontrado.
}

public String editarCultivoCompleto(String id, String nombre, String fechaSiembra, 
        String fechaCosecha, String ciclo, String estado, int supervisorId,
        String[] productoIds, String[] cantidades, String[] trabajadoresIds) {
    // Método para actualizar un cultivo existente, sus insumos y sus trabajadores asignados.

    Connection conn = null;
    // Se inicializa la variable de conexión en nulo.

    List<String> productosAgotados = new ArrayList<>();
    // Se crea una lista para anotar los nombres de insumos que lleguen a cero durante la edición.

    int cultivoId = Integer.parseInt(id);
    // Se convierte el ID del cultivo de String a entero para usarlo en las consultas SQL.

    try {
    // Inicia el bloque de seguridad para el manejo de la base de datos.

        conn = Conexion.getConexion();
        // Se establece la conexión con el servidor de MySQL.

        conn.setAutoCommit(false);
        // Se desactiva el guardado automático para asegurar que todos los pasos se cumplan o ninguno se aplique.

        // --- 1. ACTUALIZAR DATOS PRINCIPALES DEL CULTIVO ---
        String sqlUpdate = "UPDATE cultivos SET nombre=?, fecha_siembra=?, fecha_cosecha=?, ciclo=?, estado=?, supervisor_id=? WHERE id=?";
        // Se define la consulta para actualizar la información básica en la tabla "cultivos".

        try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
        // Se prepara la sentencia de actualización.

            ps.setString(1, nombre);
            // Se asigna el nuevo nombre del cultivo.

            ps.setString(2, fechaSiembra);
            // Se asigna la nueva fecha de siembra.

            ps.setString(3, (fechaCosecha != null && !fechaCosecha.isEmpty()) ? fechaCosecha : null);
            // Si la fecha de cosecha está vacía, se manda NULL a la DB; si tiene datos, se asigna.

            ps.setString(4, ciclo);
            // Se asigna el ciclo.

            ps.setString(5, estado);
            // Se asigna el estado .

            if (supervisorId == 0) {
            // Si el valor del supervisor es 0 (no seleccionado):

                ps.setNull(6, java.sql.Types.INTEGER);
                // Se envía un valor nulo a la columna supervisor_id.

            } else {
            // Si hay un supervisor válido seleccionado:

                ps.setInt(6, supervisorId);
                // Se asigna el ID del supervisor.

            }

            ps.setInt(7, cultivoId);
            // Se filtra la actualización por el ID del cultivo que estamos editando.

            ps.executeUpdate();
            // Se ejecuta la actualización de los datos principales.
        }

        // --- 2. DEVOLVER AL INVENTARIO LAS CANTIDADES ANTERIORES ---
        String sqlRecuperar = "SELECT producto_id, cantidad FROM stock_cultivo WHERE cultivo_id = ?";
        // Se prepara la consulta para saber qué productos y qué cantidades tenía asignadas este cultivo antes de la edición.

        try (PreparedStatement psRec = conn.prepareStatement(sqlRecuperar)) {
        // Se prepara la sentencia de selección de stock viejo.

            psRec.setInt(1, cultivoId);
            // Se busca por el ID del cultivo actual.

            ResultSet rsRec = psRec.executeQuery();
            // Se obtienen los registros de productos usados anteriormente.

            while (rsRec.next()) {
            // Se recorre cada producto que el cultivo tenía asignado:

                int pId = rsRec.getInt("producto_id");
                // Se obtiene el ID del producto.

                int cantAnterior = rsRec.getInt("cantidad");
                // Se obtiene la cantidad que se había gastado antes.

                String sqlDevolver = "UPDATE productos SET cantidad = cantidad + ? WHERE id = ?";
                // Se prepara la consulta para SUMAR (devolver) esa cantidad al inventario general.

                try (PreparedStatement psDev = conn.prepareStatement(sqlDevolver)) {
                // Se prepara la sentencia de devolución al stock.

                    psDev.setInt(1, cantAnterior);
                    // Se le suma la cantidad vieja al total.

                    psDev.setInt(2, pId);
                    // Se especifica a qué producto se le devuelve el stock.

                    psDev.executeUpdate();
                    // Se ejecuta la devolución en la tabla "productos".
                }
            }
        }

        // --- 3. BORRAR PRODUCTOS Y TRABAJADORES ANTERIORES DEL CULTIVO ---
        String sqlBorrarProd = "DELETE FROM stock_cultivo WHERE cultivo_id = ?";
        // Se prepara la orden para limpiar los registros de insumos viejos de este cultivo.

        try (PreparedStatement psBP = conn.prepareStatement(sqlBorrarProd)) {
        // Se prepara la sentencia de eliminación de productos asignados.

            psBP.setInt(1, cultivoId);
            // Se filtra por el ID del cultivo actual.

            psBP.executeUpdate();
            // Se eliminan los registros viejos para evitar duplicados.
        }

        String sqlBorrarTrab = "DELETE FROM cultivo_trabajador WHERE cultivo_id = ?";
        // Se prepara la orden para limpiar los trabajadores asignados anteriormente.

        try (PreparedStatement psBT = conn.prepareStatement(sqlBorrarTrab)) {
        // Se prepara la sentencia de eliminación de trabajadores.

            psBT.setInt(1, cultivoId);
            // Se filtra por el ID del cultivo.

            psBT.executeUpdate();
            // Se borran las asignaciones anteriores para registrar las nuevas.
        }

        // --- 4. REINSERTAR PRODUCTOS CON NUEVAS CANTIDADES Y DESCONTAR STOCK ---
        if (productoIds != null) {
        // Si el usuario seleccionó productos en el formulario de edición:

            for (int i = 0; i < productoIds.length; i++) {
            // Se recorre el arreglo de nuevos productos seleccionados.

                if (productoIds[i] == null || productoIds[i].isEmpty()) continue;
                // Si la posición está vacía, se ignora y se sigue con el siguiente.

                int pId = Integer.parseInt(productoIds[i]);
                // Se convierte el ID del nuevo producto a entero.

                int cant = (cantidades != null && i < cantidades.length && !cantidades[i].isEmpty())
                           ? Integer.parseInt(cantidades[i]) : 1;
                // Se obtiene la nueva cantidad; si está vacía, se asigna 1 por defecto.

                // 4a. Verificar stock disponible
                String sqlStock = "SELECT nombre, cantidad FROM productos WHERE id = ?";
                // Se consulta si hay suficiente existencia del nuevo producto.

                try (PreparedStatement psStock = conn.prepareStatement(sqlStock)) {
                // Se prepara la consulta de validación.

                    psStock.setInt(1, pId);
                    // Se busca por el ID del producto.

                    ResultSet rsStock = psStock.executeQuery();
                    // Se obtienen los datos de la bodega.

                    if (rsStock.next()) {
                    // Si el producto existe:

                        int disponible = rsStock.getInt("cantidad");
                        // Se extrae cuánto hay disponible actualmente.

                        String nombreProducto = rsStock.getString("nombre");
                        // Se obtiene el nombre para el mensaje de error.

                        if (disponible < cant) {
                        // Si lo que se quiere usar supera lo que hay en bodega:

                            conn.rollback();
                            // Se deshacen todos los cambios (incluyendo las devoluciones del Paso 2).

                            return "insuficiente:" + nombreProducto + " (disponible: " + disponible + ")";
                            // Se informa que no hay stock suficiente para completar la edición.
                        }
                    }
                }

                // 4b. Insertar en stock_cultivo
                String sqlProd = "INSERT INTO stock_cultivo(cultivo_id, producto_id, cantidad) VALUES(?,?,?)";
                // Se prepara la inserción de la nueva relación producto-cultivo.

                try (PreparedStatement psProd = conn.prepareStatement(sqlProd)) {
                // Se prepara la sentencia de registro.

                    psProd.setInt(1, cultivoId);
                    // Se vincula al ID del cultivo editado.

                    psProd.setInt(2, pId);
                    // Se vincula al nuevo producto.

                    psProd.setInt(3, cant);
                    // Se registra la nueva cantidad usada.

                    psProd.executeUpdate();
                    // Se guarda el nuevo registro de consumo.
                }

                // 4c. Descontar del inventario
                String sqlDescontar = "UPDATE productos SET cantidad = cantidad - ? WHERE id = ?";
                // Se prepara la orden para restar la nueva cantidad del inventario general.

                try (PreparedStatement psDesc = conn.prepareStatement(sqlDescontar)) {
                // Se prepara la sentencia de descuento de stock.

                    psDesc.setInt(1, cant);
                    // Se indica cuánto restar.

                    psDesc.setInt(2, pId);
                    // Se indica a qué producto restarle.

                    psDesc.executeUpdate();
                    // Se aplica la resta en la tabla "productos".
                }

                // 4d. Verificar si quedó en 0
                String sqlVerificar = "SELECT nombre, cantidad FROM productos WHERE id = ?";
                // Se consulta el estado final del producto tras el descuento.

                try (PreparedStatement psVer = conn.prepareStatement(sqlVerificar)) {
                // Se prepara la sentencia de verificación.

                    psVer.setInt(1, pId);
                    // Se busca el producto.

                    ResultSet rsVer = psVer.executeQuery();
                    // Se obtiene el resultado.

                    if (rsVer.next() && rsVer.getInt("cantidad") <= 0) {
                    // Si el stock quedó en cero o menos:

                        productosAgotados.add(rsVer.getString("nombre"));
                        // Se anota el nombre del producto en la lista de avisos.
                    }
                }
            }
        }

        // --- 5. REINSERTAR TRABAJADORES ---
        if (trabajadoresIds != null) {
        // Si se seleccionaron trabajadores en la edición:

            for (String tId : trabajadoresIds) {
            // Se recorre la lista de IDs de trabajadores.

                if (tId != null && !tId.isEmpty()) {
                // Si el ID es válido:

                    String sqlTrab = "INSERT INTO cultivo_trabajador(cultivo_id, usuario_id) VALUES(?,?)";
                    // Se prepara la consulta para asignar al trabajador al cultivo.

                    try (PreparedStatement psTrab = conn.prepareStatement(sqlTrab)) {
                    // Se prepara la sentencia de inserción.

                        psTrab.setInt(1, cultivoId);
                        // Se vincula al cultivo.

                        psTrab.setInt(2, Integer.parseInt(tId));
                        // Se vincula al ID del trabajador.

                        psTrab.executeUpdate();
                        // Se guarda la nueva asignación de personal.
                    }
                }
            }
        }

        conn.commit();
        // Se confirman todos los cambios (Update del cultivo, devoluciones y nuevos descuentos) en la base de datos.

        if (!productosAgotados.isEmpty()) {
        // Si hay productos que se terminaron durante el proceso:

            return "agotado:" + String.join(",", productosAgotados);
            // Se devuelve el mensaje de éxito pero avisando qué productos quedaron en cero.
        }

        return "ok";
        // Se devuelve "ok" indicando que la edición fue totalmente exitosa.

    } catch (Exception e) {
    // Si ocurre un error en cualquier parte del proceso:

        if (conn != null) {
        // Si la conexión estaba activa:

            try { 
                conn.rollback(); 
                // Se deshacen todos los cambios para no dejar el inventario descuadrado.
            } catch (SQLException ex) { ex.printStackTrace(); }
        }

        e.printStackTrace();
        // Se imprime el error en consola para depuración.

        return "error";
        // Se informa al Servlet que la edición falló.

    } finally {
    // Bloque que se ejecuta siempre al finalizar la operación.

        if (conn != null) {
        // Si la conexión sigue abierta:

            try { 
                conn.setAutoCommit(true); 
                // Se restablece el comportamiento normal de la DB.

                conn.close(); 
                // Se cierra la conexión para liberar recursos.
            } catch (SQLException ex) {}
        }
    }
}
}