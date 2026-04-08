package com.agritrack.agritrackplus.DAO; // Declara que esta clase pertenece al paquete DAO (Data Access Object) del proyecto AgriTrackPlus. Los paquetes organizan el código como carpetas lógicas.

import com.agritrack.agritrackplus.db.Conexion; // Importa la clase 'Conexion' del paquete 'db' del mismo proyecto. Esta clase contiene el método estático getConnection() que abre el túnel hacia la base de datos MySQL.
import com.agritrack.agritrackplus.modelo.Usuario; // Importa el modelo 'Usuario', que es un POJO (clase simple) con los campos id, nombre, documento, etc. Se usa para transportar datos de un usuario entre capas de la aplicación.
import java.sql.*; // Importa TODAS las clases del paquete java.sql: Connection, PreparedStatement, ResultSet, SQLException, Statement, etc. El asterisco (*) evita tener que importarlas una por una.
import java.util.ArrayList; // Importa ArrayList, una implementación dinámica de lista que permite almacenar múltiples objetos y agregar/eliminar elementos fácilmente.
import java.util.HashMap; // Importa HashMap, una implementación de Map que almacena pares clave-valor (por ejemplo: "nombre" -> "Juan"). No garantiza orden de inserción.
import java.util.List; // Importa la interfaz List, que define el contrato que debe cumplir cualquier lista (ArrayList, LinkedList, etc.).
import java.util.Map; // Importa la interfaz Map, que define el contrato para cualquier estructura clave-valor (HashMap, TreeMap, etc.).

/*
Maneja la autenticación con hash MD5, el registro completo de usuarios distribuyendo datos en varias tablas relacionadas (usuarios, correo, teléfono, roles, fotos), 
y usa transacciones para garantizar consistencia. Permite listar, editar, activar/desactivar y eliminar usuarios, incluyendo validaciones previas de correo y documento duplicados.
También provee métodos de consulta específicos por rol: el administrador ve todo, el supervisor solo su área. Incluye utilidades como conteo de usuarios, resumen de tareas por 
estado y cálculo de progreso por cultivo. Los recursos JDBC siempre se cierran al finalizar, ya sea con try-with-resources o con un método cerrar() en bloques finally.
*/
public class UsuarioDAO { // Declara la clase pública 'UsuarioDAO'. El patrón DAO (Data Access Object) 
    //separa la lógica de acceso a la base de datos del resto de la aplicación (Servlets, modelos, etc.).

    // ==================================================================================
    // MÉTODO PRIVADO: encriptarMD5
    // Recibe: una contraseña en texto plano (String pass)
    // Devuelve: la misma contraseña convertida en un hash MD5 de 32 caracteres hexadecimales
    // Dirección del dato: el String 'pass' entra desde cualquier método interno → sale como hash hacia la BD
    // ==================================================================================
    private String encriptarMD5(String pass) { // Declara el método como 'private' (solo accesible dentro de esta clase).
        //Recibe la contraseña sin cifrar y retorna un String con el hash.
        try { // Inicia un bloque de manejo de excepciones porque los métodos criptográficos pueden lanzar 'NoSuchAlgorithmException'.
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5"); // Solicita al sistema una instancia del motor de hashing MD5. 'MessageDigest' es la clase estándar de Java para generar resúmenes criptográficos. Si el sistema no soporta MD5, lanza una excepción.

            byte[] array = md.digest(pass.getBytes()); // Convierte el String 'pass' en un arreglo de bytes 
            //usando la codificación por defecto del sistema (generalmente UTF-8), y luego aplica el algoritmo MD5.
            //El resultado es un arreglo de 16 bytes (128 bits) que representa el hash.

            StringBuilder sb = new StringBuilder(); // Crea un StringBuilder vacío. Es más eficiente que concatenar Strings con '+' dentro de un bucle
            //porque no crea un objeto nuevo en cada iteración.

            for (byte b : array) { // Inicia un bucle 'for-each' que recorre uno a uno los 16 bytes del arreglo resultante del hash MD5.
                sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3)); // OPERACIÓN DE CONVERSIÓN POR BYTE:
                // 1. (b & 0xFF): Aplica una máscara de bits para convertir el byte con signo (rango -128 a 127) a un entero sin signo 
                //(rango 0 a 255), evitando que los bytes negativos produzcan caracteres hexadecimales incorrectos.
                // 2. | 0x100: Fuerza que el resultado siempre tenga 3 dígitos en hexadecimal (ej: 0x105), garantizando que el substring 
                //resultante siempre tenga exactamente 2 caracteres.
                // 3. .substring(1, 3): Elimina el primer carácter ('1') dejando solo los 2 dígitos hexadecimales del byte original (ej: "05").
                // 4. sb.append(...): Agrega esos 2 caracteres al StringBuilder. Al final del bucle, sb tiene 32 caracteres hexadecimales.
            }

