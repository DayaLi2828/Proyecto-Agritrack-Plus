package com.agritrack.agritrackplus.controlador;

import com.agritrack.agritrackplus.DAO.UsuarioDAO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
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

        // 1. OBTENER DATOS
        String nombre = request.getParameter("nombre");
        String pass = request.getParameter("pass");
        String documento = request.getParameter("documento");
        String direccion = request.getParameter("direccion");
        String estado = request.getParameter("estado");
        String correo = request.getParameter("correo");
        String telefono = request.getParameter("telefono");

        // Validar rol_id para evitar NumberFormatException
        String rolIdStr = request.getParameter("rol_id");
        int rolId = 0;
        if (rolIdStr != null && !rolIdStr.isEmpty()) {
            try {
                rolId = Integer.parseInt(rolIdStr);
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Usuario.jsp?error=rol_invalido");
                return;
            }
        }

        // 2. VALIDACIONES FORMATO ✅ NUEVAS
        if (nombre == null || nombre.trim().isEmpty() || nombre.length() < 2 || nombre.length() > 50) {
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Usuario.jsp?error=nombre_invalido");
            return;
        }

        if (pass == null || pass.length() < 6) {
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Usuario.jsp?error=pass_corto");
            return;
        }

        // ✅ DOCUMENTO SOLO NÚMEROS
        if (documento == null || !documento.matches("\\d+") || documento.length() < 6 || documento.length() > 15) {
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Usuario.jsp?error=documento_invalido");
            return;
        }

        if (direccion == null || direccion.trim().isEmpty() || direccion.length() > 200) {
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Usuario.jsp?error=direccion_invalida");
            return;
        }

        // ✅ CORREO FORMATO VÁLIDO
        if (correo == null || !correo.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Usuario.jsp?error=correo_invalido");
            return;
        }

        // ✅ TELÉFONO SOLO NÚMEROS (10 dígitos)
        if (telefono == null || !telefono.matches("\\d+") || telefono.length() != 10) {
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Usuario.jsp?error=telefono_invalido");
            return;
        }

        if (rolId <= 0) {
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Usuario.jsp?error=rol_obligatorio");
            return;
        }

        // 3. Manejo de la foto
        String nombreFoto = null;
        try {
            Part fotoPart = request.getPart("foto");
            if (fotoPart != null && fotoPart.getSize() > 0) {
                String nombreArchivo = Paths.get(fotoPart.getSubmittedFileName()).getFileName().toString();

                // Carpeta donde se guardarán las fotos
                String uploadPath = getServletContext().getRealPath("") + "asset/imagenes/trabajadores/";
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdirs();

                // Guardar archivo en disco
                fotoPart.write(uploadPath + nombreArchivo);

                // Guardar solo el nombre del archivo en BD
                nombreFoto = "asset/imagenes/trabajadores/" + nombreArchivo;
            }
        } catch (Exception e) {
            System.err.println("Error foto: " + e.getMessage());
            nombreFoto = null; // Continúa sin foto
        }

        // 4. GUARDAR USUARIO (DAO valida duplicados)
        UsuarioDAO dao = new UsuarioDAO();
        boolean exito = dao.crear(nombre, pass, documento, direccion, estado, correo, telefono, rolId, nombreFoto);

        if (exito) {
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Usuarios.jsp?registro=exitoso");
        } else {
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Usuario.jsp?error=duplicado");
        }
    }
}
