package com.agritrack.agritrackplus.db;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        System.out.println("Intentando conectar...");
        try (Connection conn = Conexion.getConnection()) {
            System.out.println("Conexión exitosa");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

