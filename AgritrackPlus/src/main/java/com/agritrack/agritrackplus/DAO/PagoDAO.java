package com.agritrack.agritrackplus.DAO;
// Define la ubicación física del archivo en la estructura de carpetas del proyecto.

import com.agritrack.agritrackplus.db.Conexion;
// Importa tu clase de conexión para que el DAO pueda hablar con el servidor MySQL.

import java.sql.*;
// Carga el kit de herramientas de JDBC (sentencias, conexiones y resultados).

import java.util.*;
// Carga las estructuras de datos (Listas y Mapas) para organizar la información en memoria.

public class PagoDAO {
// Inicia la clase encargada de transformar registros de la DB en objetos de nómina para Java.

    public List<Map<String, String>> buscarTareasPorSupervisor(String criterio) {
    // Busca labores agrícolas que dependen de un supervisor específico (por nombre o cédula).

        List<Map<String, String>> lista = new ArrayList<>();
        // Prepara el contenedor donde guardaremos cada fila encontrada como un "paquete" de datos.

        String sql = "SELECT c.nombre AS cultivo, t.nombre AS tarea, ut.estado " +
                     "FROM usuario_tarea ut " +
                     "JOIN cultivos c ON ut.cultivo_id = c.id " +
                     "JOIN tareas t ON ut.tarea_id = t.id " +
                     "JOIN usuarios u ON c.supervisor_id = u.id " +
                     "WHERE (u.nombre LIKE ? OR u.documento = ?) " +
                     "AND ut.estado IN ('Completada', 'En Proceso') " +
                     "ORDER BY c.nombre, t.nombre";
        // Sentencia SQL que cruza 4 tablas para saber: ¿Qué se hizo? ¿En qué cultivo? y ¿Quién manda ahí?

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
        // Establece el canal de comunicación y "pre-cocina" la consulta para evitar errores de sintaxis.

            ps.setString(1, "%" + criterio + "%");
            // Inyecta el nombre del supervisor. Los "%" permiten buscar "Juan" aunque el usuario escriba "Jua".

            ps.setString(2, criterio);
            // Inyecta el documento exacto. Aquí no usamos "%" porque la cédula debe ser idéntica.

            try (ResultSet rs = ps.executeQuery()) {
            // Ejecuta el "SELECT" y guarda la tabla de resultados en el objeto 'rs'.

                while (rs.next()) {
                // Inicia un ciclo que se repetirá mientras haya filas por leer en el resultado.

                    Map<String, String> m = new HashMap<>();
                    // Crea un diccionario temporal (Clave -> Valor) para representar una tarea.

                    m.put("cultivo", rs.getString("cultivo"));
                    // Extrae el nombre del lote/cultivo y lo guarda bajo la etiqueta "cultivo".

                    m.put("tarea",   rs.getString("tarea"));
                    // Extrae el nombre de la labor (ej: Deshierbe) y lo guarda como "tarea".

                    m.put("estado",  rs.getString("estado"));
                    // Guarda si está terminada o en curso para que el administrador decida si la paga.

                    lista.add(m);
                    // Mete el paquete de datos en la lista general de resultados.
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        // Si hay un error (ej: DB apagada), lo muestra en la consola de NetBeans/Eclipse.

        return lista;
        // Envía la lista completa al Servlet para que la mande al JSP.
    }

    public boolean registrarPago(String nombre, String documento, double monto) {
    // Este es el corazón del pago: crea el recibo y limpia la deuda de tareas en una sola transacción.

        Connection con = null;
        // Declaramos la conexión fuera para poder hacer 'rollback' si algo sale mal.

        String sqlBusqueda = "SELECT id FROM usuarios WHERE documento = ? OR nombre LIKE ?";
        // Paso previo: necesitamos el ID numérico interno, no solo el nombre que viene del formulario.

        String sqlInsert = "INSERT INTO pagos (usuario_id, fecha_pago, estado, pago) VALUES (?, CURDATE(), 'Activo', ?)";
        // Registra el egreso de dinero. CURDATE() le dice a MySQL que use la fecha de hoy automáticamente.

        String sqlUpdateTareas = "UPDATE usuario_tarea SET estado = 'Pagado' " +
                                 "WHERE usuario_id = ? AND estado IN ('Completada', 'En Proceso')";
        // Actualiza el estado de las tareas para que no aparezcan como "pendientes" en el siguiente cobro.

        try {
            con = com.agritrack.agritrackplus.db.Conexion.getConexion();
            // Abre la conexión con AgritrackPlus_DB.

            con.setAutoCommit(false); 
            // ¡VITAL!: Detiene el guardado automático. Así, si el pago se inserta pero el update falla, nada se guarda.

            int usuarioId = -1;
            // Inicializa una variable de seguridad para validar si el trabajador existe.

            try (PreparedStatement psBusqueda = con.prepareStatement(sqlBusqueda)) {
            // Prepara la búsqueda del ID del trabajador.

                psBusqueda.setString(1, documento.trim());
                // Quita espacios accidentales y asigna el documento al primer parámetro.

                psBusqueda.setString(2, "%" + nombre.trim() + "%");            
                // Asigna el nombre con comodines al segundo parámetro.

                try (ResultSet rs = psBusqueda.executeQuery()) {
                // Ejecuta la búsqueda.

                    if (rs.next()) {
                    // Si el ResultSet tiene al menos un registro:

                        usuarioId = rs.getInt("id");
                        // Capturamos el ID real de la columna 'id' de la tabla usuarios.
                    }
                }
            }

            if (usuarioId == -1) {
            // Si el ID sigue siendo -1, el sistema no sabe a quién pagarle.

                return false;
                // Aborta la operación y avisa al controlador que el usuario no existe.
            }

            try (PreparedStatement psInsert = con.prepareStatement(sqlInsert)) {
            // Prepara la inserción del dinero en la tabla pagos.

                psInsert.setInt(1, usuarioId);
                // Vincula el pago al ID que acabamos de encontrar.

                psInsert.setDouble(2, monto);
                // Inyecta el valor total a pagar (ej: 50000.0).

                psInsert.executeUpdate();
                // Ejecuta la inserción del registro de pago.
            }

            try (PreparedStatement psUpdate = con.prepareStatement(sqlUpdateTareas)) {
            // Prepara la limpieza de la deuda de tareas.

                psUpdate.setInt(1, usuarioId);
                // Filtra para que solo afecte las tareas del trabajador que está cobrando.

                psUpdate.executeUpdate();
                // Cambia el estado de todas sus labores a 'Pagado'.
            }

            con.commit();
            // FINALIZACIÓN: Si llegamos aquí sin errores, guardamos físicamente todo en el disco duro.

            return true;
            // Éxito: el dinero se registró y las tareas se marcaron como liquidadas.

        } catch (SQLException e) {
        // Si ocurre un error (ej: el servidor se desconecta a mitad del proceso):

            if (con != null) {
                try { 
                    con.rollback(); 
                    // REVERSIÓN: Deshace el insert del pago para que no aparezca que pagamos si no pudimos marcar tareas.
                } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
            // Error: se canceló todo para mantener la integridad de las cuentas.

        } finally {
        // Bloque de limpieza que se ejecuta siempre, pase lo que pase.

            if (con != null) {
                try { 
                    con.setAutoCommit(true); 
                    // Regresa la conexión a su estado normal de autoguardado.

                    con.close(); 
                    // Cierra el grifo de la conexión para no agotar la memoria del servidor.
                } catch (SQLException e) {}
            }
        }
    }

    public List<Map<String, String>> buscarTareasPorTrabajador(String criterio) {
    // Obtiene el detalle de qué ha hecho un trabajador para calcular cuánto se le debe.

        List<Map<String, String>> tareas = new ArrayList<>();
        // Lista vacía para acumular los trabajos realizados.

        String sql = "SELECT ut.id, t.nombre AS tarea, ut.estado, ut.jornada, c.nombre AS cultivo " +
                     "FROM usuario_tarea ut " +
                     "JOIN usuarios u ON ut.usuario_id = u.id " +
                     "JOIN tareas t ON ut.tarea_id = t.id " +
                     "JOIN cultivos c ON ut.cultivo_id = c.id " +
                     "WHERE (u.nombre LIKE ? OR u.documento = ?) " +
                     "AND ut.estado IN ('Completada', 'En Proceso')";
        // Consulta que une tareas con el trabajador y el lote para mostrar un reporte claro.

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
        // Conecta y prepara la consulta de labores pendientes.

            ps.setString(1, "%" + criterio + "%");
            // Parámetro para buscar por nombre del trabajador.

            ps.setString(2, criterio);
            // Parámetro para buscar por documento exacto.

            try (ResultSet rs = ps.executeQuery()) {
            // Lanza la petición a MySQL.

                while (rs.next()) {
                // Procesa cada tarea encontrada:

                    Map<String, String> tarea = new HashMap<>();
                    // Diccionario para la labor actual.

                    tarea.put("id",      rs.getString("id"));
                    // ID de la relación usuario_tarea para posibles futuras ediciones.

                    tarea.put("tarea",   rs.getString("tarea"));
                    // Nombre descriptivo (ej: Aplicación de fertilizante).

                    tarea.put("estado",  rs.getString("estado"));
                    // Estado actual (Completada/En Proceso).

                    tarea.put("cultivo", rs.getString("cultivo"));
                    // En qué parte de la finca se realizó el trabajo.

                    String jornada = rs.getString("jornada");
                    // Recupera si fue día completo o medio día.

                    tarea.put("jornada", (jornada != null) ? jornada : "Medio Dia");
                    // Regla de negocio: si no se especificó jornada, el sistema asume "Medio Dia".

                    tareas.add(tarea);
                    // Guarda la tarea en la lista final.
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en buscarTareasPorTrabajador: " + e.getMessage());
            // Imprime el mensaje específico de error en la consola de depuración.
        }
        return tareas;
        // Retorna el listado de labores para que el Servlet calcule el 'monto' total.
    }
}