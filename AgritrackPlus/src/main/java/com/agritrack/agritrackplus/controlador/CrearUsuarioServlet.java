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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        UsuarioDAO dao = new UsuarioDAO();

        // Recepción de parámetros
        String nombre = request.getParameter("nombre");
        String pass = request.getParameter("pass");
        String documento = request.getParameter("documento");
        String direccion = request.getParameter("direccion");
        String correo = request.getParameter("correo");
        String telefono = request.getParameter("telefono");
        String rolIdStr = request.getParameter("rol_id");

        // --- VALIDACIÓN 1: NOMBRE (Solo letras y espacios) ---
        // Expresión regular: permite letras (con tildes y ñ) y espacios. No permite números.
        if (nombre == null || !nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            redirigirConErrores(request, response, "error_nombre", nombre, documento, direccion, correo, telefono, rolIdStr, pass);
            return;
        }

        // --- VALIDACIÓN 2: ROL ---
        int rolId;
        try {
            rolId = (rolIdStr != null && !rolIdStr.isEmpty()) ? Integer.parseInt(rolIdStr) : 2;
        } catch (NumberFormatException e) {
            rolId = 2;
        }

        // --- VALIDACIÓN 3: CONTRASEÑA ---
        if (pass == null || pass.trim().length() < 6) {
            redirigirConErrores(request, response, "error_pass", nombre, documento, direccion, correo, telefono, rolIdStr, pass);
            return;
        }

        // --- VALIDACIÓN 4: DUPLICADOS ---
        if (dao.existeDocumento(documento)) {
            redirigirConErrores(request, response, "error_duplicado", nombre, documento, direccion, correo, telefono, rolIdStr, pass);
            return;
        }

        // --- PROCESAMIENTO DE FOTO ---
        String nombreFoto = "asset/imagenes/default-avatar.png";
        try {
            Part fotoPart = request.getPart("foto");
            if (fotoPart != null && fotoPart.getSize() > 0) {
                String fileName = System.currentTimeMillis() + "_" + Paths.get(fotoPart.getSubmittedFileName()).getFileName().toString();
                String uploadPath = getServletContext().getRealPath("/") + "asset/imagenes/trabajadores/";
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdirs();
                fotoPart.write(uploadPath + fileName);
                nombreFoto = "asset/imagenes/trabajadores/" + fileName;
            }
        } catch (Exception e) {
            System.out.println("Error procesando foto: " + e.getMessage());
        }

                // --- PERSISTENCIA EN BD ---
        boolean exito = dao.crear(
            nombre.trim(), 
            pass, 
            documento.trim(), 
            direccion, 
            correo,      // El correo ahora es el 5to parámetro
            telefono,    // El teléfono el 6to
            rolId,       // El rol el 7mo
            nombreFoto   // La foto el 8vo
        );
        if (exito) {
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Usuarios.jsp?mensaje=creado");
        } else {
            redirigirConErrores(request, response, "error_db", nombre, documento, direccion, correo, telefono, rolIdStr, pass);
        }
    }

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
            "&rol_id=" + URLEncoder.encode(rolIdStr != null ? rolIdStr : "2", "UTF-8") +
            "&pass=" + URLEncoder.encode(pass != null ? pass : "", "UTF-8") +
            "&" + tipoError + "=true");
    }
}