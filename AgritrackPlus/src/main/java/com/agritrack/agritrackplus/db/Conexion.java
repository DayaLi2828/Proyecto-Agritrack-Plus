package com.agritrack.agritrackplus.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    // Se añade allowPublicKeyRetrieval=true para permitir la autenticación segura de MySQL
    private static final String DB = "jdbc:mysql://localhost:3306/AgritrackPlus?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "agriplus";
    private static final String PASSWORD = "#Aprendiz2024";

    public static Connection getConexion() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Error: Driver MySQL no encontrado.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return getConexion(); 
    }
}