            return sb.toString(); // Convierte el StringBuilder a un String normal de 32 caracteres y lo retorna. 
            //Este es el hash MD5 final (ej: "5f4dcc3b5aa765d61d8327deb882cf99").

        } catch (Exception e) { // Captura cualquier excepción que ocurra durante la generación del hash (principalmente 'NoSuchAlgorithmException').
            return pass; // Si el hashing falla, retorna la contraseña original sin cifrar. ADVERTENCIA: 
            //en producción esto es un riesgo de seguridad porque guardaría la clave en texto plano.
        }
    }

    // ==================================================================================
    // MÉTODO PRIVADO: cerrar
    // Recibe: los tres recursos JDBC a liberar (ResultSet rs, PreparedStatement ps, Connection conn)
    // Devuelve: void (no retorna valor, solo libera recursos)
    // Dirección: recibe objetos abiertos desde cualquier método → los cierra y libera memoria del servidor
    // ==================================================================================
    private void cerrar(ResultSet rs, PreparedStatement ps, Connection conn) { // Método utilitario privado para cerrar 
        //los recursos JDBC en el orden correcto: primero el resultado, luego la sentencia, finalmente la conexión.
        try { // Bloque try-catch obligatorio porque el método .close() puede lanzar una 'SQLException' 
            //si la conexión ya fue cerrada o hubo un error de red.
            if (rs != null) rs.close(); // Verifica que el ResultSet (cursor de filas) no sea null antes de cerrarlo. 
            //Si fue null (la consulta falló antes de ejecutarse), omite este paso para evitar un NullPointerException.
            if (ps != null) ps.close(); // Verifica que el PreparedStatement (la sentencia preparada) no sea null y lo cierra. 
            //Libera los recursos que el motor de la BD reservó para compilar y ejecutar la consulta.
            if (conn != null) conn.close(); // Verifica que la Connection (el canal físico TCP/IP hacia la BD) no sea null y la cierra. 
            //Este es el paso más importante: si se usa un pool de conexiones, las devuelve al pool; si no, termina la sesión con el servidor MySQL.
        } catch (SQLException e) { // Captura específicamente errores de SQL/JDBC que ocurran durante el cierre de los recursos.
            e.printStackTrace(); // Imprime la traza completa del error en la consola del servidor. Útil para diagnóstico, aunque en producción se debería usar un logger (Log4j, SLF4J, etc.).
        }
    }

    // ==================================================================================
    // MÉTODO PÚBLICO: existeCorreo (versión para registro nuevo)
    // Recibe: el email a verificar (String correo)
    // Devuelve: true si el correo YA existe en la tabla 'correo', false si está disponible
    // Dirección: recibe el email desde el Servlet → consulta la BD → devuelve booleano al Servlet
    // ==================================================================================
    public boolean existeCorreo(String correo) { // Método público para verificar si un correo ya está registrado en la BD.
        //Lo usa el método 'crear' antes de insertar un usuario nuevo.
        Connection conn = null;      // Declara la conexión como null. Se hace fuera del try para que el bloque 'finally' 
        //pueda referenciarla y cerrarla aunque ocurra una excepción.
        PreparedStatement ps = null; // Declara la sentencia preparada como null por la misma razón que 'conn'.
        ResultSet rs = null;         // Declara el cursor de resultados como null por la misma razón que 'conn'.

        try { // Inicia el bloque de código que puede lanzar excepciones. Si alguna línea falla, el flujo salta directo al catch.
            conn = Conexion.getConnection(); // Llama al método estático 'getConnection()' de la clase 'Conexion' (del paquete db).
            //Este método abre (o recupera del pool) una conexión activa hacia la base de datos MySQL y la asigna a 'conn'.

            ps = conn.prepareStatement("SELECT id FROM correo WHERE email = ?"); // Prepara la consulta SQL en el servidor de BD. El '?' 
            //es un marcador de posición (parámetro) que se asignará después. Seleccionamos solo 'id' porque es el campo más ligero;
            //no necesitamos todos los datos, solo saber si existe algún registro.

            ps.setString(1, correo); // Asigna el valor del parámetro 'correo' al primer '?' de la consulta (índice 1, no 0). 
            //El driver JDBC se encarga de escapar caracteres especiales, previniendo inyección SQL.

            rs = ps.executeQuery(); // Envía la consulta SQL al servidor MySQL y recibe el resultado. 
            //El resultado queda almacenado en el objeto ResultSet 'rs', que funciona como un cursor sobre las filas devueltas.

            return rs.next(); // Intenta mover el cursor al primer registro. Si hay al menos una fila 
            //(el correo existe en la BD), rs.next() retorna true. Si la consulta no encontró ninguna coincidencia, retorna false. 
            //Este valor booleano va directo al método que llamó a existeCorreo().

        } catch (Exception e) { // Captura cualquier excepción que pueda ocurrir (SQLException, NullPointerException, etc.).
            e.printStackTrace(); // Imprime la traza del error en la consola para diagnóstico del desarrollador.
            return false; // Si hubo un error de conexión u otro problema, retorna false por seguridad (asume que el correo no existe,
            //aunque no se pudo verificar).

        } finally { // Bloque que se ejecuta SIEMPRE, ocurra o no una excepción, incluso si hay un 'return' en el try o catch.
            cerrar(rs, ps, conn); // Llama al método auxiliar 'cerrar' para liberar los tres recursos JDBC y devolver la conexión al pool, 
            //evitando fugas de memoria y agotamiento de conexiones.
        }
    }

    // ==================================================================================
    // MÉTODO PÚBLICO: existeTelefono
    // Recibe: el número de teléfono a verificar (String telefono)
    // Devuelve: true si el teléfono YA existe en la tabla 'usuarios', false si está disponible
    // Dirección: recibe el teléfono desde el Servlet/formulario → consulta la BD → devuelve booleano
    // ==================================================================================
    public boolean existeTelefono(String telefono) { // Método público que verifica si un número de teléfono ya está registrado antes de permitir 
        //un nuevo registro de usuario.
        boolean existe = false; // Variable resultado inicializada en false. Se asume que el teléfono NO existe hasta que la BD diga lo contrario.
        //Esto es "fail-safe".

        String sql = "SELECT COUNT(*) FROM usuarios WHERE telefono = ?"; // Define la consulta SQL. COUNT(*) le pide a MySQL que cuente cuántas filas 
        //coinciden con el teléfono dado. Retorna 0 si no hay nadie con ese número, o un número mayor a 0 si ya existe.

        try (Connection con = Conexion.getConnection(); // TRY-WITH-RESOURCES: abre la conexión aquí. Al finalizar el bloque try (con éxito o error), 
                //Java cierra automáticamente los recursos declarados entre paréntesis.
                //Esto garantiza el cierre sin necesidad de un bloque 'finally' explícito.
             PreparedStatement ps = con.prepareStatement(sql)) { // Prepara la sentencia SQL dentro del mismo try-with-resources.
            //Ambos recursos (con y ps) se cerrarán automáticamente al salir del bloque.

            ps.setString(1, telefono); // Inyecta el número de teléfono en el primer '?' de la consulta. El PreparedStatement escapa el valor para 
            //prevenir inyección SQL.

            try (ResultSet rs = ps.executeQuery()) { // Segundo TRY-WITH-RESOURCES anidado: ejecuta la consulta y abre el ResultSet. 
                //Este ResultSet también se cerrará automáticamente al salir de este bloque interno.
                if (rs.next()) { // Intenta mover el cursor a la primera (y única) fila que devuelve COUNT(*). Con COUNT, siempre habrá una fila,
                    //así que este if casi siempre será true.
                    existe = rs.getInt(1) > 0; // Lee el valor de la primera columna (el conteo numérico). Si COUNT(*) devolvió 0, la expresión '> 0' 
                    //es false y 'existe' permanece false. Si devolvió 1 o más, 'existe' se convierte en true.
                }
            }

        } catch (SQLException e) { // Captura errores específicos de JDBC: problemas de conexión, sintaxis SQL incorrecta, servidor caído, etc.
            System.out.println("Error al verificar teléfono: " + e.getMessage()); // Imprime solo el mensaje del error (no toda la traza) 
            //usando System.out en lugar de e.printStackTrace(). Útil para diagnóstico básico.
        }

        return existe; // Retorna el resultado final: true si el teléfono ya está registrado en la BD, false si está disponible.
    }

    // ==================================================================================
    // MÉTODO PRIVADO: existeCorreo (versión sobrecargada para edición)
    // Recibe: el email a verificar (String correo) y el ID del usuario que se está editando (int usuarioIdExcluir)
    // Devuelve: true si OTRO usuario diferente ya tiene ese correo, false si está libre o si el correo pertenece al mismo usuario
    // Dirección: lo llama 'editarUsuario' → consulta la BD → devuelve booleano a 'editarUsuario'
    // ==================================================================================
    private boolean existeCorreo(String correo, int usuarioIdExcluir) { // SOBRECARGA del método existeCorreo. Java permite tener dos métodos con el 
        //mismo nombre si difieren en los parámetros. Esta versión privada se usa al editar un usuario: excluye al propio usuario de la búsqueda para no 
        //generar un falso positivo cuando el correo no cambió.
        Connection conn = null; // Declara la conexión como null fuera del try para acceder a ella en el bloque 'finally'.
        PreparedStatement ps = null; // Declara la sentencia preparada como null por la misma razón.
        ResultSet rs = null; // Declara el cursor de resultados como null por la misma razón.

        try { // Bloque principal que puede lanzar excepciones de base de datos o de otra índole.
            conn = Conexion.getConnection(); // Obtiene una conexión activa desde la clase 'Conexion'. Este método puede lanzar una excepción si la BD no está disponible.

            ps = conn.prepareStatement("SELECT id FROM correo WHERE email = ? AND usuario_id != ?"); // Prepara una consulta con DOS condiciones:
            // 1. email = ?: busca el correo específico que se quiere validar.
            // 2. usuario_id != ?: excluye al usuario que se está editando (no se marca como duplicado si el correo ya le pertenece a él mismo).

            ps.setString(1, correo); // Asigna el correo a verificar al primer parámetro '?' de la consulta.

            ps.setInt(2, usuarioIdExcluir); // Asigna el ID del usuario que se está editando al segundo '?'. Esto hace que la consulta ignore los registros de ese usuario, evitando el falso positivo.

            rs = ps.executeQuery(); // Ejecuta la consulta en la BD y almacena el resultado en el cursor 'rs'.

            return rs.next(); // Si hay al menos una fila (otro usuario diferente tiene ese correo), retorna true. Si no hay filas, retorna false (el correo está disponible o solo lo tiene el usuario actual).

        } catch (Exception e) { // Captura cualquier excepción que ocurra durante la consulta.
            e.printStackTrace(); // Imprime la traza completa del error en la consola del servidor.
            return false; // Ante cualquier error técnico, retorna false (asume que no hay duplicado). Esto podría permitir un correo duplicado si la BD falla, pero evita bloquear la edición.

        } finally { // Se ejecuta siempre, independientemente de si hubo error o no.
            cerrar(rs, ps, conn); // Llama al método cerrar() para liberar los recursos JDBC y prevenir fugas de memoria.
        }
    }

    // ==================================================================================
    // MÉTODO PÚBLICO: existeDocumento
    // Recibe: el número de documento de identidad a verificar (String documento)
    // Devuelve: true si ese documento YA existe en la tabla 'usuarios', false si está disponible
    // Dirección: recibe el documento desde el Servlet → consulta la BD → devuelve booleano al Servlet
    // ==================================================================================
    public boolean existeDocumento(String documento) { // Método público que verifica si un número de documento ya está registrado. Lo llama el método 'crear' para evitar usuarios duplicados.
        Connection conn = null;      // Declara la conexión fuera del try para poder cerrarla en el bloque 'finally'.
        PreparedStatement ps = null; // Declara la sentencia preparada fuera del try por la misma razón.
        ResultSet rs = null;         // Declara el cursor de resultados fuera del try por la misma razón.

        try { // Inicia el bloque protegido donde se realizan las operaciones con la BD.
            conn = Conexion.getConnection(); // Obtiene la conexión activa desde el pool o crea una nueva. Si el servidor de BD está apagado, lanza una excepción aquí.

            ps = conn.prepareStatement("SELECT id FROM usuarios WHERE documento = ?"); // Prepara la sentencia SQL. Busca en la tabla 'usuarios' si existe algún registro con ese número de documento. Seleccionar solo 'id' es más eficiente que 'SELECT *' porque transfiere menos datos desde el servidor.

            ps.setString(1, documento); // Asigna el número de documento al primer '?' de la consulta. El JDBC driver previene inyección SQL al escapar caracteres especiales automáticamente.

            rs = ps.executeQuery(); // Envía la consulta al servidor MySQL y recibe las filas resultantes. Si no hay ningún usuario con ese documento, el ResultSet estará vacío.

            return rs.next(); // Mueve el cursor a la primera fila del resultado. Si existe al menos un registro (el documento ya está registrado), retorna true; si no hay filas, retorna false.

        } catch (Exception e) { // Captura excepciones como errores de conexión, sintaxis SQL inválida, etc.
            e.printStackTrace(); // Muestra el stack trace completo en la consola para facilitar el diagnóstico.
            return false; // Retorna false ante cualquier error. Esto significa que, si la BD falla, el sistema podría intentar insertar un duplicado, pero no bloqueará al usuario con un error genérico.

        } finally { // Bloque que siempre se ejecuta para garantizar la limpieza de recursos.
            cerrar(rs, ps, conn); // Cierra el ResultSet, PreparedStatement y Connection en ese orden, liberando recursos del servidor de BD.
        }
    }

    // ==================================================================================
    // MÉTODO PÚBLICO: validarAcceso
    // Recibe: el email del formulario de login (String email) y la contraseña en texto plano (String pass)
    // Devuelve: un Map con {id, nombre, rol} si las credenciales son correctas, o null si son inválidas
    // Dirección: recibe datos desde el Servlet de login → consulta la BD con JOIN → devuelve Map al Servlet para crear la sesión HTTP
    // ==================================================================================
    public Map<String, Object> validarAcceso(String email, String pass) { // Método de autenticación. Verifica las credenciales contra la BD y retorna los datos del usuario si son correctas, o null si no coinciden.
        System.out.println(email); // Imprime el email recibido en la consola del servidor. Solo para depuración en desarrollo. En producción, esto debería eliminarse por seguridad (registro de emails en logs).
        System.out.println(pass); // Imprime la contraseña en la consola. ADVERTENCIA GRAVE: imprimir contraseñas en logs es una vulnerabilidad de seguridad. Debe eliminarse antes de pasar a producción.

        String sql = "SELECT u.id, u.nombre, r.nombre AS rol_nombre " + // Define la primera parte del SELECT: pide el ID del usuario, su nombre y el nombre del rol (con alias 'rol_nombre' para diferenciarlo del nombre del usuario).
                     "FROM correo c " +  // La consulta parte de la tabla 'correo' (que tiene el email) y une el resto de tablas hacia afuera.
                     "JOIN usuarios u ON c.usuario_id = u.id " + // Une la tabla 'correo' con 'usuarios' usando la clave foránea 'usuario_id'. Así conectamos el email con los datos del usuario.
                     "JOIN roles_usuarios ru ON u.id = ru.usuario_id " + // Une 'usuarios' con la tabla intermedia 'roles_usuarios' para saber qué rol tiene asignado ese usuario.
                     "JOIN roles r ON ru.rol_id = r.id " + // Une la tabla intermedia 'roles_usuarios' con la tabla 'roles' para obtener el nombre legible del rol (ej: 'administrador', 'supervisor', 'trabajador').
                     "WHERE c.email = ? AND u.pass = MD5(?)"; // Filtra por dos condiciones:
                     // 1. c.email = ?: el email debe coincidir exactamente con el ingresado.
                     // 2. u.pass = MD5(?): la contraseña almacenada (que está en MD5) debe ser igual al hash MD5 de la contraseña ingresada. MD5() es una función nativa de MySQL.

        try (Connection conn = Conexion.getConnection(); // TRY-WITH-RESOURCES: abre la conexión. Se cerrará automáticamente al terminar el bloque try.
             PreparedStatement ps = conn.prepareStatement(sql)) { // Prepara la consulta SQL con los JOINs y el filtro de autenticación. También se cerrará automáticamente.

            ps.setString(1, email.trim()); // Asigna el email al primer '?'. El método .trim() elimina espacios en blanco al inicio y al final del texto ingresado por el usuario, evitando problemas de comparación.
            ps.setString(2, pass.trim()); // Asigna la contraseña al segundo '?'. .trim() elimina espacios accidentales. MySQL aplicará MD5() a este valor antes de compararlo con el hash guardado.

            try (ResultSet rs = ps.executeQuery()) { // Ejecuta la consulta y abre el ResultSet en un segundo try-with-resources. El ResultSet se cerrará automáticamente al salir de este bloque interno.
                if (rs.next()) { // Si la consulta devolvió al menos una fila, las credenciales son correctas y existe el usuario con ese rol.
                    Map<String, Object> datos = new HashMap<>(); // Crea un HashMap para almacenar los datos del usuario autenticado. Se usa Map<String, Object> (no Map<String, String>) porque el 'id' es un número entero.

                    datos.put("id", rs.getInt("id")); // Extrae el ID del usuario (entero) de la columna 'id' del ResultSet y lo guarda en el Map con la clave "id". Este ID se usará para identificar la sesión del usuario.
                    datos.put("nombre", rs.getString("nombre")); // Extrae el nombre del usuario (texto) de la columna 'nombre' del ResultSet y lo guarda con la clave "nombre". Se mostrará en la interfaz.
                    datos.put("rol", rs.getString("rol_nombre")); // Extrae el nombre del rol (el alias definido en el SQL) del ResultSet y lo guarda con la clave "rol". Se usará para controlar los permisos en la aplicación.

                    return datos; // Retorna el Map con los datos del usuario. El Servlet receptor creará una sesión HTTP con esta información y redirigirá al usuario al panel principal.
                }
            }
        } catch (Exception e) { // Captura cualquier excepción: error de conexión, SQL mal escrito, etc.
            e.printStackTrace(); // Imprime la traza del error para diagnóstico.
        }

        return null; // Si el email no existe, la contraseña es incorrecta, o hubo un error técnico, retorna null. El Servlet interpretará null como "acceso denegado" y mostrará un mensaje de error al usuario.
    }

    // ==================================================================================
    // MÉTODO PÚBLICO: crear
    // Recibe: nombre, pass, documento, direccion, correo, telefono, rolId (int), foto del usuario nuevo
    // Devuelve: true si el usuario fue creado exitosamente, false si ya existe el correo/documento o hubo error
    // Dirección: recibe datos del formulario de registro → inserta en 5 tablas en transacción → devuelve resultado al Servlet
    // ==================================================================================
    public boolean crear(String nombre, String pass, String documento, String direccion,
                         String correo, String telefono, int rolId, String foto) { // Método que crea un usuario completo insertando datos en múltiples tablas de forma atómica (todo o nada).
        if (existeCorreo(correo) || existeDocumento(documento)) return false; // Antes de cualquier inserción, verifica si el correo o el documento ya están registrados. Si cualquiera de los dos existe, retorna false inmediatamente para evitar duplicados. El operador '||' (OR) hace que baste con que uno de los dos exista.

        Connection conn = null; // Declara la conexión fuera del try para poder hacer rollback en el catch y cerrarla en el finally.
        try { // Inicia el bloque de la transacción. Todos los INSERT deben completarse o ninguno debe guardarse.
            conn = Conexion.getConnection(); // Obtiene la conexión activa desde la clase Conexion.
            conn.setAutoCommit(false); // DESHABILITA el auto-commit. Por defecto, cada sentencia SQL se confirma automáticamente. Al poner false, todas las operaciones quedan en un buffer temporal hasta que llamemos a conn.commit() o conn.rollback().
            int usuarioId = 0; // Variable que almacenará el ID autogenerado por la BD al insertar el nuevo usuario. Se inicializa en 0 y se actualizará después del INSERT.

            String sqlU = "INSERT INTO usuarios (nombre, pass, documento, direccion, estado) VALUES (?, MD5(?), ?, ?, 'Activo')"; // Prepara la sentencia SQL para insertar en la tabla principal 'usuarios'. Notar que 'pass' va dentro de MD5() directamente en MySQL, y 'estado' se inserta directamente como 'Activo' sin parámetro porque siempre será ese valor al crear.
            try (PreparedStatement ps = conn.prepareStatement(sqlU, Statement.RETURN_GENERATED_KEYS)) { // Prepara la sentencia con el flag 'RETURN_GENERATED_KEYS'. Este flag le indica al driver JDBC que, después del INSERT, devuelva el ID autoincremental que MySQL generó para el nuevo registro.
                ps.setString(1, nombre); // Asigna el nombre del usuario al primer '?'.
                ps.setString(2, pass); // Asigna la contraseña al segundo '?'. MySQL aplicará la función MD5() a este valor antes de almacenarlo, generando el hash de 32 caracteres.
                ps.setString(3, documento); // Asigna el número de documento al tercer '?'.
                ps.setString(4, direccion); // Asigna la dirección al cuarto '?'. El quinto valor ('Activo') está directamente en el SQL, no como parámetro.
                ps.executeUpdate(); // Ejecuta el INSERT en la BD. Retorna el número de filas afectadas (debería ser 1). El resultado no se guarda porque solo nos interesa el ID generado.
                try (ResultSet rs = ps.getGeneratedKeys()) { // Abre un ResultSet especial que contiene la(s) clave(s) primaria(s) generadas automáticamente por el INSERT anterior.
                    if (rs.next()) usuarioId = rs.getInt(1); // Si hay un ID generado (siempre lo habrá si el INSERT fue exitoso), lo extrae de la primera columna y lo guarda en 'usuarioId'. Este ID se necesitará para los siguientes INSERT en las tablas relacionadas.
                }
            }

            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO correo (email, usuario_id) VALUES (?, ?)")) { // Prepara el INSERT para la tabla 'correo', que almacena los emails separados de los usuarios principales.
                ps.setString(1, correo); // Asigna el email al primer '?'.
                ps.setInt(2, usuarioId); // Asigna el ID del usuario recién creado al segundo '?'. Esto crea la relación entre la tabla 'correo' y la tabla 'usuarios'.
                ps.executeUpdate(); // Ejecuta el INSERT del correo. Como auto-commit está desactivado, este cambio aún no se guarda permanentemente en la BD.
            }

            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO telefono (numero, usuario_id) VALUES (?, ?)")) { // Prepara el INSERT para la tabla 'telefono', que almacena los números de teléfono relacionados con cada usuario.
                ps.setString(1, telefono); // Asigna el número de teléfono al primer '?'.
                ps.setInt(2, usuarioId); // Asigna el ID del usuario al segundo '?' para mantener la relación entre las tablas.
                ps.executeUpdate(); // Ejecuta el INSERT del teléfono en la BD (aún pendiente de commit).
            }

            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO roles_usuarios (usuario_id, rol_id) VALUES (?, ?)")) { // Prepara el INSERT para la tabla intermedia 'roles_usuarios', que implementa la relación muchos-a-muchos entre usuarios y roles.
                ps.setInt(1, usuarioId); // Asigna el ID del usuario al primer '?'.
                ps.setInt(2, rolId); // Asigna el ID del rol seleccionado al segundo '?'. El 'rolId' viene del formulario de registro (ej: 1=administrador, 2=supervisor, 3=trabajador).
                ps.executeUpdate(); // Ejecuta el INSERT de la asignación de rol (aún pendiente de commit).
            }

            if (foto != null && !foto.isEmpty()) { // Verifica que la foto no sea null y que no sea un String vacío. La foto es opcional: si el usuario no subió imagen, este bloque se omite.
                try (PreparedStatement ps = conn.prepareStatement("INSERT INTO fotos_usuario (usuario_id, ruta) VALUES (?, ?)")) { // Prepara el INSERT para la tabla 'fotos_usuario'. Almacena la ruta relativa de la imagen en el servidor de archivos.
                    ps.setInt(1, usuarioId); // Asigna el ID del usuario al primer '?'.
                    ps.setString(2, foto); // Asigna el nombre/ruta del archivo de foto al segundo '?'. Esta ruta se usará luego para mostrar la imagen en la interfaz.
                    ps.executeUpdate(); // Ejecuta el INSERT de la foto (aún pendiente de commit).
                }
            }

            conn.commit(); // CONFIRMA TODOS LOS CAMBIOS. Solo en este punto los 4 o 5 INSERT quedan grabados permanentemente en la BD. Si alguno de los INSERT anteriores falló, el flujo ya habría saltado al catch, nunca llegando aquí.
            return true; // Retorna true indicando que el usuario fue creado exitosamente en todas las tablas.
        } catch (Exception e) { // Captura cualquier excepción que ocurra en cualquiera de los INSERT anteriores.
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {} // Si ocurrió un error, deshace TODOS los cambios realizados desde el último setAutoCommit(false). Esto garantiza que no queden datos parciales: si falló insertar el teléfono, también se cancela el INSERT del usuario. El try-catch interno maneja el caso poco probable de que el rollback mismo falle.
            e.printStackTrace(); // Imprime la traza del error para diagnóstico del desarrollador.
            return false; // Retorna false indicando que el proceso de creación falló.
        } finally { // Siempre se ejecuta, asegurando la liberación de la conexión.
            cerrar(null, null, conn); // Cierra solo la conexión (los PreparedStatement se cierran automáticamente por el try-with-resources interno). Se pasan null en los dos primeros parámetros porque ya fueron cerrados.
        }
    }

    // ==================================================================================
    // MÉTODO PÚBLICO: listarUsuarios
    // Recibe: nada (no tiene parámetros)
    // Devuelve: una Lista de Mapas, donde cada Map representa un usuario con todos sus datos
    // Dirección: no recibe parámetros → consulta la BD con múltiples JOIN → devuelve la lista al Servlet para mostrarla en la vista
    // ==================================================================================
    public List<Map<String, String>> listarUsuarios() { // Método que recupera todos los usuarios de la BD con sus datos completos, incluyendo email, teléfono, rol y foto.
        List<Map<String, String>> lista = new ArrayList<>(); // Crea una lista vacía que almacenará los resultados. Cada elemento de la lista será un Map que representa un usuario.
        Connection conn = null; // Declara la conexión como null para acceder a ella en el finally.
        PreparedStatement ps = null; // Declara la sentencia como null para cerrarla en el finally.
        ResultSet rs = null; // Declara el cursor de resultados como null para cerrarlo en el finally.
        try { // Inicia el bloque de consulta a la BD.
            conn = Conexion.getConnection(); // Obtiene la conexión activa hacia la BD.
            String sql = "SELECT u.id, u.nombre, u.documento, u.direccion, u.estado, " + // Selecciona campos básicos del usuario desde la tabla 'usuarios' (alias 'u').
                         "MAX(COALESCE(f.ruta, 'asset/imagenes/default-avatar.png')) AS foto, " + // MAX + COALESCE: si el usuario tiene foto en 'fotos_usuario', usa su ruta; si no tiene ('f.ruta' es null), usa una imagen por defecto. MAX() se aplica porque el GROUP BY puede generar múltiples filas por usuario si tiene varias fotos.
                         "MAX(COALESCE(c.email, 'Sin correo')) AS email, " + // Similar al anterior: usa el email real si existe, o el texto 'Sin correo' si la columna es null. MAX() maneja el caso de múltiples correos.
                         "MAX(COALESCE(t.numero, 'Sin teléfono')) AS telefono, " + // Usa el teléfono real si existe, o 'Sin teléfono' si es null. MAX() con GROUP BY evita duplicados.
                         "MAX(COALESCE(r.nombre, 'Sin rol')) AS rol " + // Usa el nombre del rol si existe, o 'Sin rol' si es null.
                         "FROM usuarios u " + // La tabla principal es 'usuarios'.
                         "LEFT JOIN fotos_usuario f ON u.id = f.usuario_id " + // LEFT JOIN con 'fotos_usuario': incluye usuarios aunque no tengan foto (el LEFT JOIN devuelve null para f.ruta si no hay foto).
                         "LEFT JOIN correo c ON u.id = c.usuario_id " + // LEFT JOIN con 'correo': incluye usuarios aunque no tengan email registrado en esa tabla.
                         "LEFT JOIN telefono t ON u.id = t.usuario_id " + // LEFT JOIN con 'telefono': incluye usuarios aunque no tengan teléfono.
                         "LEFT JOIN roles_usuarios ru ON u.id = ru.usuario_id " + // LEFT JOIN con la tabla intermedia 'roles_usuarios' para conectar usuarios con roles.
                         "LEFT JOIN roles r ON ru.rol_id = r.id " + // LEFT JOIN con la tabla 'roles' para obtener el nombre del rol.
                         "GROUP BY u.id " + // Agrupa los resultados por el ID único de usuario. Esto colapsa todas las filas de un mismo usuario (que pueden multiplicarse por los JOINs) en una sola fila por usuario.
                         "ORDER BY u.id ASC"; // Ordena los resultados por ID de forma ascendente (el usuario con menor ID aparece primero).

            ps = conn.prepareStatement(sql); // Prepara la sentencia SQL compleja en el servidor de BD para su ejecución optimizada.
            rs = ps.executeQuery(); // Ejecuta la consulta. El servidor procesa todos los JOINs y agrupaciones y devuelve una fila por usuario.

            while (rs.next()) { // Itera sobre cada fila del resultado. 'rs.next()' mueve el cursor a la siguiente fila y retorna true mientras haya más filas.
                Map<String, String> u = new HashMap<>(); // Crea un nuevo Map (vacío) para cada usuario. Almacenará todos los campos como pares clave-valor de tipo String.
                u.put("id", String.valueOf(rs.getInt("id"))); // Extrae el ID entero y lo convierte a String con String.valueOf(). Los Maps de String no pueden almacenar int directamente.
                u.put("nombre", rs.getString("nombre")); // Extrae el nombre del usuario de la columna 'nombre'.
                u.put("documento", rs.getString("documento")); // Extrae el número de documento de identidad.
                u.put("direccion", rs.getString("direccion")); // Extrae la dirección del usuario.
                u.put("estado", rs.getString("estado")); // Extrae el estado ('Activo' o 'Inactivo') del usuario.
                u.put("foto", rs.getString("foto")); // Extrae la ruta de la foto (o la ruta por defecto si no tiene).
                u.put("correo", rs.getString("email")); // Extrae el email. Nota: la columna en el SQL se llama 'email' pero se guarda en el Map con la clave 'correo' para consistencia con otras partes del sistema.
                u.put("telefono", rs.getString("telefono")); // Extrae el número de teléfono (o 'Sin teléfono' si no tiene).
                u.put("rol", rs.getString("rol")); // Extrae el nombre del rol asignado al usuario.
                lista.add(u); // Agrega el Map del usuario actual a la lista de resultados.
            }
        } catch (Exception e) { // Captura cualquier excepción durante la consulta.
            e.printStackTrace(); // Imprime la traza del error para diagnóstico.
        } finally { // Se ejecuta siempre para liberar recursos.
            cerrar(rs, ps, conn); // Cierra el ResultSet, PreparedStatement y Connection en orden correcto.
        }
        return lista; // Retorna la lista completa de usuarios al Servlet o a quien llamó este método. Si no hay usuarios o hubo un error, la lista estará vacía.
    }

    // ==================================================================================
    // MÉTODO PÚBLICO: editarUsuario
    // Recibe: id, nombre, documento, direccion, correo, telefono, pass, rolId, estado, nombreFoto (todos String)
    // Devuelve: true si la edición fue exitosa, false si el correo está duplicado o hubo un error
    // Dirección: recibe datos del formulario de edición → actualiza múltiples tablas en transacción → devuelve resultado al Servlet
    // ==================================================================================
    public boolean editarUsuario(String id, String nombre, String documento, String direccion,
                                String correo, String telefono, String pass, String rolId,
                                String estado, String nombreFoto) { // Método que actualiza los datos de un usuario existente en todas las tablas relacionadas de forma atómica.
        Connection conn = null; // Declara la conexión fuera del try para poder hacer rollback en el catch y cerrarla en el finally.
        try { // Inicia el bloque de la transacción de actualización.
            int usuarioId = Integer.parseInt(id); // Convierte el ID que llegó como String (desde el formulario HTML) a un entero. Si 'id' no es un número válido, lanza NumberFormatException y el método retornará false.
            conn = Conexion.getConnection(); // Obtiene la conexión activa desde la BD.
            conn.setAutoCommit(false); // Deshabilita el auto-commit para que todos los UPDATE sean atómicos: si alguno falla, se revertirán todos.
            if (existeCorreo(correo, usuarioId)) return false; // Verifica si el nuevo correo ya está en uso por OTRO usuario diferente. Usa la versión sobrecargada que excluye al usuario actual. Si está duplicado, retorna false inmediatamente sin modificar nada.

            try (PreparedStatement ps = conn.prepareStatement("UPDATE usuarios SET nombre=?, documento=?, direccion=?, estado=? WHERE id=?")) { // Prepara el UPDATE para la tabla principal 'usuarios'. Actualiza nombre, documento, dirección y estado donde el ID coincida.
                ps.setString(1, nombre); // Asigna el nuevo nombre al primer '?'.
                ps.setString(2, documento); // Asigna el nuevo número de documento al segundo '?'.
                ps.setString(3, direccion); // Asigna la nueva dirección al tercer '?'.
                ps.setString(4, estado); // Asigna el nuevo estado ('Activo' o 'Inactivo') al cuarto '?'.
                ps.setInt(5, usuarioId); // Asigna el ID del usuario al quinto '?' (cláusula WHERE). Solo se actualiza el registro con ese ID específico.
                ps.executeUpdate(); // Ejecuta el UPDATE. Si no hay ningún usuario con ese ID, no afecta ninguna fila pero no lanza excepción.
            }

            try (PreparedStatement ps = conn.prepareStatement("UPDATE correo SET email=? WHERE usuario_id=?")) { // Prepara el UPDATE para la tabla 'correo'. Actualiza el email del usuario identificado por su ID.
                ps.setString(1, correo); ps.setInt(2, usuarioId); ps.executeUpdate(); // Asigna el nuevo email y el ID en una sola línea compacta, luego ejecuta el UPDATE.
            }

            try (PreparedStatement ps = conn.prepareStatement("UPDATE telefono SET numero=? WHERE usuario_id=?")) { // Prepara el UPDATE para la tabla 'telefono'. Actualiza el número de teléfono asociado al usuario.
                ps.setString(1, telefono); ps.setInt(2, usuarioId); ps.executeUpdate(); // Asigna el nuevo teléfono y el ID, luego ejecuta el UPDATE.
            }

            try (PreparedStatement ps = conn.prepareStatement("UPDATE roles_usuarios SET rol_id=? WHERE usuario_id=?")) { // Prepara el UPDATE para la tabla intermedia 'roles_usuarios'. Cambia el rol asignado al usuario.
                ps.setInt(1, Integer.parseInt(rolId)); ps.setInt(2, usuarioId); ps.executeUpdate(); // Convierte el rolId de String a int, lo asigna junto con el ID del usuario, y ejecuta el UPDATE.
            }

            if (pass != null && !pass.trim().isEmpty()) { // Verifica si se ingresó una nueva contraseña. Si 'pass' es null o está vacío, no se actualiza (se mantiene la contraseña anterior).
                try (PreparedStatement ps = conn.prepareStatement("UPDATE usuarios SET pass=MD5(?) WHERE id=?")) { // Prepara un UPDATE separado solo para la contraseña. MD5() se aplica en MySQL para generar el hash.
                    ps.setString(1, pass); ps.setInt(2, usuarioId); ps.executeUpdate(); // Asigna la nueva contraseña (MySQL la hasheará) y el ID del usuario, luego ejecuta el UPDATE.
                }
            }

            if (nombreFoto != null && !nombreFoto.trim().isEmpty()) { // Verifica si se subió una nueva foto. Si no se subió imagen, este bloque se omite.
                try (PreparedStatement ps = conn.prepareStatement("INSERT INTO fotos_usuario (usuario_id, ruta) VALUES (?, ?) ON DUPLICATE KEY UPDATE ruta=?")) { // Usa la cláusula especial de MySQL 'ON DUPLICATE KEY UPDATE': si ya existe un registro para este usuario_id (clave única), actualiza la ruta. Si no existe, inserta uno nuevo. Evita tener que hacer un SELECT previo.
                    ps.setInt(1, usuarioId); ps.setString(2, nombreFoto); ps.setString(3, nombreFoto); ps.executeUpdate(); // Asigna el ID del usuario, la nueva ruta de foto (dos veces: una para el INSERT y otra para el UPDATE), luego ejecuta la operación.
                }
            }
            conn.commit(); // Confirma TODOS los UPDATE como un bloque atómico. Solo en este punto los cambios son permanentes en la BD.
            return true; // Retorna true indicando que todos los datos del usuario fueron actualizados exitosamente.
        } catch (Exception e) { // Captura cualquier excepción durante alguno de los UPDATE.
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {} // Si algo salió mal, revierte todos los cambios realizados desde el setAutoCommit(false), dejando la BD en su estado original.
            return false; // Retorna false indicando que la edición falló.
        } finally { cerrar(null, null, conn); } // Siempre cierra la conexión, pasando null para los otros dos recursos ya que se cerraron automáticamente por sus try-with-resources.
    }

    // ==================================================================================
    // MÉTODO PÚBLICO: obtenerPorId
    // Recibe: el ID del usuario como String (String id)
    // Devuelve: un Map<String, String> con todos los datos del usuario, o un Map vacío si no se encuentra
    // Dirección: recibe el ID del Servlet → consulta la BD con JOINs → devuelve el Map al Servlet para pre-llenar el formulario de edición
    // ==================================================================================
    public Map<String, String> obtenerPorId(String id) { // Método que recupera todos los datos de un usuario específico por su ID. Se usa generalmente para cargar el formulario de edición.
        Map<String, String> u = new HashMap<>(); // Crea un Map vacío que recibirá los datos del usuario. Si el ID no existe, este Map permanecerá vacío.
        Connection conn = null; // Declara la conexión fuera del try para cerrarla en el finally.
        PreparedStatement ps = null; // Declara la sentencia fuera del try para cerrarla en el finally.
        ResultSet rs = null; // Declara el cursor fuera del try para cerrarlo en el finally.
        try { // Bloque de consulta a la BD.
            conn = Conexion.getConnection(); // Obtiene la conexión activa.

            ps = conn.prepareStatement("SELECT u.*, c.email, t.numero AS telefono, r.nombre as rol FROM usuarios u " + // SELECT con comodín 'u.*': trae TODAS las columnas de la tabla 'usuarios'. Luego añade email de 'correo', número de 'telefono' (con alias), y nombre de 'roles'.
                "LEFT JOIN correo c ON c.usuario_id = u.id " + // LEFT JOIN con 'correo': trae el email aunque el usuario no tenga ninguno registrado.
                "LEFT JOIN telefono t ON t.usuario_id = u.id " + // LEFT JOIN con 'telefono': trae el teléfono aunque sea null.
                "LEFT JOIN roles_usuarios ru ON ru.usuario_id = u.id " + // LEFT JOIN con la tabla intermedia de roles.
                "LEFT JOIN roles r ON r.id = ru.rol_id WHERE u.id = ?"); // LEFT JOIN con 'roles' y filtro por ID de usuario. El '?' será el ID específico a buscar.

            ps.setInt(1, Integer.parseInt(id)); // Convierte el ID de String a int y lo asigna al primer '?'. Integer.parseInt puede lanzar NumberFormatException si el String no es numérico.
            rs = ps.executeQuery(); // Ejecuta la consulta. Debería devolver máximo una fila si el ID es único (clave primaria).

            if (rs.next()) { // Si se encontró el usuario con ese ID...
                u.put("id", id); // Guarda el ID como String en el Map. Se usa directamente el parámetro 'id' ya que es el mismo valor.
                u.put("nombre", rs.getString("nombre")); // Extrae y guarda el nombre del usuario.
                u.put("documento", rs.getString("documento")); // Extrae y guarda el número de documento.
                u.put("direccion", rs.getString("direccion")); // Extrae y guarda la dirección.
                u.put("estado", rs.getString("estado")); // Extrae y guarda el estado ('Activo' o 'Inactivo').
                u.put("correo", rs.getString("email")); // Extrae el email (columna 'email' del JOIN con 'correo') y lo guarda con la clave 'correo' para consistencia con la nomenclatura del sistema.
                u.put("telefono", rs.getString("telefono")); // Extrae el teléfono (alias del JOIN) y lo guarda.
                u.put("rol", rs.getString("rol")); // Extrae el nombre del rol (del JOIN con 'roles') y lo guarda.
            }
        } catch (Exception e) { // Captura cualquier excepción durante la consulta.
            e.printStackTrace(); // Imprime la traza del error para diagnóstico.
        } finally { // Siempre se ejecuta para liberar recursos.
            cerrar(rs, ps, conn); // Cierra los recursos JDBC en el orden correcto.
        }
        return u; // Retorna el Map con los datos del usuario. Si no se encontró el ID, retorna el Map vacío creado al inicio.
    }

    // ==================================================================================
    // MÉTODO PÚBLICO: actualizarEstado
    // Recibe: el ID del usuario (int usuarioId) y el nuevo estado (String nuevoEstado)
    // Devuelve: true si se actualizó al menos una fila, false si no se encontró el usuario o hubo error
    // Dirección: recibe el ID y estado desde el Servlet → actualiza la BD → devuelve resultado booleano
    // ==================================================================================
    public boolean actualizarEstado(int usuarioId, String nuevoEstado) { // Método para activar o desactivar un usuario (toggle de estado). Útil para suspender cuentas sin eliminarlas.
        Connection conn = null; // Declara la conexión para poder cerrarla en el finally.
        PreparedStatement ps = null; // Declara la sentencia para poder cerrarla en el finally.
        try { // Bloque de la actualización.
            conn = Conexion.getConnection(); // Obtiene la conexión activa.
            ps = conn.prepareStatement("UPDATE usuarios SET estado = ? WHERE id = ?"); // Prepara el UPDATE que solo modifica la columna 'estado' del registro identificado por su ID.
            ps.setString(1, nuevoEstado); ps.setInt(2, usuarioId); // Asigna el nuevo estado ('Activo' o 'Inactivo') al primer '?' y el ID del usuario al segundo '?'.
            return ps.executeUpdate() > 0; // executeUpdate() retorna el número de filas afectadas. Si es mayor que 0 (se actualizó al menos 1 usuario), retorna true; si es 0 (no existe el usuario con ese ID), retorna false.
        } catch (Exception e) { return false; } // Si ocurre cualquier excepción, retorna false sin imprimir el error (podría mejorarse para incluir logging).
        finally { cerrar(null, ps, conn); } // Siempre cierra la sentencia y la conexión. Se pasa null para el ResultSet porque este método no abrió ninguno.
    }

    // ==================================================================================
    // MÉTODO PÚBLICO: eliminarUsuario
    // Recibe: el ID del usuario a eliminar (int usuarioId)
    // Devuelve: true si se eliminó exitosamente de todas las tablas, false si hubo un error
    // Dirección: recibe el ID del Servlet → elimina registros en múltiples tablas en orden correcto (transacción) → devuelve resultado
    // ==================================================================================
    public boolean eliminarUsuario(int usuarioId) { // Método que elimina un usuario y todos sus datos relacionados de la BD. El orden de los DELETE es crítico para respetar las restricciones de clave foránea (Foreign Key constraints).
        Connection conn = null; // Declara la conexión para poder hacer rollback en el catch y cerrarla en el finally.
        try { // Inicia el bloque de la transacción de eliminación.
            conn = Conexion.getConnection(); // Obtiene la conexión activa.
            conn.setAutoCommit(false); // Deshabilita el auto-commit para que todos los DELETE sean atómicos: si alguno falla, se revertirán todos.

            String[] sqls = { // Define un arreglo de Strings con todas las sentencias DELETE en el orden correcto. Se elimina de las tablas más externas (que tienen claves foráneas) hacia la tabla central ('usuarios').
                "DELETE FROM supervisor WHERE usuario_id = ?",      // Elimina los registros de supervisión asociados al usuario.
                "DELETE FROM pagos WHERE usuario_id = ?",           // Elimina los registros de pagos asociados al usuario.
                "DELETE FROM cultivo_trabajador WHERE usuario_id = ?", // Elimina las relaciones del usuario con cultivos como trabajador.
                "DELETE FROM roles_usuarios WHERE usuario_id = ?",  // Elimina la asignación de rol del usuario en la tabla intermedia.
                "DELETE FROM correo WHERE usuario_id = ?",          // Elimina el registro de email del usuario.
                "DELETE FROM telefono WHERE usuario_id = ?",        // Elimina el registro de teléfono del usuario.
                "DELETE FROM fotos_usuario WHERE usuario_id = ?",   // Elimina la ruta de foto del usuario.
                "DELETE FROM usuarios WHERE id = ?"                 // Finalmente elimina el registro principal del usuario. Se hace al último porque las otras tablas dependen de este ID con claves foráneas.
            };

            for (String sql : sqls) { // Itera sobre cada sentencia DELETE del arreglo en el orden definido.
                try (PreparedStatement ps = conn.prepareStatement(sql)) { // Prepara cada sentencia dentro de un try-with-resources para que el PreparedStatement se cierre automáticamente en cada iteración.
                    ps.setInt(1, usuarioId); // Asigna el ID del usuario a eliminar al único parámetro '?' de cada sentencia DELETE.
                    ps.executeUpdate(); // Ejecuta el DELETE. Si el usuario no tiene registros en esa tabla, el DELETE simplemente no afecta ninguna fila pero no genera error.
                }
            }

            conn.commit(); // Confirma todos los DELETE como una unidad atómica. El usuario y todos sus datos relacionados se eliminan permanentemente de la BD.
            return true; // Retorna true indicando que el usuario fue eliminado exitosamente.
        } catch (Exception e) { // Captura cualquier excepción durante los DELETE (ej: restricción de clave foránea no contemplada, error de conexión, etc.).
            if (conn != null) { // Verifica que la conexión no sea null antes de intentar el rollback.
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } // Revierte todos los DELETE ejecutados hasta el momento del error. El try-catch interno maneja el caso raro de que el rollback también falle.
            }
            e.printStackTrace(); // Imprime la traza del error. Útil para identificar qué tabla o restricción causó el fallo.
            return false; // Retorna false indicando que la eliminación no se completó.
        } finally { // Siempre se ejecuta para liberar la conexión.
            cerrar(null, null, conn); // Cierra la conexión. Los PreparedStatement se cerraron automáticamente por el try-with-resources dentro del bucle.
        }
    }

    // ==================================================================================
    // MÉTODO PÚBLICO: actualizarPerfil
    // Recibe: id (int), nombre, documento, direccion, pass, correo, telefono, nombreFoto (todos String)
    // Devuelve: true si el perfil fue actualizado exitosamente, false si hubo error
    // Dirección: recibe datos del formulario de perfil del usuario → actualiza múltiples tablas en transacción → devuelve resultado al Servlet
    // ==================================================================================
    public boolean actualizarPerfil(int id, String nombre, String documento, String direccion, String pass, String correo, String telefono, String nombreFoto) { // Método que permite al propio usuario editar su perfil. Similar a 'editarUsuario' pero sin cambiar el estado ni el rol.
        Connection conn = null; // Declara la conexión para poder hacer rollback y cerrarla en el finally.
        try { // Inicia el bloque de la transacción.
            conn = Conexion.getConnection(); // Obtiene la conexión activa.
            conn.setAutoCommit(false); // Deshabilita el auto-commit para garantizar atomicidad en todos los UPDATE.

            String sqlUser = (pass == null || pass.trim().isEmpty()) // Ternario: evalúa si el usuario ingresó una nueva contraseña. Si 'pass' es null o vacío, elige el SQL sin actualizar la contraseña.
                ? "UPDATE usuarios SET nombre = ?, documento = ?, direccion = ? WHERE id = ?" // SQL SIN contraseña: solo actualiza nombre, documento y dirección. Se usa cuando el usuario no quiere cambiar su clave.
                : "UPDATE usuarios SET nombre = ?, documento = ?, direccion = ?, pass = MD5(?) WHERE id = ?"; // SQL CON contraseña: actualiza nombre, documento, dirección Y hashea la nueva contraseña con MD5() de MySQL.

            try (PreparedStatement ps = conn.prepareStatement(sqlUser)) { // Prepara la sentencia seleccionada por el ternario anterior.
                ps.setString(1, nombre); // Asigna el nuevo nombre al primer '?' (común en ambas versiones del SQL).
                ps.setString(2, documento); // Asigna el nuevo documento al segundo '?' (común en ambas versiones).
                ps.setString(3, direccion); // Asigna la nueva dirección al tercer '?' (común en ambas versiones).
                if (pass == null || pass.trim().isEmpty()) { // Verifica de nuevo si hay contraseña para saber cuántos parámetros asignar.
                    ps.setInt(4, id); // Sin contraseña: el cuarto '?' es el ID del usuario (cláusula WHERE).
                } else { // Con contraseña: hay un parámetro extra antes del ID.
                    ps.setString(4, pass); // Asigna la nueva contraseña al cuarto '?'. MySQL aplicará MD5() a este valor.
                    ps.setInt(5, id); // Asigna el ID del usuario al quinto '?' (cláusula WHERE).
                }
                ps.executeUpdate(); // Ejecuta el UPDATE de los datos principales del usuario.
            }

            if (nombreFoto != null && !nombreFoto.isEmpty()) { // Verifica si el usuario subió una nueva foto de perfil.
                String sqlFoto = "INSERT INTO fotos_usuario (usuario_id, ruta) VALUES (?, ?) " + // Si el usuario sube una foto, usa INSERT con ON DUPLICATE KEY para manejar tanto el caso de primera foto como el de reemplazo.
                                 "ON DUPLICATE KEY UPDATE ruta = ?"; // Si ya existe un registro para ese usuario_id en la tabla, actualiza solo la ruta. Si no existe, inserta el nuevo registro.
                try (PreparedStatement ps = conn.prepareStatement(sqlFoto)) { // Prepara la sentencia con la cláusula especial de MySQL.
                    ps.setInt(1, id); // Asigna el ID del usuario al primer '?' (para el INSERT).
                    ps.setString(2, nombreFoto); // Asigna la ruta de la nueva foto al segundo '?' (para el INSERT).
                    ps.setString(3, nombreFoto); // Asigna la ruta de la nueva foto al tercer '?' (para el UPDATE en caso de duplicado).
                    ps.executeUpdate(); // Ejecuta la operación INSERT o UPDATE según corresponda.
                }
            }

            try (PreparedStatement ps = conn.prepareStatement("UPDATE correo SET email = ? WHERE usuario_id = ?")) { // Prepara el UPDATE para actualizar el email del usuario en la tabla separada 'correo'.
                ps.setString(1, correo); // Asigna el nuevo email al primer '?'.
                ps.setInt(2, id); // Asigna el ID del usuario al segundo '?' (cláusula WHERE).
                ps.executeUpdate(); // Ejecuta el UPDATE del correo.
            }

            try (PreparedStatement ps = conn.prepareStatement("UPDATE telefono SET numero = ? WHERE usuario_id = ?")) { // Prepara el UPDATE para actualizar el teléfono del usuario en la tabla separada 'telefono'.
                ps.setString(1, telefono); // Asigna el nuevo número de teléfono al primer '?'.
                ps.setInt(2, id); // Asigna el ID del usuario al segundo '?' (cláusula WHERE).
                ps.executeUpdate(); // Ejecuta el UPDATE del teléfono.
            }

            conn.commit(); // Confirma todos los cambios del perfil como una unidad atómica. Todos los UPDATE quedan permanentes en la BD.
            return true; // Retorna true indicando que el perfil fue actualizado exitosamente.
        } catch (Exception e) { // Captura cualquier excepción durante los UPDATE.
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {} // Revierte todos los cambios si algo falló, manteniendo el perfil en su estado anterior.
            e.printStackTrace(); // Imprime la traza del error para diagnóstico.
            return false; // Retorna false indicando que la actualización del perfil falló.
        } finally { // Siempre se ejecuta para liberar recursos.
            cerrar(null, null, conn); // Cierra la conexión. Los PreparedStatement se cerraron automáticamente por sus try-with-resources.
        }
    }

    // ==================================================================================
    // MÉTODO PÚBLICO: obtenerProgresoPorCultivo
    // Recibe: el ID del usuario trabajador (int idUsuario)
    // Devuelve: un Map donde cada clave es el nombre de un cultivo y el valor es el porcentaje de tareas completadas
    // Dirección: recibe el ID desde el Servlet → consulta la tabla 'tareas' → devuelve el Map al Servlet para mostrar barras de progreso
    // ==================================================================================
    public Map<String, Integer> obtenerProgresoPorCultivo(int idUsuario) { // Método que calcula el porcentaje de avance de las tareas de un usuario agrupadas por cultivo. Se usa en el dashboard del trabajador.
        Map<String, Integer> progreso = new HashMap<>(); // Crea un Map vacío que almacenará nombre_cultivo → porcentaje.
        String sql = "SELECT nombre_cultivo, " + // Selecciona el nombre del cultivo y calcula el porcentaje de progreso.
                     "ROUND((COUNT(CASE WHEN estado = 'Completada' THEN 1 END) * 100.0) / COUNT(*)) as porcentaje " + // Cálculo del porcentaje:
                     // COUNT(CASE WHEN estado = 'Completada' THEN 1 END): cuenta solo las tareas con estado 'Completada'.
                     // * 100.0: multiplica por 100 y el .0 fuerza división de punto flotante (no entera).
                     // / COUNT(*): divide entre el total de tareas del grupo.
                     // ROUND(): redondea al entero más cercano para mostrar un porcentaje limpio (ej: 67%).
                     "FROM tareas WHERE id_usuario = ? " + // Filtra solo las tareas asignadas al usuario específico.
                     "GROUP BY nombre_cultivo"; // Agrupa los resultados por nombre de cultivo. Cada cultivo tendrá su propio porcentaje calculado independientemente.

        try (Connection con = Conexion.getConnection(); // TRY-WITH-RESOURCES: abre la conexión y la cierra automáticamente.
             PreparedStatement ps = con.prepareStatement(sql)) { // Prepara la sentencia SQL dentro del mismo bloque.

            ps.setInt(1, idUsuario); // Asigna el ID del usuario al parámetro '?' para filtrar solo sus tareas.
            try (ResultSet rs = ps.executeQuery()) { // Ejecuta la consulta y abre el ResultSet en un segundo try-with-resources.
                while (rs.next()) { // Itera sobre cada fila del resultado (un cultivo por fila).
                    progreso.put(rs.getString("nombre_cultivo"), rs.getInt("porcentaje")); // Extrae el nombre del cultivo y su porcentaje calculado, y los guarda como par clave-valor en el Map.
                }
            }
        } catch (Exception e) { // Captura cualquier excepción durante la consulta.
            e.printStackTrace(); // Imprime la traza del error para diagnóstico.
        }
        return progreso; // Retorna el Map con el progreso por cultivo. Si el usuario no tiene tareas, retorna el Map vacío.
    }

    // ==================================================================================
    // MÉTODO PÚBLICO: obtenerTareasPorRol
    // Recibe: el ID del usuario (int idUsuario) y su rol (String rol)
    // Devuelve: una Lista de Mapas con las tareas asignadas, filtradas según el rol del usuario
    // Dirección: recibe ID y rol desde el Servlet → consulta BD con JOINs (SQL diferente según rol) → devuelve lista al Servlet
    // ==================================================================================
    public List<Map<String, Object>> obtenerTareasPorRol(int idUsuario, String rol) { // Método que retorna tareas con lógica diferenciada por rol: el administrador ve TODO, el supervisor solo ve las tareas de los cultivos que supervisa.
        List<Map<String, Object>> lista = new ArrayList<>(); // Crea la lista vacía que almacenará todas las tareas encontradas.
        String sql; // Declara la variable SQL sin inicializar. Se asignará uno de dos queries dependiendo del rol.

        if ("administrador".equalsIgnoreCase(rol)) { // Compara el rol ignorando mayúsculas/minúsculas. 'equalsIgnoreCase' es más robusto que 'equals' para comparar roles.
            sql = "SELECT ut.id, t.nombre as tarea, c.nombre as cultivo, u.nombre as trabajador, " + // SQL para ADMINISTRADOR: consulta todas las tareas sin filtro de usuario.
                  "ut.descripcion_actividad, ut.estado, ut.jornada, ut.fecha_asignacion " + // Selecciona detalles de la tarea: descripción, estado, tipo de jornada y fecha de asignación.
                  "FROM usuario_tarea ut " + // Tabla principal: 'usuario_tarea' relaciona usuarios con tareas específicas.
                  "JOIN tareas t ON ut.tarea_id = t.id " + // JOIN para obtener el nombre de la tarea desde la tabla 'tareas'.
                  "JOIN cultivos c ON ut.cultivo_id = c.id " + // JOIN para obtener el nombre del cultivo donde se realiza la tarea.
                  "JOIN usuarios u ON ut.usuario_id = u.id"; // JOIN para obtener el nombre del trabajador asignado. SIN cláusula WHERE: el administrador ve todas las tareas de todos los usuarios.
        } else { // Si el rol es supervisor (o cualquier otro rol no-administrador)...
            sql = "SELECT ut.id, t.nombre as tarea, c.nombre as cultivo, u.nombre as trabajador, " + // SQL para SUPERVISOR: misma estructura de SELECT pero con filtro adicional.
                  "ut.descripcion_actividad, ut.estado, ut.jornada, ut.fecha_asignacion " + // Mismos campos de detalle de la tarea.
                  "FROM usuario_tarea ut " + // Misma tabla principal.
                  "JOIN tareas t ON ut.tarea_id = t.id " + // Mismo JOIN para nombre de tarea.
                  "JOIN cultivos c ON ut.cultivo_id = c.id " + // Mismo JOIN para nombre de cultivo.
                  "JOIN usuarios u ON ut.usuario_id = u.id " + // Mismo JOIN para nombre del trabajador.
                  "JOIN supervisor s ON s.cultivo_id = c.id " + // JOIN ADICIONAL con tabla 'supervisor': filtra solo los cultivos que este supervisor gestiona.
                  "WHERE s.usuario_id = ?"; // Filtro clave: solo muestra tareas de cultivos donde 'usuario_id' en la tabla 'supervisor' coincide con el ID del supervisor actual.
        }

        try (Connection con = Conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) { // TRY-WITH-RESOURCES: abre la conexión y prepara la sentencia seleccionada (admin o supervisor).
            if (!"administrador".equalsIgnoreCase(rol)) ps.setInt(1, idUsuario); // Si NO es administrador, asigna el ID del usuario al '?'. El SQL de administrador no tiene '?', así que esto solo aplica para el supervisor.
            try (ResultSet rs = ps.executeQuery()) { // Ejecuta la consulta y abre el ResultSet en un segundo try-with-resources.
                while (rs.next()) { // Itera sobre cada fila del resultado (una tarea por fila).
                    Map<String, Object> map = new HashMap<>(); // Crea un nuevo Map para cada tarea. Se usa Map<String, Object> porque los valores pueden ser de tipos mixtos.
                    map.put("tarea", rs.getString("tarea")); // Extrae y guarda el nombre de la tarea.
                    map.put("cultivo", rs.getString("cultivo")); // Extrae y guarda el nombre del cultivo.
                    map.put("trabajador", rs.getString("trabajador")); // Extrae y guarda el nombre del trabajador asignado.
                    map.put("descripcion", rs.getString("descripcion_actividad")); // Extrae y guarda la descripción de la actividad.
                    map.put("estado", rs.getString("estado")); // Extrae y guarda el estado de la tarea ('Pendiente', 'Proceso', 'Completada').
                    map.put("jornada", rs.getString("jornada")); // Extrae y guarda el tipo de jornada (mañana, tarde, noche, etc.).
                    map.put("fecha", rs.getString("fecha_asignacion")); // Extrae y guarda la fecha de asignación de la tarea.
                    lista.add(map); // Agrega el Map de esta tarea a la lista de resultados.
                }
            }
        } catch (Exception e) { e.printStackTrace(); } // Captura y muestra en consola cualquier excepción durante la consulta.
        return lista; // Retorna la lista con todas las tareas encontradas según el rol. Si no hay tareas, retorna la lista vacía.
    }

    // ==================================================================================
    // MÉTODO PÚBLICO: contarUsuarios
    // Recibe: nada (sin parámetros)
    // Devuelve: el número total de usuarios registrados en la tabla 'usuarios' (int)
    // Dirección: no recibe datos → consulta la BD → devuelve el conteo al Servlet para mostrar estadísticas
    // ==================================================================================
    public int contarUsuarios() { // Método simple que cuenta el total de usuarios registrados. Útil para mostrar estadísticas en el dashboard del administrador.
        int total = 0; // Variable de resultado inicializada en 0. Si la consulta falla, retorna 0 como valor seguro.
        Connection conn = null; // Declara la conexión fuera del try para cerrarla en el finally.
        PreparedStatement ps = null; // Declara la sentencia fuera del try para cerrarla en el finally.
        ResultSet rs = null; // Declara el cursor fuera del try para cerrarlo en el finally.
        try { // Bloque de consulta.
            conn = Conexion.getConnection(); // Obtiene la conexión activa.
            ps = conn.prepareStatement("SELECT COUNT(*) FROM usuarios"); // Prepara la consulta que cuenta TODAS las filas de la tabla 'usuarios'. COUNT(*) cuenta todas las filas incluyendo los de cualquier estado.
            rs = ps.executeQuery(); // Ejecuta la consulta. Siempre devolverá exactamente una fila con un número entero.
            if (rs.next()) { // Mueve el cursor a la primera (y única) fila del resultado.
                total = rs.getInt(1); // Lee el valor de la primera columna (el conteo) y lo guarda en 'total'.
            }
        } catch (Exception e) { // Captura cualquier excepción durante la consulta.
            e.printStackTrace(); // Imprime la traza del error para diagnóstico.
        } finally { // Siempre se ejecuta para liberar recursos.
            cerrar(rs, ps, conn); // Cierra el ResultSet, PreparedStatement y Connection.
        }
        return total; // Retorna el total de usuarios. Si hubo error, retorna 0.
    }

    // ==================================================================================
    // MÉTODO PÚBLICO: obtenerResumenTareas
    // Recibe: el ID del usuario (int idUsuario)
    // Devuelve: un Map con las claves "Completada", "Proceso" y "Pendiente" mapeadas a sus conteos
    // Dirección: recibe el ID del Servlet → consulta 'usuario_tarea' → devuelve el resumen para el dashboard del trabajador
    // ==================================================================================
    public Map<String, Integer> obtenerResumenTareas(int idUsuario) { // Método que cuenta cuántas tareas tiene el usuario en cada estado. Se usa para mostrar tarjetas de resumen en el perfil o dashboard.
        Map<String, Integer> resumen = new HashMap<>(); // Crea el Map con valores iniciales en 0 para cada estado.
        resumen.put("Completada", 0); // Inicializa en 0 el conteo de tareas completadas. Garantiza que el Map siempre tenga las tres claves aunque la consulta no devuelva datos.
        resumen.put("Proceso", 0); // Inicializa en 0 el conteo de tareas en proceso.
        resumen.put("Pendiente", 0); // Inicializa en 0 el conteo de tareas pendientes.

        String sql = "SELECT estado, COUNT(*) as total FROM usuario_tarea WHERE usuario_id = ? GROUP BY estado"; // Consulta que agrupa las tareas del usuario por estado y cuenta cuántas hay en cada grupo. Retorna máximo 3 filas (una por estado posible).

        try (Connection con = Conexion.getConnection(); // TRY-WITH-RESOURCES: abre la conexión.
             PreparedStatement ps = con.prepareStatement(sql)) { // Prepara la sentencia.
            ps.setInt(1, idUsuario); // Asigna el ID del usuario al '?' para filtrar solo sus tareas.
            try (ResultSet rs = ps.executeQuery()) { // Ejecuta la consulta y abre el ResultSet.
                while (rs.next()) { // Itera sobre cada fila del resultado (un estado por fila).
                    resumen.put(rs.getString("estado"), rs.getInt("total")); // Extrae el nombre del estado ('Completada', 'Proceso' o 'Pendiente') y su conteo, y actualiza el valor en el Map. Sobreescribe los valores 0 inicializados al inicio del método.
                }
            }
        } catch (Exception e) { // Captura cualquier excepción durante la consulta.
            e.printStackTrace(); // Imprime la traza del error.
        }
        return resumen; // Retorna el Map con los conteos por estado. Siempre tendrá las tres claves (por la inicialización), aunque algunas puedan tener valor 0.
    }

    // ==================================================================================
    // MÉTODO PÚBLICO: listarTrabajadores
    // Recibe: nada (sin parámetros)
    // Devuelve: una Lista de Mapas con {id, nombre} de usuarios con rol 'trabajador' O 'supervisor' que están Activos
    // Dirección: no recibe datos → consulta la BD con JOINs → devuelve la lista al Servlet para poblar dropdowns de asignación
    // ==================================================================================
    public List<Map<String, String>> listarTrabajadores() { // Método que obtiene todos los usuarios activos con rol de trabajador o supervisor. Se usa para cargar listas desplegables donde se asignan tareas.
        List<Map<String, String>> lista = new ArrayList<>(); // Crea la lista vacía que almacenará los trabajadores encontrados.
        String sql = "SELECT u.id, u.nombre " + // Selecciona solo el ID y el nombre del usuario (lo mínimo necesario para un dropdown).
                     "FROM usuarios u " + // Tabla principal de usuarios.
                     "JOIN roles_usuarios ru ON u.id = ru.usuario_id " + // JOIN con la tabla intermedia para acceder a los roles.
                     "JOIN roles r ON ru.rol_id = r.id " + // JOIN con la tabla 'roles' para filtrar por nombre de rol.
                     "WHERE r.nombre IN ('trabajador', 'supervisor') AND u.estado = 'Activo'"; // Filtra usuarios que tengan rol 'trabajador' O 'supervisor' (IN permite múltiples valores) Y que estén activos (no suspendidos).

        try (Connection conn = Conexion.getConnection(); // TRY-WITH-RESOURCES: abre la conexión.
             PreparedStatement ps = conn.prepareStatement(sql); // Prepara la sentencia SQL.
             ResultSet rs = ps.executeQuery()) { // Ejecuta la consulta inmediatamente. Todos los recursos se declaran en el mismo try-with-resources.

            while (rs.next()) { // Itera sobre cada trabajador/supervisor activo encontrado.
                Map<String, String> t = new HashMap<>(); // Crea un Map ligero para cada usuario con solo ID y nombre.
                t.put("id", String.valueOf(rs.getInt("id"))); // Extrae el ID entero y lo convierte a String.
                t.put("nombre", rs.getString("nombre")); // Extrae el nombre del usuario.
                lista.add(t); // Agrega el Map del usuario a la lista de resultados.
            }
        } catch (Exception e) { // Captura cualquier excepción durante la consulta.
            e.printStackTrace(); // Imprime la traza del error.
        }
        return lista; // Retorna la lista de trabajadores y supervisores activos.
    }

    // ==================================================================================
    // MÉTODO PÚBLICO: listarSoloSupervisores
    // Recibe: nada (sin parámetros)
    // Devuelve: una Lista de Mapas con {id, nombre} de usuarios con rol exclusivamente 'supervisor' y estado 'Activo'
    // Dirección: no recibe datos → consulta la BD → devuelve la lista al Servlet para dropdowns de asignación de supervisores
    // ==================================================================================
    public List<Map<String, String>> listarSoloSupervisores() { // Método que obtiene ÚNICAMENTE los supervisores activos. Se usa para asignar un supervisor a un cultivo o para formularios donde se necesita elegir solo supervisores.
        List<Map<String, String>> lista = new ArrayList<>(); // Crea la lista vacía para almacenar los supervisores.
        String sql = "SELECT u.id, u.nombre FROM usuarios u " + // Selecciona ID y nombre de la tabla 'usuarios'.
                     "JOIN roles_usuarios ru ON u.id = ru.usuario_id " + // JOIN con la tabla intermedia de roles.
                     "JOIN roles r ON ru.rol_id = r.id " + // JOIN con la tabla 'roles' para acceder al nombre del rol.
                     "WHERE r.nombre = 'supervisor' AND u.estado = 'Activo'"; // Filtra EXCLUSIVAMENTE por rol 'supervisor' (no incluye trabajadores) y que estén activos.
        try (Connection conn = Conexion.getConnection(); // TRY-WITH-RESOURCES: abre la conexión y la cierra automáticamente.
             PreparedStatement ps = conn.prepareStatement(sql); // Prepara la sentencia SQL.
             ResultSet rs = ps.executeQuery()) { // Ejecuta la consulta. Los tres recursos se cierran automáticamente.
            while (rs.next()) { // Itera sobre cada supervisor activo encontrado.
                Map<String, String> m = new HashMap<>(); // Crea un Map ligero para cada supervisor.
                m.put("id", String.valueOf(rs.getInt("id"))); // Extrae el ID del supervisor y lo convierte a String.
                m.put("nombre", rs.getString("nombre")); // Extrae el nombre del supervisor.
                lista.add(m); // Agrega el Map del supervisor a la lista.
            }
        } catch (Exception e) { e.printStackTrace(); } // Captura y muestra cualquier excepción en consola.
        return lista; // Retorna la lista de supervisores activos.
    }

    // ==================================================================================
    // MÉTODO PÚBLICO: listarSoloTrabajadores
    // Recibe: nada (sin parámetros)
    // Devuelve: una Lista de Mapas con {id, nombre} de usuarios con rol exclusivamente 'trabajador' y estado 'Activo'
    // Dirección: no recibe datos → consulta la BD → devuelve la lista al Servlet para dropdowns de asignación de tareas
    // ==================================================================================
    public List<Map<String, String>> listarSoloTrabajadores() { // Método que obtiene ÚNICAMENTE los trabajadores activos. Se usa en formularios donde se asignan tareas específicamente a trabajadores (no supervisores).
        List<Map<String, String>> lista = new ArrayList<>(); // Crea la lista vacía para almacenar los trabajadores.
        String sql = "SELECT u.id, u.nombre FROM usuarios u " + // Selecciona ID y nombre de la tabla 'usuarios'.
                     "JOIN roles_usuarios ru ON u.id = ru.usuario_id " + // JOIN con la tabla intermedia de roles.
                     "JOIN roles r ON ru.rol_id = r.id " + // JOIN con la tabla 'roles' para filtrar por nombre de rol.
                     "WHERE r.nombre = 'trabajador' AND u.estado = 'Activo'"; // Filtra EXCLUSIVAMENTE por rol 'trabajador' (no incluye supervisores) y que estén activos en el sistema.
        try (Connection conn = Conexion.getConnection(); // TRY-WITH-RESOURCES: abre la conexión automáticamente gestionada.
             PreparedStatement ps = conn.prepareStatement(sql); // Prepara la sentencia SQL.
             ResultSet rs = ps.executeQuery()) { // Ejecuta la consulta. Los tres recursos se cierran automáticamente al finalizar el bloque.
            while (rs.next()) { // Itera sobre cada trabajador activo encontrado.
                Map<String, String> m = new HashMap<>(); // Crea un Map para cada trabajador con ID y nombre.
                m.put("id", String.valueOf(rs.getInt("id"))); // Extrae el ID del trabajador y lo convierte a String.
                m.put("nombre", rs.getString("nombre")); // Extrae el nombre del trabajador.
                lista.add(m); // Agrega el Map del trabajador a la lista.
            }
        } catch (Exception e) { e.printStackTrace(); } // Captura y muestra cualquier excepción en consola.
        return lista; // Retorna la lista de trabajadores activos.
    }

    // ==================================================================================
    // MÉTODO PÚBLICO: listarId
    // Recibe: el ID del usuario como entero (int id)
    // Devuelve: un objeto Usuario completo con todos sus datos, o un Usuario vacío si no se encuentra
    // Dirección: recibe el ID del Servlet → consulta la BD con JOINs → devuelve el objeto Usuario al Servlet o JSP
    // ==================================================================================
    public Usuario listarId(int id) { // Método que recupera todos los datos de un usuario como un objeto tipado 'Usuario'. A diferencia de 'obtenerPorId', retorna un POJO en lugar de un Map, lo que es más seguro y conveniente en ciertos contextos.
        Usuario user = new Usuario(); // Crea un objeto Usuario vacío con valores por defecto. Si el ID no se encuentra, se retorna este objeto vacío.
        Connection conn = null; // Declara la conexión fuera del try para cerrarla en el finally.
        PreparedStatement ps = null; // Declara la sentencia fuera del try para cerrarla en el finally.
        ResultSet rs = null; // Declara el cursor fuera del try para cerrarlo en el finally.

        String sql = "SELECT u.*, c.email, t.numero AS telefono, f.ruta AS foto " + // SELECT con comodín: trae todas las columnas de 'usuarios' más el email, teléfono (alias) y ruta de foto (alias) de las tablas relacionadas.
                     "FROM usuarios u " + // Tabla principal de usuarios.
                     "LEFT JOIN correo c ON c.usuario_id = u.id " + // LEFT JOIN: incluye el usuario aunque no tenga email registrado.
                     "LEFT JOIN telefono t ON t.usuario_id = u.id " + // LEFT JOIN: incluye el usuario aunque no tenga teléfono registrado.
                     "LEFT JOIN fotos_usuario f ON f.usuario_id = u.id " + // LEFT JOIN: incluye el usuario aunque no tenga foto registrada.
                     "WHERE u.id = ?"; // Filtra por el ID específico del usuario a buscar.

        try { // Inicia el bloque de consulta.
            conn = Conexion.getConnection(); // Obtiene la conexión activa.
            ps = conn.prepareStatement(sql); // Prepara la sentencia SQL con los JOINs.
            ps.setInt(1, id); // Asigna el ID entero al primer '?' de la consulta WHERE.
            rs = ps.executeQuery(); // Ejecuta la consulta. Debería retornar máximo una fila.

            if (rs.next()) { // Si se encontró el usuario con ese ID...
                user.setId(rs.getInt("id")); // Asigna el ID del usuario al objeto usando el setter del POJO.
                user.setNombre(rs.getString("nombre")); // Asigna el nombre del usuario al objeto.
                user.setDocumento(rs.getString("documento")); // Asigna el número de documento al objeto.
                user.setDireccion(rs.getString("direccion")); // Asigna la dirección al objeto.
                user.setCorreo(rs.getString("email")); // Asigna el email (columna 'email' del JOIN con 'correo') al campo 'correo' del objeto Usuario.
                user.setTelefono(rs.getString("telefono")); // Asigna el teléfono (alias del JOIN con 'telefono') al objeto.
                user.setFoto(rs.getString("foto")); // Asigna la ruta de la foto (alias del JOIN con 'fotos_usuario') al objeto. Si no tiene foto, será null.
            }
        } catch (Exception e) { // Bloque catch vacío: si hay un error, el objeto 'user' permanecerá con sus valores por defecto y se retornará sin lanzar excepción al llamador. En producción, sería ideal al menos loggear el error.
        } finally { // Siempre se ejecuta para liberar recursos.
            cerrar(rs, ps, conn); // Cierra el ResultSet, PreparedStatement y Connection en el orden correcto.
        }
        return user; // Retorna el objeto Usuario completo con todos sus datos, o el objeto vacío si no se encontró el ID o hubo un error.
    }
}