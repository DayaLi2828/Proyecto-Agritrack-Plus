package com.agritrack.agritrackplus.db; 
// Declara que esta clase pertenece al paquete 'db' dentro del proyecto 'AgritrackPlus', que puede contener clases relacionadas con la base de datos.

import java.sql.Connection; 
// Importa la interfaz Connection, que representa una conexión a una base de datos SQL.

public class Main { 
    // Define la clase 'Main', que contiene el método principal para ejecutar la aplicación.

    public static void main(String[] args) { 
        // Método principal que se ejecuta al iniciar la aplicación. Recibe un arreglo de argumentos de línea de comandos.

        System.out.println("Intentando conectar..."); 
        // Imprime un mensaje en la consola indicando que se está intentando establecer una conexión a la base de datos.

        try (Connection conn = Conexion.getConnection()) { 
            // Intenta obtener una conexión a la base de datos utilizando el método 'getConnection()' de la clase 'Conexion'.
            // El uso de 'try-with-resources' asegura que la conexión se cerrará automáticamente al finalizar el bloque.

            System.out.println("Conexión exitosa"); 
            // Si la conexión se establece correctamente, imprime un mensaje indicando que la conexión fue exitosa.
        } catch (Exception e) { 
            // Captura cualquier excepción que ocurra durante el intento de conexión o en el bloque try.

            e.printStackTrace(); 
            // Imprime el seguimiento de la pila de la excepción en la consola, lo que ayuda a diagnosticar el error que ocurrió.
        }
    }
}