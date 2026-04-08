package com.agritrack.agritrackplus.DAO; 
import com.agritrack.agritrackplus.db.Conexion; 
import java.sql.*; 
import java.util.ArrayList; 
import java.util.HashMap; 
import java.util.List; 
import java.util.Map; 
public class CultivoDAO { 
// Clase DAO (Data Access Object) que encapsula toda la lógica de acceso a datos de la tabla "cultivos".
/*
Gestiona el acceso a datos de la tabla "cultivos" en una base de datos. Incluye métodos para listar cultivos, registrar un nuevo cultivo y contar cultivos según el rol 
del usuario (administrador o supervisor). Utiliza consultas SQL para interactuar con la base de datos y emplea técnicas de manejo de excepciones para gestionar errores.    
*/
    // MÉTODO PARA LISTAR CULTIVOS
    public List<Map<String, String>> listarCultivos() throws SQLException, ClassNotFoundException {
        // Crea una lista vacía que contendrá todos los cultivos en forma de mapas (clave-valor).
        List<Map<String, String>> lista = new ArrayList<>();
        
        // Sentencia SQL que selecciona columnas específicas de la tabla "cultivos".
        String sql = "SELECT id, nombre, fecha_siembra, fecha_cosecha, ciclo FROM cultivos ORDER BY id DESC";//ordenar de menor a mayor
        
        // try-with-resources: abre conexión y asegura que se cierre automáticamente al terminar.
        try (Connection conn = Conexion.getConnection(); // Obtiene conexión a la DB.
             PreparedStatement ps = conn.prepareStatement(sql); // Prepara la sentencia SQL.
             ResultSet rs = ps.executeQuery()) { // Ejecuta la consulta y guarda resultados en rs.

            // Itera sobre cada fila del ResultSet.
            while (rs.next()) {
                // Crea un mapa temporal para guardar los datos de un cultivo.
                Map<String, String> c = new HashMap<>();
                
                // Obtiene el valor de la columna "id" y lo convierte a String.
                c.put("id", String.valueOf(rs.getInt("id")));
                
                // Obtiene el valor de la columna "nombre".
                c.put("nombre", rs.getString("nombre"));
                
                // Obtiene el valor de la columna "fecha_siembra".
                c.put("fecha_siembra", rs.getString("fecha_siembra"));
                
                // Obtiene el valor de la columna "fecha_cosecha".
                String cosecha = rs.getString("fecha_cosecha");
                
                // Si la fecha de cosecha es NULL, se reemplaza por "Pendiente".
                c.put("fecha_cosecha", (cosecha == null) ? "Pendiente" : cosecha);
                
                // Obtiene el valor de la columna "ciclo".
                c.put("ciclo", rs.getString("ciclo"));
                
                // Agrega el mapa del cultivo a la lista general.
                lista.add(c);
            }
        } catch (SQLException e) {
            // Captura errores de SQL y los imprime en consola.
            e.printStackTrace();
        }
        // Devuelve la lista de cultivos (puede estar vacía si hubo error).
        return lista;
    }

    // MÉTODO PARA REGISTRAR UN NUEVO CULTIVO
    public boolean registrarCultivo(String nombre, String fechaSiembra, String ciclo) throws SQLException, ClassNotFoundException {
        // Sentencia SQL con parámetros (?) para evitar inyección SQL.
        String sql = "INSERT INTO cultivos (nombre, fecha_siembra, ciclo) VALUES (?, ?, ?)";//marcadores de posición para los valores que se insertarán en la consulta.
        
        try (Connection conn = Conexion.getConnection(); // Abre conexión.
             PreparedStatement ps = conn.prepareStatement(sql)) { // Prepara sentencia.

            // Inserta valores en los parámetros de la sentencia SQL.
            ps.setString(1, nombre);       // Primer parámetro: nombre del cultivo.
            ps.setString(2, fechaSiembra); // Segundo parámetro: fecha de siembra.
            ps.setString(3, ciclo);        // Tercer parámetro: ciclo del cultivo.
            
            // Ejecuta la sentencia INSERT. Devuelve número de filas afectadas.
            return ps.executeUpdate() > 0; // Si es mayor a 0, el registro fue exitoso.
        } catch (SQLException e) {
            // Captura errores de SQL y los imprime.
            e.printStackTrace();
            return false; // Devuelve false si hubo error.
        }
    }

    // MÉTODO PARA CONTAR CULTIVOS SEGÚN EL ROL DEL USUARIO
    public int contarCultivosPorRol(int idUsuario, String rol) {
        // Si el rol es administrador, cuenta todos los cultivos.
        // Si no, cuenta solo los cultivos asociados al usuario.
        String sql = "administrador".equalsIgnoreCase(rol) ? 
                     "SELECT COUNT(*) FROM cultivos" : 
                     "SELECT COUNT(*) FROM supervisor WHERE usuario_id = ?";
        
        try (Connection con = Conexion.getConnection(); // Abre conexión.
             PreparedStatement ps = con.prepareStatement(sql)) { // Prepara sentencia.

            // Si el rol no es administrador, se pasa el idUsuario como parámetro.
            if (!"administrador".equalsIgnoreCase(rol)) ps.setInt(1, idUsuario);
            
            // Ejecuta la consulta de conteo.
            try (ResultSet rs = ps.executeQuery()) {
                // Si hay resultado, obtiene el número de cultivos.
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) { 
            // Captura cualquier error y lo imprime.
            e.printStackTrace(); 
        }
        // Si falla, devuelve 0 para no romper la interfaz.
        return 0;
    }
}
