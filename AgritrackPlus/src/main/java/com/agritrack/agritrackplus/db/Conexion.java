package com.agritrack.agritrackplus.db; // Declara que esta clase pertenece al paquete 'db' dentro del proyecto 'AgritrackPlus', que maneja la conexión a la base de datos.

import java.sql.Connection; // Importa la interfaz Connection, que representa una conexión a una base de datos SQL.

import java.sql.DriverManager; // Importa la clase DriverManager, que gestiona las conexiones a bases de datos.

import java.sql.SQLException; // Importa la clase SQLException, que maneja errores relacionados con la conexión a la base de datos y operaciones SQL.
/*
estiona la conexión a una base de datos MySQL llamada AgritrackPlus. 
Contiene una URL de conexión, un nombre de usuario y una contraseña para autenticar el acceso.
El método getConexion() intenta cargar el driver de MySQL y establece la conexión, manejando excepciones 
si el driver no se encuentra. Además, proporciona un método alternativo getConnection() que simplemente 
llama a getConexion(). En caso de errores, se lanzan excepciones adecuadas.
*/
public class Conexion { 
    // Define la clase 'Conexion', que contendrá métodos para establecer una conexión a la base de datos.

    // URL de conexión a la base de datos MySQL, que incluye el nombre de la base de datos y parámetros de configuración.
    private static final String DB = "jdbc:mysql://localhost:3306/AgritrackPlus?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true"; 
    // 'jdbc:mysql://' especifica el protocolo de conexión, 'localhost:3306' indica que la base de datos está en el mismo servidor en el puerto 3306, 
    // y 'AgritrackPlus' es el nombre de la base de datos. Los parámetros adicionales configuran el manejo de zona horaria, 
    // deshabilitan SSL y permiten la recuperación de claves públicas.

    private static final String USER = "agriplus"; 
    // Define el nombre de usuario para la conexión a la base de datos, en este caso 'agriplus'.

    private static final String PASSWORD = "#Aprendiz2024"; 
    // Define la contraseña asociada al usuario 'agriplus' para autenticar la conexión a la base de datos.

    public static Connection getConexion() throws SQLException { 
        // Método estático que devuelve una conexión a la base de datos. Puede lanzar una SQLException si ocurre un error.

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); 
            // Intenta cargar el driver de MySQL necesario para establecer la conexión. Si no se encuentra, lanzará una excepción.

            return DriverManager.getConnection(DB, USER, PASSWORD); 
            // Utiliza DriverManager para obtener una conexión a la base de datos usando la URL, usuario y contraseña definidos anteriormente.
        } catch (ClassNotFoundException e) {
            // Captura la excepción si el driver de MySQL no se encuentra en el classpath.

            throw new SQLException("Error: Driver MySQL no encontrado.", e); 
            // Lanza una SQLException con un mensaje personalizado, indicando que el driver no se pudo encontrar, junto con la excepción original.
        }
    }

    public static Connection getConnection() throws SQLException { 
        // Método estático que proporciona una forma alternativa de obtener una conexión a la base de datos. 
        // Llama al método getConexion() para obtener la conexión real.

        return getConexion(); 
        // Devuelve la conexión obtenida a través del método getConexion().
    }
}