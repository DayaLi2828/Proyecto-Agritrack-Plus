package com.agritrack.agritrackplus.DAO;

import com.agritrack.agritrackplus.db.Conexion;
import com.agritrack.agritrackplus.modelo.Tarea;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import java.util.List;
import java.util.Map;
/*
Permite listar cultivos, trabajadores y tareas desde catálogos, filtrando por rol, supervisor o cultivo específico. Incluye métodos para crear tareas usando transacciones que 
garantizan consistencia entre las tablas tareas y usuario_tarea. Permite actualizar el estado de una tareaProvee estadísticas como conteos por estado, porcentajes de 
cumplimiento por cultivo y resumen de pagos por trabajador. Para el dashboard del supervisor ofrece datos listos para gráficas: tareas por día de la semana, avance por 
cultivo y distribución por estado. También incluye búsqueda de tareas filtrando por nombre o documento del supervisor responsable.
*/
public class TareaDAO {

    public TareaDAO() {
        // Constructor vacío para manejar conexiones por método
    }

   // 1. MÉTODO PARA EL SELECT DE CULTIVOS
// Define un método público que devuelve una Lista de objetos de tipo 'Tarea'
public List<Tarea> listarCultivos() {
    
    // Crea una nueva instancia de ArrayList para almacenar los resultados que traigamos de la BD
    List<Tarea> lista = new ArrayList<>();
    
    // Define la consulta SQL: selecciona el ID y el Nombre de la tabla 'cultivos' 
    // pero solo de aquellos cuyo estado sea exactamente 'Activo'
    String sql = "SELECT id, nombre FROM cultivos WHERE estado = 'Activo'";
    
    // Bloque 'try-with-resources': asegura que la conexión, el statement y el resultset 
    // se cierren automáticamente al finalizar, incluso si ocurre un error.
    try (
         // 1. Obtiene la conexión a la base de datos llamando al método getConexion() de tu clase Conexion
         Connection con = new Conexion().getConexion();
        
         // 2. Prepara la sentencia SQL para ser enviada a la base de datos de forma segura
         PreparedStatement ps = con.prepareStatement(sql);
        
         // 3. Ejecuta la consulta (SELECT) y guarda los datos devueltos en un objeto ResultSet (rs)
         ResultSet rs = ps.executeQuery()
        ) {
        
        // Mientras el ResultSet tenga una fila siguiente (mientras haya datos en la tabla)
        while (rs.next()) {
            
            // Crea un nuevo objeto de tipo Tarea para representar la fila actual
            Tarea t = new Tarea();
            
            // Extrae el valor de la columna "id" del ResultSet y lo guarda en el atributo cultivoId del objeto
            t.setCultivoId(rs.getInt("id"));
            
            // Extrae el texto de la columna "nombre" del ResultSet y lo guarda en el atributo nombreCultivo del objeto
            t.setNombreCultivo(rs.getString("nombre"));
            
            // Añade el objeto 't' (ya con datos) a la lista que declaramos al inicio
            lista.add(t);
        }
        
    } catch (SQLException e) { 
        // Si algo falla en la conexión o en la consulta, imprime el error en la consola para depurar
        e.printStackTrace(); 
    }
    
    // Devuelve la lista completa (esté llena con cultivos o vacía si hubo error/no hay datos)
    return lista;
}

