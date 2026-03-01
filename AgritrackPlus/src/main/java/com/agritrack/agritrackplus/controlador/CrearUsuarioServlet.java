package com.agritrack.agritrackplus.controlador;

import com.agritrack.agritrackplus.DAO.UsuarioDAO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.net.URLEncoder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet(name = "CrearUsuarioServlet", urlPatterns = {"/CrearUsuarioServlet"})
@MultipartConfig
public class CrearUsuarioServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        UsuarioDAO dao = new UsuarioDAO();
        boolean hayErrores = false;

        // 1. OBTENER DATOS DEL FORMULARIO
        String nombre = request.getParameter("nombre");
        String pass = request.getParameter("pass");
        String documento = request.getParameter("documento");
        String direccion = request.getParameter("direccion");
        String estado = "Activo"; // Valor por defecto
        String correo = request.getParameter("correo");
        String telefono = request.getParameter("telefono");
        String rolIdStr = request.getParameter("rol_id");

        // 2. VALIDACIONES CLIENT-SERVER CON PARÁMETROS GET
        // Validación de Nombre (Sin números)
        if (nombre == null || nombre.trim().isEmpty() || nombre.matches(".*\\d.*")) {
            response.sendRedirect(request.getContextPath() + 
                "/public/Administrador/Agregar_Usuario.jsp?" +
                "nombre=" + URLEncoder.encode(nombre != null ? nombre : "", "UTF-8") +
                "&documento=" + URLEncoder.encode(documento != null ? documento : "", "UTF-8") +
                "&direccion=" + URLEncoder.encode(direccion != null ? direccion : "", "UTF-8") +
                "&correo=" + URLEncoder.encode(correo != null ? correo : "", "UTF-8") +
                "&telefono=" + URLEncoder.encode(telefono != null ? telefono : "", "UTF-8") +
                "&rol_id=" + URLEncoder.encode(rolIdStr != null ? rolIdStr : "", "UTF-8") +
                "&pass=" + URLEncoder.encode(pass != null ? pass : "", "UTF-8") +
                "&error_nombre=true");
            return;
        }

        // Validación de Documento (Exactamente 10 dígitos)
        if (documento == null || !documento.matches("\\d{10}") || documento.length() != 10) {
            redirigirConErrores(request, response, "error_doc", nombre, documento, direccion, correo, telefono, rolIdStr, pass);
            return;
        } else if (dao.existeDocumento(documento)) {
            redirigirConErrores(request, response, "error_duplicado", nombre, documento, direccion, correo, telefono, rolIdStr, pass);
            return;
        }

        // Validación de Correo
        if (correo == null || !correo.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            redirigirConErrores(request, response, "error_correo", nombre, documento, direccion, correo, telefono, rolIdStr, pass);
            return;
        } else if (dao.existeCorreo(correo)) {
            redirigirConErrores(request, response, "error_duplicado", nombre, documento, direccion, correo, telefono, rolIdStr, pass);
            return;
        }

        // Validación de Teléfono (Exactamente 10 dígitos)
        if (telefono == null || !telefono.matches("\\d{10}")) {
            redirigirConErrores(request, response, "error_tel", nombre, documento, direccion, correo, telefono, rolIdStr, pass);
            return;
        }

        // Validación de Contraseña
        if (pass == null || pass.length() < 6) {
            redirigirConErrores(request, response, "error_pass", nombre, documento, direccion, correo, telefono, rolIdStr, pass);
            return;
        }

        // Validación de Rol
        if (rolIdStr == null || rolIdStr.trim().isEmpty() || (!"2".equals(rolIdStr) && !"3".equals(rolIdStr))) {
            redirigirConErrores(request, response, "error_rol", nombre, documento, direccion, correo, telefono, rolIdStr, pass);
            return;
        }

        // 3. TODAS VALIDACIONES OK - PROCESAR FOTO
        int rolId = Integer.parseInt(rolIdStr);
        String nombreFoto = "asset/imagenes/default-avatar.png";

        try {
            Part fotoPart = request.getPart("foto");
            if (fotoPart != null && fotoPart.getSize() > 0) {
                String fileName = Paths.get(fotoPart.getSubmittedFileName()).getFileName().toString();
                String uploadPath = getServletContext().getRealPath("") + File.separator + "asset" + File.separator + "imagenes" + File.separator + "trabajadores" + File.separator;
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdirs();
                
                fotoPart.write(uploadPath + fileName);
                nombreFoto = "asset/imagenes/trabajadores/" + fileName;
            }
        } catch (Exception e) {
            System.err.println("Error al subir foto: " + e.getMessage());
        }

        // 4. GUARDAR EN BD
        boolean exito = dao.crear(nombre.trim(), pass, documento, direccion.trim(), estado, correo.trim(), telefono, rolId, nombreFoto);

        if (exito) {
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Usuarios.jsp?registro=exitoso");
        } else {
            redirigirConErrores(request, response, "error_duplicado", nombre, documento, direccion, correo, telefono, rolIdStr, pass);
        }
    }

    // ✅ MÉTODO AUXILIAR PARA REDIRIGIR CON ERRORES Y MANTENER DATOS
    private void redirigirConErrores(HttpServletRequest request, HttpServletResponse response, 
                                   String tipoError, String nombre, String documento, String direccion, 
                                   String correo, String telefono, String rolIdStr, String pass) throws IOException {
        response.sendRedirect(request.getContextPath() + 
            "/public/Administrador/Agregar_Usuario.jsp?" +
            "nombre=" + URLEncoder.encode(nombre != null ? nombre : "", "UTF-8") +
            "&documento=" + URLEncoder.encode(documento != null ? documento : "", "UTF-8") +
            "&direccion=" + URLEncoder.encode(direccion != null ? direccion : "", "UTF-8") +
            "&correo=" + URLEncoder.encode(correo != null ? correo : "", "UTF-8") +
            "&telefono=" + URLEncoder.encode(telefono != null ? telefono : "", "UTF-8") +
            "&rol_id=" + URLEncoder.encode(rolIdStr != null ? rolIdStr : "", "UTF-8") +
            "&pass=" + URLEncoder.encode(pass != null ? pass : "", "UTF-8") +
            "&" + tipoError + "=true");
    }
}
