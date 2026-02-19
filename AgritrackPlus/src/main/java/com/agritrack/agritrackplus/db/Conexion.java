package com.agritrack.agritrackplus.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static final String DB = "jdbc:mysql://localhost:3306/AgritrackPlus";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        // En aplicaciones web, es vital cargar el driver manualmente
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(DB, USER, PASSWORD);
    }
}