   // 2. MÉTODO PARA EL SELECT DE TRABAJADORES
// Define un método público que retorna una lista de objetos 'Tarea' con la información de los empleados.
public List<Tarea> listarTrabajadores() {
    
    // Crea una lista vacía de tipo ArrayList para ir guardando los trabajadores encontrados.
    List<Tarea> lista = new ArrayList<>();
    
    // Define la consulta SQL con un JOIN:
    // 1. 'usuarios u': Selecciona de la tabla usuarios (usando el alias 'u').
    // 2. 'JOIN roles_usuarios ru': Une la tabla de usuarios con la tabla que asigna los roles.
    // 3. 'ON u.id = ru.usuario_id': La unión se hace donde coincidan los IDs de usuario.
    // 4. 'WHERE ru.rol_id = 2': Filtra para traer solo a los que tienen asignado el rol con ID 2 (Trabajadores).
    String sql = "SELECT u.id, u.nombre FROM usuarios u " +
                 "JOIN roles_usuarios ru ON u.id = ru.usuario_id " +
                 "WHERE ru.rol_id = 2";
    
    // Inicia el bloque try-with-resources para gestionar la apertura y cierre automático de recursos.
    try (
         // Crea una nueva instancia de la clase Conexion y obtiene el objeto Connection.
         Connection con = new Conexion().getConexion();
        
         // Prepara la consulta SQL para ser enviada de forma eficiente a la base de datos.
         PreparedStatement ps = con.prepareStatement(sql);
        
         // Ejecuta la consulta y guarda el "set de resultados" (filas y columnas) en la variable 'rs'.
         ResultSet rs = ps.executeQuery()
        ) {
        
        // Mientras el cursor del ResultSet encuentre una fila de datos disponible:
        while (rs.next()) {
            
            // Instancia un nuevo objeto de la clase Tarea (donde mapearemos los datos del trabajador).
            Tarea t = new Tarea();
            
            // Obtiene el valor de la columna "id" de la base de datos y lo asigna al atributo 'Id' del objeto.
            t.setId(rs.getInt("id")); 
            
            // Obtiene el valor de la columna "nombre" y lo asigna al atributo 'NombreTrabajador' del objeto.
            t.setNombreTrabajador(rs.getString("nombre"));
            
            // Agrega el objeto 't' ya poblado con datos a nuestra lista de resultados.
            lista.add(t);
        }
        
    } catch (SQLException e) { 
        // Si ocurre un error (problemas de red, error en el SQL, etc.), imprime el rastro del error.
        e.printStackTrace(); 
    }
    
    // Retorna la lista con todos los trabajadores (o una lista vacía si no encontró ninguno).
    return lista;
}
    // MÉTODO PARA LISTAR TRABAJADORES ASOCIADOS A UN CULTIVO ESPECÍFICO
// Recibe como parámetro el 'cultivoId' para saber de qué cultivo queremos traer el personal.
public List<Tarea> listarTrabajadoresPorCultivo(int cultivoId) {
    
    // Inicializa una lista vacía de objetos 'Tarea' para almacenar los trabajadores encontrados.
    List<Tarea> lista = new ArrayList<>();
    
    // Define la consulta SQL con un JOIN entre la tabla 'usuarios' y la tabla intermedia 'cultivo_trabajador'.
    // El '?' es un marcador de posición que será reemplazado por el ID del cultivo recibido.
    String sql = "SELECT u.id, u.nombre FROM usuarios u " +
                 "JOIN cultivo_trabajador ct ON u.id = ct.usuario_id " +
                 "WHERE ct.cultivo_id = ?";
    
    // Bloque try-with-resources: Abre la conexión y prepara la sentencia SQL de forma segura.
    try (
         // Obtiene la conexión a través del método estático de la clase Conexion.
         Connection con = Conexion.getConexion();
        
         // Prepara la ejecución de la consulta SQL definida arriba.
         PreparedStatement ps = con.prepareStatement(sql)
        ) {
        
        // Asigna el valor del parámetro 'cultivoId' al primer (y único) signo de interrogación '?' en el SQL.
        // Esto protege tu base de datos contra ataques de Inyección SQL.
        ps.setInt(1, cultivoId);
        
        // Ejecuta la consulta y abre un segundo bloque try-with-resources exclusivo para el ResultSet (rs).
        try (ResultSet rs = ps.executeQuery()) {
            
            // Recorre cada registro devuelto por la base de datos que coincida con el cultivo solicitado.
            while (rs.next()) {
                
                // Crea una nueva instancia del objeto Tarea para mapear la fila actual.
                Tarea t = new Tarea();
                
                // Extrae el ID del usuario desde la columna 'id' y lo guarda en el objeto.
                t.setId(rs.getInt("id"));
                
                // Extrae el nombre del usuario desde la columna 'nombre' y lo guarda como nombre del trabajador.
                t.setNombreTrabajador(rs.getString("nombre"));
                
                // Agrega el trabajador configurado a la lista de resultados.
                lista.add(t);
            }
        }
        
    } catch (SQLException e) { 
        // Si hay un error en la base de datos o en la conexión, se imprime el rastro de la excepción.
        e.printStackTrace(); 
    }
    
    // Retorna la lista de trabajadores vinculados a ese cultivo específico.
    return lista;
}
   // 3. MÉTODO PARA EL SELECT DE TIPOS DE TAREAS (Catálogo)
// Declara un método público que devuelve una lista de objetos 'Tarea' con las definiciones base del sistema.
public List<Tarea> listarCatalogoTareas() {
    
    // Crea una nueva lista de tipo ArrayList para almacenar las tareas que encontremos en el catálogo.
    List<Tarea> lista = new ArrayList<>();
    
    // Define la consulta SQL: selecciona el ID y el Nombre de la tabla general de 'tareas'.
    // Esta tabla funciona como un diccionario de actividades disponibles.
    String sql = "SELECT id, nombre FROM tareas";
    
    // Bloque try-with-resources: gestiona la conexión y los objetos de base de datos.
    try (
         // 1. Abre la conexión llamando a tu clase Conexion.
         Connection con = new Conexion().getConexion();
        
         // 2. Prepara la sentencia SQL para ser ejecutada.
         PreparedStatement ps = con.prepareStatement(sql);
        
         // 3. Ejecuta la consulta y obtiene los resultados en el ResultSet 'rs'.
         ResultSet rs = ps.executeQuery()
        ) {
        
        // Inicia un bucle que se ejecutará mientras existan registros en el ResultSet.
        while (rs.next()) {
            
            // Instancia un nuevo objeto Tarea para representar la actividad del catálogo.
            Tarea t = new Tarea();
            
            // Extrae el ID de la base de datos y lo guarda en el atributo 'Id' del objeto Tarea.
            t.setId(rs.getInt("id"));
            
            // Extrae el nombre de la tarea (ej: "Abonar") y lo guarda en el atributo 'NombreTarea'.
            t.setNombreTarea(rs.getString("nombre"));
            
            // Añade el objeto configurado a la lista que retornaremos al final.
            lista.add(t);
        }
        
    } catch (SQLException e) { 
        // Si hay algún error en el proceso (ej: la tabla no existe o error de red), imprime el error.
        e.printStackTrace(); 
    }
    
    // Retorna la lista con todas las tareas encontradas en el catálogo.
    return lista;
}

