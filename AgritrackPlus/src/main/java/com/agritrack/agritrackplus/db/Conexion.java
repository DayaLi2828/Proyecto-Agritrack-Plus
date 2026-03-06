package com.agritrack.agritrackplus.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    // Verifica que tu base de datos en MySQL se llame exactamente "AgritrackPlus"
    private static final String DB = "jdbc:mysql://localhost:3306/AgritrackPlus?serverTimezone=UTC&useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Si en tu MySQL pusiste clave, escríbela aquí

    public static Connection getConexion() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            // Convertimos el error de Driver en un error de SQL para no dañar los DAOs
            throw new SQLException("Error: Driver MySQL no encontrado.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return getConnection(); 
    }
}