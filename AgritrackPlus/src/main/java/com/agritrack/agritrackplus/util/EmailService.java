package com.agritrack.agritrackplus.util;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;


public class EmailService {
    
    //  CAMBIA ESTOS 2 VALORES CON TUS DATOS
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_REMITENTE = "tu_correo@gmail.com";  // ← TU GMAIL
    private static final String PASSWORD_APP = "abcd efgh ijkl mnop";     // ← App Password
    
    public static boolean enviarRegistroExitoso(String correoDestino, String nombreUsuario) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_REMITENTE, PASSWORD_APP);
            }
        });
        
        try {
            Message mensaje = new MimeMessage(session);
            mensaje.setFrom(new InternetAddress(EMAIL_REMITENTE, "🌱 AgriTrack Plus"));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correoDestino));
            mensaje.setSubject("✅ ¡Bienvenido a AgriTrack Plus!");
            
            // HTML EMAIL PROFESIONAL
            String htmlBody = "<html><body style='font-family: Arial, sans-serif;'>" +
            "<h2>¡Hola " + nombreUsuario + "!</h2>" +
            "<p>Tu cuenta ha sido <strong>creada exitosamente</strong> en AgriTrack Plus.</p>" +
            "<p><strong>Correo:</strong> " + correoDestino + "</p>" +
            "<p>Ingresa en: <a href='http://localhost:8080/AgritrackPlus'>AgriTrack Plus</a></p>" +
            "</body></html>";
            
            mensaje.setContent(htmlBody, "text/html; charset=UTF-8");
            Transport.send(mensaje);
            System.out.println(" EMAIL ENVIADO a: " + correoDestino);
            return true;
            
        } catch (Exception e) {
            System.err.println(" ERROR email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