    // 4. AGREGAR TAREA (Desde catálogo)
// Define un método que recibe los datos de la tarea y retorna un booleano (true si tuvo éxito, false si falló).
public boolean agregarTarea(int idCultivo, String descripcion, String nombreManual, String jornada, int idTrabajador) {
    
    // SQL 1: Inserta el nombre de la nueva tarea en la tabla 'tareas'.
    String sqlTarea = "INSERT INTO tareas (nombre) VALUES (?)";
    
    // SQL 2: Inserta la relación en 'usuario_tarea' usando el ID de la tarea que acabamos de crear arriba.
    String sqlAsignacion = "INSERT INTO usuario_tarea (cultivo_id, tarea_id, usuario_id, descripcion_actividad, jornada, estado) VALUES (?, ?, ?, ?, ?, 'Pendiente')";

    // Inicia el bloque try-with-resources para obtener la conexión.
    try (Connection con = new Conexion().getConexion()) {
        
        // DESACTIVA EL AUTO-COMMIT: Esto es vital. Le dice a la base de datos: 
        // "No guardes nada permanentemente hasta que yo te dé la orden final (commit)".
        con.setAutoCommit(false); 
        
        // Prepara el primer SQL y añade el parámetro 'RETURN_GENERATED_KEYS' para poder obtener el ID que la base de datos le asigne automáticamente.
        try (PreparedStatement psTarea = con.prepareStatement(sqlTarea, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            
            // Setea el nombre de la tarea (ej. "Limpieza de maleza") en el primer '?'.
            psTarea.setString(1, nombreManual);
            
            // Ejecuta la inserción en la tabla 'tareas'.
            psTarea.executeUpdate();

            // Obtiene la llave (ID) generada automáticamente por la base de datos para esa nueva tarea.
            ResultSet rs = psTarea.getGeneratedKeys();
            int idNuevaTarea = 0;
            
            // Si la base de datos devolvió un ID, lo guarda en la variable 'idNuevaTarea'.
            if (rs.next()) { idNuevaTarea = rs.getInt(1); }

            // Segundo bloque try interno para preparar la asignación de la tarea al trabajador.
            try (PreparedStatement psAsign = con.prepareStatement(sqlAsignacion)) {
                
                // Remplaza los 5 signos de interrogación '?' con los valores correspondientes:
                psAsign.setInt(1, idCultivo);      // ID del cultivo donde se hará la labor.
                psAsign.setInt(2, idNuevaTarea);   // El ID que acabamos de recuperar de la tabla anterior.
                psAsign.setInt(3, idTrabajador);   // ID del trabajador asignado.
                psAsign.setString(4, descripcion); // Detalles de la actividad.
                psAsign.setString(5, jornada);     // Si es mañana, tarde o día completo.
                
                // Ejecuta la segunda inserción en 'usuario_tarea'.
                psAsign.executeUpdate();
            }
            
            // COMMIT: Si ambas inserciones fueron exitosas, se da la orden final para guardar todo en la BD.
            con.commit(); 
            return true;
            
        } catch (SQLException e) {
            // ROLLBACK: Si algo falló (por ejemplo, el ID del trabajador no existe), se deshacen los cambios.
            // Esto evita que se cree una tarea en la tabla 'tareas' si no se pudo asignar al usuario.
            con.rollback(); 
            e.printStackTrace();
            return false;
        }
    } catch (SQLException e) { 
        // Captura errores generales de conexión.
        e.printStackTrace(); 
        return false; 
    }
}

   // 5. AGREGAR TAREA MANUAL
// Declara el método que recibe los parámetros necesarios para crear una tarea desde cero y asignarla.
public boolean agregarTareaManual(int idCultivo, String descripcion, String nombreTarea, String jornada, int idTrabajador) {
    
    // SQL 1: Sentencia para insertar el nombre de la nueva tarea personalizada en la tabla 'tareas'.
    String sqlTarea = "INSERT INTO tareas (nombre) VALUES (?)";
    
    // SQL 2: Sentencia para vincular esa nueva tarea con un cultivo, un trabajador y sus detalles específicos.
    // Por defecto, toda tarea manual inicia con el estado 'Pendiente'.
    String sqlAsignacion = "INSERT INTO usuario_tarea (cultivo_id, tarea_id, usuario_id, descripcion_actividad, jornada, estado) VALUES (?, ?, ?, ?, ?, 'Pendiente')";

    // Intenta obtener una conexión a la base de datos.
    try (Connection con = new Conexion().getConexion()) {
        
        // Desactiva el inicio automático de cambios (AutoCommit). 
        // Esto permite que las dos inserciones se traten como un solo paquete.
        con.setAutoCommit(false); 
        
        // Variable para capturar el ID que la base de datos le asigne a la nueva tarea.
        int idGenerado = 0;
        
        // Prepara la primera inserción (nombre de la tarea) y solicita que devuelva las llaves generadas.
        try (PreparedStatement psT = con.prepareStatement(sqlTarea, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            
            // Asigna el nombre de la tarea (ej: "Reparación de cerca") al primer signo '?'.
            psT.setString(1, nombreTarea);
            
            // Ejecuta la inserción en la tabla 'tareas'.
            psT.executeUpdate();
            
            // Recupera el ID autoincremental generado por el motor de base de datos (MySQL/PostgreSQL).
            ResultSet rs = psT.getGeneratedKeys();
            
            // Si la base de datos generó el ID correctamente, lo guarda en 'idGenerado'.
            if (rs.next()) idGenerado = rs.getInt(1);
        }
        
        // Prepara la segunda inserción (la asignación del trabajador al cultivo).
        try (PreparedStatement psA = con.prepareStatement(sqlAsignacion)) {
            
            // Remplaza los marcadores '?' con la información de la tarea manual:
            psA.setInt(1, idCultivo);      // El cultivo asociado.
            psA.setInt(2, idGenerado);     // El ID que acabamos de generar en el bloque anterior.
            psA.setInt(3, idTrabajador);   // El empleado que realizará la labor.
            psA.setString(4, descripcion); // El detalle de lo que debe hacer.
            psA.setString(5, jornada);     // El horario o turno.
            
            // Ejecuta la inserción en la tabla 'usuario_tarea'.
            psA.executeUpdate();
        }
        
        // Finaliza la transacción guardando ambos cambios de forma permanente en el disco.
        con.commit(); 
        
        // Retorna true indicando que el proceso fue exitoso.
        return true;
        
    } catch (SQLException e) { 
        // Si ocurre un error en cualquier punto, imprime el error y retorna false.
        // Nota: Al cerrar la conexión tras un error sin commit, la BD suele hacer rollback automático.
        e.printStackTrace(); 
        return false; 
    }
}

    // 6. MÉTODO PARA LISTAR EN LA TABLA PRINCIPAL (CORREGIDO: OCULTA PAGADAS)
// Define un método público que devuelve una lista de objetos 'Tarea' para mostrar en la interfaz principal.
public List<Tarea> listarTareas() {
    
    // Crea una lista de tipo ArrayList donde guardaremos cada fila de la consulta convertida en objeto.
    List<Tarea> lista = new ArrayList<>();
    
    // Definición de la consulta SQL con múltiples JOINs (Cruces de tablas):
    // - SELECT: Elegimos el ID de la asignación y los nombres descriptivos de las tablas relacionadas.
    // - FROM usuario_tarea ut: Nuestra tabla base es la que une usuarios con sus tareas.
    // - JOIN cultivos c: Unimos con la tabla cultivos para obtener el nombre del lote.
    // - JOIN tareas t: Unimos con la tabla tareas para obtener el nombre de la actividad (ej: Riego).
    // - JOIN usuarios u: Unimos con la tabla usuarios para saber quién es el trabajador.
    // - WHERE: Filtramos para que NO se muestren las tareas cuyo estado sea 'PAGADO'.
    // - ORDER BY: Ordenamos por ID de forma descendente (DESC) para ver lo último creado arriba.
    String sql = "SELECT ut.id, c.nombre AS nombre_cultivo, t.nombre AS nombre_tarea, " +
                 "u.nombre AS nombre_trabajador, ut.descripcion_actividad AS descripcion, " +
                 "ut.jornada, ut.estado " +
                 "FROM usuario_tarea ut " +
                 "JOIN cultivos c ON ut.cultivo_id = c.id " +
                 "JOIN tareas t ON ut.tarea_id = t.id " +
                 "JOIN usuarios u ON ut.usuario_id = u.id " +
                 "WHERE UPPER(TRIM(ut.estado)) != 'PAGADO' " + 
                 "ORDER BY ut.id DESC"; 

    // Bloque try-with-resources para asegurar el cierre automático de la conexión y los statements.
    try (
         // Crea la conexión llamando a la clase Conexion.
         Connection con = new Conexion().getConexion();
        
         // Prepara la consulta SQL para ser ejecutada.
         PreparedStatement ps = con.prepareStatement(sql);
        
         // Ejecuta la consulta y recibe el conjunto de resultados en 'rs'.
         ResultSet rs = ps.executeQuery()
        ) {
        
        // Inicia el bucle para procesar cada fila devuelta por la base de datos.
        while (rs.next()) {
            
            // Instancia un nuevo objeto Tarea para "mapear" los datos de la fila actual.
            Tarea t = new Tarea();
            
            // Setea el ID único de la asignación (desde usuario_tarea).
            t.setId(rs.getInt("id"));
            
            // Setea el nombre del cultivo (gracias al JOIN con la tabla cultivos).
            t.setNombreCultivo(rs.getString("nombre_cultivo"));
            
            // Setea el nombre de la labor (gracias al JOIN con la tabla tareas).
            t.setNombreTarea(rs.getString("nombre_tarea"));
            
            // Setea el nombre de la persona (gracias al JOIN con la tabla usuarios).
            t.setNombreTrabajador(rs.getString("nombre_trabajador"));
            
            // Setea la descripción adicional que escribió el supervisor.
            t.setDescripcion(rs.getString("descripcion"));
            
            // Setea la jornada asignada (Mañana/Tarde).
            t.setJornada(rs.getString("jornada"));
            
            // Setea el estado actual (Pendiente, En Proceso, Completada).
            t.setEstado(rs.getString("estado"));
            
            // Agrega el objeto totalmente configurado a la lista.
            lista.add(t);
        }
        
    } catch (SQLException e) { 
        // En caso de error en el SQL o conexión, imprime la traza del error.
        e.printStackTrace(); 
    }
    
    // Retorna la lista con todas las tareas activas para ser mostradas en AgritrackPlus.
    return lista;
}

   // 7. MÉTODO PARA LISTAR TAREAS DE UN TRABAJADOR ESPECÍFICO (CORREGIDO: OCULTA PAGADAS)
// Define un método que recibe el 'idTrabajador' y retorna solo sus tareas personales.
public List<Tarea> listarTareasPorTrabajador(int idTrabajador) {
    
    // Crea la lista donde se guardarán las tareas asignadas a ese trabajador en particular.
    List<Tarea> lista = new ArrayList<>();
    
    // Define la consulta SQL con JOINs para obtener nombres descriptivos:
    // 1. Une 'usuario_tarea' con 'cultivos' y 'tareas'.
    // 2. Filtra por el ID del trabajador (usuario_id = ?).
    // 3. Excluye las tareas con estado 'PAGADO' (usando UPPER y TRIM para mayor seguridad en el texto).
    // 4. Ordena de la más reciente a la más antigua.
    String sql = "SELECT ut.id, c.nombre AS nombre_cultivo, t.nombre AS nombre_tarea, " +
                 "ut.descripcion_actividad AS descripcion, ut.jornada, ut.estado " +
                 "FROM usuario_tarea ut " +
                 "JOIN cultivos c ON ut.cultivo_id = c.id " +
                 "JOIN tareas t ON ut.tarea_id = t.id " +
                 "WHERE ut.usuario_id = ? AND UPPER(TRIM(ut.estado)) != 'PAGADO' " +
                 "ORDER BY ut.id DESC";
    
    // Inicia el bloque try-with-resources para la conexión y la preparación de la sentencia.
    try (
         // Obtiene la conexión a través de tu clase Conexion.
         Connection con = new Conexion().getConexion();
        
         // Prepara la consulta SQL para inyectar parámetros de forma segura.
         PreparedStatement ps = con.prepareStatement(sql)
        ) {
        
        // Reemplaza el primer '?' de la consulta con el 'idTrabajador' que recibió el método.
        ps.setInt(1, idTrabajador);
        
        // Ejecuta la consulta y abre un ResultSet interno para procesar las filas devueltas.
        try (ResultSet rs = ps.executeQuery()) {
            
            // Mientras el cursor encuentre una fila de datos que pertenezca a este trabajador:
            while (rs.next()) {
                
                // Crea un objeto Tarea y le asigna los valores de la fila actual de la BD.
                Tarea t = new Tarea();
                
                // Extrae el ID de la asignación.
                t.setId(rs.getInt("id"));
                
                // Extrae el nombre del cultivo (gracias al JOIN).
                t.setNombreCultivo(rs.getString("nombre_cultivo"));
                
                // Extrae el nombre de la tarea (ej: "Poda").
                t.setNombreTarea(rs.getString("nombre_tarea"));
                
                // Extrae los detalles específicos escritos por el administrador.
                t.setDescripcion(rs.getString("descripcion"));
                
                // Extrae la jornada (Mañana/Tarde/Día completo).
                t.setJornada(rs.getString("jornada"));
                
                // Extrae el estado (ej: "Pendiente" o "En Proceso").
                t.setEstado(rs.getString("estado"));
                
                // Agrega la tarea configurada a la lista de resultados.
                lista.add(t);
            }
        }
        
    } catch (SQLException e) { 
        // Si ocurre un error de base de datos, lo imprime para poder depurarlo.
        e.printStackTrace(); 
    }
    
    // Retorna la lista filtrada solo con las tareas del trabajador solicitado.
    return lista;
}

   // 8. MÉTODO PARA ACTUALIZAR EL ESTADO DE UNA TAREA
// Recibe el ID de la asignación, el texto del nuevo estado y cualquier nota adicional (observación).
public boolean actualizarEstadoTarea(int idTarea, String nuevoEstado, String observacion) {
    
    // Define la consulta SQL de actualización (UPDATE).
    // Modificamos las columnas 'estado' y 'observacion' filtrando por el ID único de la tarea.
    String sql = "UPDATE usuario_tarea SET estado = ?, observacion = ? WHERE id = ?";
    
    // Bloque try-with-resources: Abre la conexión y prepara la sentencia SQL de forma automática.
    try (
         // 1. Obtiene la conexión desde tu clase de configuración de base de datos.
         Connection con = new Conexion().getConexion();
        
         // 2. Prepara el comando SQL para inyectar los datos de forma segura.
         PreparedStatement ps = con.prepareStatement(sql)
        ) {
        
        // Setea el primer '?' con el nuevo estado (ej: "Completado", "En proceso").
        ps.setString(1, nuevoEstado);
        
        // Setea el segundo '?' con la observación (ej: "Se terminó el fertilizante").
        ps.setString(2, observacion);
        
        // Setea el tercer '?' (el WHERE) con el ID de la tarea que queremos modificar.
        ps.setInt(3, idTarea);
        
        // Ejecuta la actualización y verifica si tuvo éxito.
        // ps.executeUpdate() devuelve el número de filas afectadas. 
        // Si es mayor a 0, significa que la tarea existía y se actualizó correctamente.
        return ps.executeUpdate() > 0;
        
    } catch (SQLException e) { 
        // Si ocurre un error (ej: conexión perdida), imprime el error en consola.
        e.printStackTrace(); 
        
        // Retorna false indicando que no se pudo realizar el cambio.
        return false; 
    }
}

   // 9. MÉTODO PARA LISTAR CULTIVOS POR SUPERVISOR
// Este método devuelve la lista de cultivos que un supervisor específico tiene a su cargo.
public List<Tarea> listarCultivosPorSupervisor(int idSupervisor) {
    
    // Crea una lista vacía para almacenar los objetos 'Tarea' (que aquí actúan como contenedores del cultivo).
    List<Tarea> lista = new ArrayList<>();
    
    // Consulta SQL avanzada:
    // 1. SELECT c.id, c.nombre...: Selecciona los datos básicos del cultivo.
    // 2. GROUP_CONCAT(u.nombre SEPARATOR ', '): Esta es la "magia". Toma todos los nombres de los 
    //    trabajadores vinculados y los une en una sola cadena de texto separada por comas.
    // 3. LEFT JOIN: Se usa para que, si un cultivo no tiene trabajadores asignados aún, el cultivo 
    //    aparezca de todos modos (no lo ignora).
    // 4. GROUP BY c.id: Es obligatorio al usar GROUP_CONCAT para agrupar los resultados por cada cultivo.
    String sql = "SELECT c.id, c.nombre AS cultivo, c.estado, c.ciclo, " +
                 "GROUP_CONCAT(u.nombre SEPARATOR ', ') AS trabajadores " +
                 "FROM cultivos c " +
                 "LEFT JOIN cultivo_trabajador ct ON c.id = ct.cultivo_id " +
                 "LEFT JOIN usuarios u ON ct.usuario_id = u.id " +
                 "WHERE c.supervisor_id = ? " +
                 "GROUP BY c.id";

    // Intenta establecer conexión y preparar la sentencia SQL.
    try (Connection con = Conexion.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        // Asigna el ID del supervisor al parámetro '?' para filtrar solo sus cultivos.
        ps.setInt(1, idSupervisor);
        
        // Ejecuta la consulta y abre el ResultSet para leer los datos.
        try (ResultSet rs = ps.executeQuery()) {
            
            // Recorre cada fila devuelta (cada fila representa un cultivo único).
            while (rs.next()) {
                
                // Crea una nueva instancia del objeto Tarea para mapear la información.
                Tarea t = new Tarea();
                
                // Guarda el ID del cultivo.
                t.setCultivoId(rs.getInt("id"));
                
                // Guarda el nombre del cultivo (ej: "Lote Norte - Café").
                t.setNombreCultivo(rs.getString("cultivo"));
                
                // Guarda el estado (ej: "Activo", "En Cosecha").
                t.setEstado(rs.getString("estado"));
                
                // Aquí usas el atributo 'jornada' para guardar el 'ciclo' del cultivo (ej: "Crecimiento").
                t.setJornada(rs.getString("ciclo"));
                
                // Guarda la lista de nombres de trabajadores que el GROUP_CONCAT unió (ej: "Juan, Pedro, Maria").
                t.setNombreTrabajador(rs.getString("trabajadores"));
                
                // Agrega el cultivo configurado a la lista de resultados.
                lista.add(t);
            }
        }
        
    } catch (SQLException e) {
        // En caso de error en la base de datos, imprime el rastro de la excepción.
        e.printStackTrace();
    }
    
    // Retorna la lista con los cultivos y sus respectivos equipos de trabajo.
    return lista;
}

    // 10. OBTENER CONTEOS POR ESTADO (CORREGIDO: NO CUENTA PAGADAS)
// Recibe el ID del usuario y devuelve un Mapa donde la clave es el nombre del estado (String) 
// y el valor es la cantidad de tareas en ese estado (Integer).
public Map<String, Integer> obtenerConteosPorEstado(int idUsuario) {
    
    // Crea una instancia de HashMap para almacenar los resultados.
    Map<String, Integer> conteos = new HashMap<>();
    
    // Inicializa el mapa con valores en 0. Esto asegura que si no hay tareas de un tipo, 
    // el sistema muestre "0" en lugar de un error o un valor nulo.
    conteos.put("Pendiente", 0);
    conteos.put("En Proceso", 0);
    conteos.put("Completada", 0);

    // Consulta SQL con funciones de agregación:
    // 1. TRIM(estado): Limpia espacios en blanco alrededor del texto del estado.
    // 2. COUNT(*): Cuenta cuántas filas existen para cada grupo.
    // 3. WHERE ... != 'PAGADO': Filtra para ignorar las tareas que ya están cerradas financieramente.
    // 4. GROUP BY estado: Agrupa los resultados para obtener un total por cada categoría.
    String sql = "SELECT TRIM(estado) as estado_limpio, COUNT(*) as total " +
                 "FROM usuario_tarea WHERE usuario_id = ? AND UPPER(TRIM(estado)) != 'PAGADO' GROUP BY estado";

    // Bloque try-with-resources para gestionar la conexión y la sentencia.
    try (Connection con = new Conexion().getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        // Asigna el ID del usuario al marcador '?' para que el conteo sea personal.
        ps.setInt(1, idUsuario);
        
        // Ejecuta la consulta y abre el ResultSet para procesar los grupos encontrados.
        try (ResultSet rs = ps.executeQuery()) {
            
            // Mientras la base de datos devuelva grupos (ej: un grupo para 'Pendiente', otro para 'Completada').
            while (rs.next()) {
                
                // Obtiene el nombre del estado limpio desde la base de datos.
                String estadoDB = rs.getString("estado_limpio");
                
                // Valida que el estado no sea nulo antes de compararlo.
                if (estadoDB != null) {
                    
                    // Compara el nombre del estado (ignorando mayúsculas/minúsculas).
                    // Si coincide, actualiza el valor en el Mapa con el número total devuelto por COUNT(*).
                    
                    if (estadoDB.equalsIgnoreCase("Pendiente")) 
                        conteos.put("Pendiente", rs.getInt("total"));
                        
                    if (estadoDB.equalsIgnoreCase("En Proceso")) 
                        conteos.put("En Proceso", rs.getInt("total"));
                        
                    if (estadoDB.equalsIgnoreCase("Completada")) 
                        conteos.put("Completada", rs.getInt("total"));
                }
            }
        }
        
    } catch (SQLException e) { 
        // Imprime el error en caso de fallo en la consulta.
        e.printStackTrace(); 
    }
    
    // Retorna el mapa con los números finales (ej: {Pendiente=5, En Proceso=2, Completada=10}).
    return conteos;
}

    // 11. CÁLCULO DE CUMPLIMIENTO (PAGADAS CUENTAN COMO ÉXITO)
// Retorna un Mapa donde la clave es el nombre del cultivo y el valor es su porcentaje de avance (0-100).
public Map<String, Integer> obtenerCumplimientoCultivos(int idUsuario) {
    
    // Crea un HashMap para almacenar el par {Nombre del Cultivo : Porcentaje de éxito}.
    Map<String, Integer> cumplimiento = new HashMap<>();
    
    // Consulta SQL de alta complejidad:
    // 1. SELECT c.nombre: Trae el nombre del cultivo para identificar el porcentaje.
    // 2. CASE WHEN ... IN ('COMPLETADA', 'PAGADO') THEN 1 ELSE 0 END: Crea una columna temporal de 1s y 0s. 
    //    Si la tarea está terminada o pagada vale 1, si está pendiente o en proceso vale 0.
    // 3. (SUM(...) * 100) / COUNT(ut.id): Es la fórmula del porcentaje: (Tareas Finalizadas * 100) / Total de Tareas.
    // 4. ROUND(...) e IFNULL(..., 0): Redondea el resultado y, si no hay tareas (división por cero), devuelve 0.
    // 5. JOIN cultivos c: Une con la tabla cultivos para poder mostrar el nombre real del lote.
    // 6. GROUP BY c.nombre: Agrupa las tareas por cada cultivo para calcular el porcentaje de cada uno por separado.
    String sql = "SELECT c.nombre, " +
                 "IFNULL(ROUND((SUM(CASE WHEN UPPER(TRIM(ut.estado)) IN ('COMPLETADA', 'PAGADO') THEN 1 ELSE 0 END) * 100) / COUNT(ut.id)), 0) as porcentaje " +
                 "FROM usuario_tarea ut " +
                 "JOIN cultivos c ON ut.cultivo_id = c.id " +
                 "WHERE ut.usuario_id = ? " +
                 "GROUP BY c.nombre";

    // Bloque try-with-resources para manejar la conexión y la preparación del SQL.
    try (Connection con = new Conexion().getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        // Inyecta el ID del usuario en el '?' para filtrar solo sus cultivos y tareas asignadas.
        ps.setInt(1, idUsuario);
        
        // Ejecuta la consulta y abre el ResultSet para leer los resultados.
        try (ResultSet rs = ps.executeQuery()) {
            
            // Recorre cada fila devuelta (una fila por cada cultivo que tenga el usuario).
            while (rs.next()) {
                
                // Extrae el nombre del cultivo y su porcentaje calculado.
                // Ejemplo: rs.getString("nombre") -> "Lote Café", rs.getInt("porcentaje") -> 75.
                // Guarda estos datos en el mapa de cumplimiento.
                cumplimiento.put(rs.getString("nombre"), rs.getInt("porcentaje"));
            }
        }
        
    } catch (SQLException e) { 
        // En caso de error técnico en la base de datos, imprime el rastro de la falla.
        e.printStackTrace(); 
    }
    
    // Retorna el mapa listo (ej: {"Cafetales": 80, "Maizal": 25}).
    return cumplimiento;
}

    // 12. RESUMEN DE PAGOS
// Define un método público que devuelve un Mapa con el resumen de dinero (Clave: String, Valor: Decimal).
public Map<String, Double> obtenerResumenPagos(int idUsuario) {
    
    // Crea una instancia de HashMap para almacenar los totales financieros.
    Map<String, Double> resumen = new HashMap<>();
    
    // Inicializa los valores en 0.0 para evitar errores de puntero nulo (NullPointerException) 
    // en la interfaz si el usuario aún no tiene registros de pago.
    resumen.put("Pagado", 0.0);
    resumen.put("Pendiente", 0.0);

    // Consulta SQL de agregación:
    // 1. SUM(pago): Suma todos los valores de la columna 'pago'.
    // 2. WHERE usuario_id = ?: Filtra para que solo sume el dinero del trabajador actual.
    // 3. (UPPER(TRIM(estado)) IN ('ACTIVO', 'PAGADO')): Asegura que solo se sumen registros 
    //    que estén vigentes o ya liquidados, ignorando posibles pagos anulados.
    String sql = "SELECT SUM(pago) as total FROM pagos WHERE usuario_id = ? AND (UPPER(TRIM(estado)) IN ('ACTIVO', 'PAGADO'))";

    // Bloque try-with-resources: Establece la conexión y prepara la sentencia SQL.
    try (Connection con = new Conexion().getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        // Inyecta el ID del usuario en el parámetro '?' de la consulta.
        ps.setInt(1, idUsuario);
        
        // Ejecuta la consulta y abre el ResultSet para leer el resultado de la suma.
        try (ResultSet rs = ps.executeQuery()) {
            
            // Si la base de datos devuelve un resultado (incluso si la suma es 0).
            if (rs.next()) {
                
                // Extrae el valor de la suma total y lo guarda en el Mapa bajo la clave "Pagado".
                // rs.getDouble("total") obtiene el resultado del alias definido en el SQL.
                resumen.put("Pagado", rs.getDouble("total"));
            }
        }
        
    } catch (SQLException e) { 
        // Si ocurre un error en la comunicación con la BD, imprime la traza del error.
        e.printStackTrace(); 
    }
    
    // Retorna el mapa con el total acumulado (ej: {"Pagado": 150000.0, "Pendiente": 0.0}).
    return resumen;
}
    // GRÁFICO 1: Tareas creadas por día de la semana actual
// Define un método que retorna un Mapa ordenado (LinkedHashMap) con los días y sus totales.
public Map<String, Integer> contarTareasPorDiaSemana(int idSupervisor) {
    
    // Usa LinkedHashMap en lugar de HashMap para mantener el orden de inserción (Lun, Mar, Mie...).
    Map<String, Integer> datos = new LinkedHashMap<>();
    
    // Inicializa todos los días en 0. Esto garantiza que la gráfica no se vea "vacía" 
    // si un martes o miércoles no se crearon tareas.
    datos.put("Lun", 0); datos.put("Mar", 0); datos.put("Mie", 0);
    datos.put("Jue", 0); datos.put("Vie", 0); datos.put("Sab", 0);

    // Consulta SQL con funciones de tiempo:
    // 1. DAYNAME(fecha_creacion): Obtiene el nombre del día en inglés (ej: 'Monday').
    // 2. WEEK(fecha_creacion) = WEEK(CURDATE()): Filtra para que solo cuente las tareas de la SEMANA ACTUAL.
    // 3. JOIN cultivos: Necesario para filtrar por el supervisor responsable.
    // 4. GROUP BY DAYNAME: Agrupa los conteos por cada día de la semana.
    String sql = "SELECT DAYNAME(fecha_creacion) AS dia, COUNT(*) AS total " +
                 "FROM usuario_tarea ut " +
                 "JOIN cultivos c ON ut.cultivo_id = c.id " +
                 "WHERE c.supervisor_id = ? " +
                 "AND WEEK(fecha_creacion) = WEEK(CURDATE()) " +
                 "GROUP BY DAYNAME(fecha_creacion)";

    // Bloque try-with-resources para manejar la conexión y la sentencia.
    try (Connection con = Conexion.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        // Asigna el ID del supervisor al parámetro '?' para filtrar sus datos.
        ps.setInt(1, idSupervisor);
        
        // Ejecuta la consulta y abre el ResultSet.
        try (ResultSet rs = ps.executeQuery()) {
            
            // Recorre los resultados (máximo 6 o 7 filas, una por cada día con datos).
            while (rs.next()) {
                String dia = rs.getString("dia"); // El nombre del día que viene de la BD (en inglés).
                int total = rs.getInt("total");   // La cantidad de tareas para ese día.

                // TRADUCCIÓN Y MAPEADO: Como la base de datos devuelve nombres en inglés ('Monday'),
                // usamos condicionales para asignar el 'total' a nuestras claves en español.
                if (dia.startsWith("Mon"))      datos.put("Lun", total);
                else if (dia.startsWith("Tue")) datos.put("Mar", total);
                else if (dia.startsWith("Wed")) datos.put("Mie", total);
                else if (dia.startsWith("Thu")) datos.put("Jue", total);
                else if (dia.startsWith("Fri")) datos.put("Vie", total);
                else if (dia.startsWith("Sat")) datos.put("Sab", total);
            }
        }
    } catch (SQLException e) { 
        // Captura e imprime errores de SQL (ej: columna fecha_creacion no existe).
        e.printStackTrace(); 
    }
    
    // Retorna el mapa listo para que el Front-end lo convierta en una gráfica.
    return datos;
}
    // GRÁFICO 2: % de tareas completadas por cultivo del supervisor
// Retorna un Mapa con el nombre del cultivo y su porcentaje de avance global.
public Map<String, Integer> cumplimientoPorCultivo(int idSupervisor) {
    
    // Usa LinkedHashMap para asegurar que el orden en que los cultivos salen de la BD 
    // sea el mismo en que se muestren en la gráfica del Dashboard.
    Map<String, Integer> datos = new LinkedHashMap<>();
    
    // Consulta SQL con lógica matemática:
    // 1. SELECT c.nombre: Nombre del lote o cultivo.
    // 2. CASE WHEN ut.estado = 'Completada' THEN 1 ELSE 0 END: Si la tarea está lista vale 1, si no, vale 0.
    // 3. (SUM(...) * 100.0) / COUNT(*): Suma los "1s" (completadas), multiplica por 100 y divide entre el total de tareas del cultivo.
    // 4. ROUND(...): Redondea el resultado para no tener decimales en la gráfica.
    // 5. JOIN cultivos c: Cruza con la tabla cultivos para filtrar por el supervisor responsable.
    // 6. GROUP BY c.id: Agrupa todas las tareas que pertenecen al mismo cultivo.
    String sql = "SELECT c.nombre, " +
                 "ROUND((SUM(CASE WHEN ut.estado = 'Completada' THEN 1 ELSE 0 END) * 100.0) / COUNT(*)) AS porcentaje " +
                 "FROM usuario_tarea ut " +
                 "JOIN cultivos c ON ut.cultivo_id = c.id " +
                 "WHERE c.supervisor_id = ? " +
                 "GROUP BY c.id, c.nombre";

    // Bloque try-with-resources: Abre la conexión y prepara la ejecución del SQL.
    try (Connection con = Conexion.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        // Inyecta el ID del supervisor en el '?' para que solo vea sus propios cultivos a cargo.
        ps.setInt(1, idSupervisor);
        
        // Ejecuta la consulta y procesa el set de resultados (ResultSet).
        try (ResultSet rs = ps.executeQuery()) {
            
            // Recorre cada fila devuelta (una fila por cada cultivo del supervisor).
            while (rs.next()) {
                
                // Extrae el nombre del cultivo (String) y el porcentaje calculado (Integer).
                // Ejemplo: "Lote Café A" -> 85%
                // Lo guarda en el mapa 'datos'.
                datos.put(rs.getString("nombre"), rs.getInt("porcentaje"));
            }
        }
        
    } catch (SQLException e) { 
        // Si hay un error (ej: error de sintaxis en el SQL), imprime el error en consola.
        e.printStackTrace(); 
    }
    
    // Retorna el mapa listo para ser procesado por una librería de gráficas como Chart.js.
    return datos;
}

    // GRÁFICO 3: Distribución de tareas por estado (donut)
// Define un método público que retorna un Mapa con el conteo de tareas según su situación.
public Map<String, Integer> rendimientoSemanal(int idSupervisor) {
    
    // Usa LinkedHashMap para asegurar que las categorías aparezcan siempre en el mismo orden 
    // en la leyenda de la gráfica (Completada, Luego En Proceso, etc.).
    Map<String, Integer> datos = new LinkedHashMap<>();
    
    // Inicializa las categorías con 0. Esto es una buena práctica para que la gráfica 
    // no de error si, por ejemplo, aún no hay ninguna tarea "En Proceso".
    datos.put("Completada", 0);
    datos.put("En Proceso", 0);
    datos.put("Pendiente", 0);

    // Consulta SQL de agrupación:
    // 1. SELECT ut.estado, COUNT(*): Selecciona el nombre del estado y cuenta cuántas tareas hay en cada uno.
    // 2. JOIN cultivos c: Une con la tabla de cultivos para poder filtrar por el supervisor.
    // 3. WHERE c.supervisor_id = ?: Filtra para obtener solo la información que le compete a este supervisor.
    // 4. GROUP BY ut.estado: Agrupa los resultados para que el conteo se haga por cada tipo de estado.
    String sql = "SELECT ut.estado, COUNT(*) AS total " +
                 "FROM usuario_tarea ut " +
                 "JOIN cultivos c ON ut.cultivo_id = c.id " +
                 "WHERE c.supervisor_id = ? " +
                 "GROUP BY ut.estado";

    // Bloque try-with-resources: Abre la conexión y prepara la sentencia SQL de forma automática.
    try (Connection con = Conexion.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        // Asigna el ID del supervisor al parámetro '?' para que el resultado sea personalizado.
        ps.setInt(1, idSupervisor);
        
        // Ejecuta la consulta y abre el ResultSet para leer los datos devueltos por la BD.
        try (ResultSet rs = ps.executeQuery()) {
            
            // Recorre cada fila del resultado (máximo 3 filas: una por cada estado).
            while (rs.next()) {
                
                // Sobrescribe los ceros iniciales en el Mapa con los valores reales encontrados.
                // rs.getString("estado") obtiene el nombre y rs.getInt("total") el número de tareas.
                datos.put(rs.getString("estado"), rs.getInt("total"));
            }
        }
        
    } catch (SQLException e) { 
        // Si ocurre un error de base de datos, imprime el error en la consola.
        e.printStackTrace(); 
    }
    
    // Retorna el mapa con la distribución final de las tareas.
    return datos;
}
    // MÉTODO PARA BUSCAR TAREAS FILTRANDO POR EL SUPERVISOR RESPONSABLE
// Recibe un 'criterio' (String) que puede ser el nombre o el documento del supervisor.
public List<Map<String, String>> buscarTareasPorSupervisor(String criterio) {
    
    // Crea una lista de Mapas. Cada Mapa representará una fila con claves "cultivo", "tarea" y "estado".
    List<Map<String, String>> lista = new ArrayList<>();
    
    // Consulta SQL con múltiples JOINs y filtros complejos:
    // 1. Une 'usuario_tarea' con 'cultivos' y 'tareas' para obtener los nombres reales.
    // 2. Une con 'usuarios' (u) a través del 'supervisor_id' del cultivo.
    // 3. WHERE: Busca si el nombre del supervisor contiene el criterio (LIKE) O si el documento es idéntico.
    // 4. Filtra solo tareas en estado 'Completada' o 'En Proceso' (ignora las pendientes o pagadas).
    String sql = "SELECT c.nombre AS cultivo, t.nombre AS tarea, ut.estado " +
                 "FROM usuario_tarea ut " +
                 "JOIN cultivos c ON ut.cultivo_id = c.id " +
                 "JOIN tareas t ON ut.tarea_id = t.id " +
                 "JOIN usuarios u ON c.supervisor_id = u.id " +
                 "WHERE (u.nombre LIKE ? OR u.documento = ?) " +
                 "AND ut.estado IN ('Completada', 'En Proceso') " +
                 "ORDER BY c.nombre, t.nombre";

    // Bloque try-with-resources: Abre la conexión y prepara la sentencia SQL.
    try (Connection con = Conexion.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        // Configura el primer '?' para el nombre. El "%" permite buscar coincidencias parciales.
        // Ejemplo: Si buscas "Day", encontrará "Dayana".
        ps.setString(1, "%" + criterio + "%");
        
        // Configura el segundo '?' para el documento. Aquí la búsqueda es exacta.
        ps.setString(2, criterio);
        
        // Ejecuta la consulta y procesa los resultados.
        try (ResultSet rs = ps.executeQuery()) {
            
            // Recorre cada registro encontrado.
            while (rs.next()) {
                
                // Crea un mapa para guardar los datos de la fila actual de forma dinámica.
                Map<String, String> m = new HashMap<>();
                
                // Extrae los datos usando los alias definidos en el SELECT y los guarda en el mapa.
                m.put("cultivo", rs.getString("cultivo"));
                m.put("tarea",   rs.getString("tarea"));
                m.put("estado",  rs.getString("estado"));
                
                // Agrega el mapa a la lista final.
                lista.add(m);
            }
        }
    } catch (SQLException e) { 
        // Imprime el error si algo falla en la conexión o sintaxis SQL.
        e.printStackTrace(); 
    }
    
    // Retorna la lista de tareas filtradas por el supervisor buscado.
    return lista;
}
}