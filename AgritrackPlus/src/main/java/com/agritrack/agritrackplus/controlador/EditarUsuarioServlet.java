package com.agritrack.agritrackplus.controlador;

import com.agritrack.agritrackplus.DAO.UsuarioDAO;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Paths;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet(name = "EditarUsuarioServlet", urlPatterns = {"/EditarUsuarioServlet"})
@MultipartConfig
public class EditarUsuarioServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        UsuarioDAO dao = new UsuarioDAO();

        // 1. OBTENER DATOS DEL FORMULARIO
        String id = request.getParameter("id");
        String nombre = request.getParameter("nombre");
        String pass = request.getParameter("pass");
        String documento = request.getParameter("documento");
        String direccion = request.getParameter("direccion");
        String estado = "Activo"; // Por defecto activo en edición
        String correo = request.getParameter("correo");
        String telefono = request.getParameter("telefono");
        String rolIdStr = request.getParameter("rol_id");

        // 2. VALIDACIONES IGUALES AL CREAR (pero excluyendo correo duplicado del mismo usuario)
        int usuarioId = Integer.parseInt(id);

        // Validación de Nombre (Sin números)
        if (nombre == null || nombre.trim().isEmpty() || nombre.matches(".*\\d.*")) {
            redirigirConErrores(request, response, "error_nombre", id, nombre, documento, direccion, correo, telefono, rolIdStr, pass);
            return;
        }

        // Validación de Documento (Exactamente 10 dígitos)
        if (documento == null || !documento.matches("\\d{10}") || documento.length() != 10) {
            redirigirConErrores(request, response, "error_doc", id, nombre, documento, direccion, correo, telefono, rolIdStr, pass);
            return;
        } else if (dao.existeDocumento(documento) && !validaMismoUsuario(dao, documento, usuarioId)) {
            redirigirConErrores(request, response, "error_duplicado", id, nombre, documento, direccion, correo, telefono, rolIdStr, pass);
            return;
        }

        // Validación de Correo
        if (correo == null || !correo.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            redirigirConErrores(request, response, "error_correo", id, nombre, documento, direccion, correo, telefono, rolIdStr, pass);
            return;
        } else if (dao.existeCorreo(correo, usuarioId)) {
            redirigirConErrores(request, response, "error_duplicado", id, nombre, documento, direccion, correo, telefono, rolIdStr, pass);
            return;
        }

        // Validación de Teléfono (Exactamente 10 dígitos)
        if (telefono == null || !telefono.matches("\\d{10}")) {
            redirigirConErrores(request, response, "error_tel", id, nombre, documento, direccion, correo, telefono, rolIdStr, pass);
            return;
        }

        // Validación de Contraseña (solo si se proporciona nueva)
        if (pass != null && !pass.trim().isEmpty() && pass.length() < 6) {
            redirigirConErrores(request, response, "error_pass", id, nombre, documento, direccion, correo, telefono, rolIdStr, pass);
            return;
        }

        // Validación de Rol
        if (rolIdStr == null || rolIdStr.trim().isEmpty() || (!"2".equals(rolIdStr) && !"3".equals(rolIdStr))) {
            redirigirConErrores(request, response, "error_rol", id, nombre, documento, direccion, correo, telefono, rolIdStr, pass);
            return;
        }

        // 3. PROCESAR FOTO
        String nombreFoto = null;
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

        // 4. GUARDAR CAMBIOS
        boolean exito = dao.editarUsuario(id, nombre.trim(), documento, direccion.trim(), 
                                        correo.trim(), telefono, pass, rolIdStr, estado, nombreFoto);

        if (exito) {
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Usuarios.jsp?editado=exitoso");
        } else {
            redirigirConErrores(request, response, "error_duplicado", id, nombre, documento, direccion, correo, telefono, rolIdStr, pass);
        }
    }

    // ✅ MÉTODO PARA VERIFICAR SI EL DOCUMENTO PERTENECE AL MISMO USUARIO
    private boolean validaMismoUsuario(UsuarioDAO dao, String documento, int usuarioId) {
        try {
            // Obtener usuario por ID para comparar su documento
            Map<String, String> usuario = dao.obtenerPorId(String.valueOf(usuarioId));
            return usuario != null && documento.equals(usuario.get("documento"));
        } catch (Exception e) {
            return false;
        }
    }

    // ✅ MÉTODO AUXILIAR PARA REDIRIGIR CON ERRORES (IGUAL QUE CREAR)
    private void redirigirConErrores(HttpServletRequest request, HttpServletResponse response, 
                                   String tipoError, String id, String nombre, String documento, String direccion, 
                                   String correo, String telefono, String rolIdStr, String pass) throws IOException {
        response.sendRedirect(request.getContextPath() + 
            "/public/Administrador/Agregar_Usuario.jsp?" +
            "id=" + URLEncoder.encode(id != null ? id : "", "UTF-8") +
            "&nombre=" + URLEncoder.encode(nombre != null ? nombre : "", "UTF-8") +
            "&documento=" + URLEncoder.encode(documento != null ? documento : "", "UTF-8") +
            "&direccion=" + URLEncoder.encode(direccion != null ? direccion : "", "UTF-8") +
            "&correo=" + URLEncoder.encode(correo != null ? correo : "", "UTF-8") +
            "&telefono=" + URLEncoder.encode(telefono != null ? telefono : "", "UTF-8") +
            "&rol_id=" + URLEncoder.encode(rolIdStr != null ? rolIdStr : "", "UTF-8") +
            "&pass=" + URLEncoder.encode(pass != null ? pass : "", "UTF-8") +
            "&" + tipoError + "=true");
    }
}
