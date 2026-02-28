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
        System.out.println("===  CREAR USUARIO INICIADO ===");

        // 1. OBTENER DATOS
        String nombre = request.getParameter("nombre");
        String pass = request.getParameter("pass");
        String documento = request.getParameter("documento");
        String direccion = request.getParameter("direccion");
        String estado = request.getParameter("estado");
        String correo = request.getParameter("correo");
        String telefono = request.getParameter("telefono");
        String rolIdStr = request.getParameter("rol_id");

        // LOGS DEBUG
        System.out.println(" nombre: '" + nombre + "'");
        System.out.println(" documento: '" + documento + "'");
        System.out.println(" correo: '" + correo + "'");
        System.out.println(" telefono: '" + telefono + "'");
        System.out.println(" rol_id: '" + rolIdStr + "'");

        // 2. VALIDAR ROL
        int rolId = 0;
        if (rolIdStr != null && !rolIdStr.trim().isEmpty()) {
            try {
                rolId = Integer.parseInt(rolIdStr);
                System.out.println(" Rol válido: " + rolId);
            } catch (NumberFormatException e) {
                System.err.println(" ERROR rol inválido: " + rolIdStr);
                response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Usuario.jsp?error=rol_invalido");
                return;
            }
        }

        // 3. VALIDACIONES CORREGIDAS 
        if (nombre == null || nombre.trim().isEmpty() || nombre.length() < 2 || nombre.length() > 50) {
            System.err.println(" ERROR nombre inválido");
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Usuario.jsp?error=nombre_invalido");
            return;
        }

        if (pass == null || pass.length() < 6) {
            System.err.println(" ERROR pass corta");
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Usuario.jsp?error=pass_corto");
            return;
        }

        // REGEX CORREGIDO - SOLO NÚMEROS
        if (documento == null || !documento.matches("\\d+") || documento.length() < 6 || documento.length() > 15) {
            System.err.println("ERROR documento inválido: '" + documento + "'");
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Usuario.jsp?error=documento_invalido");
            return;
        }

        if (direccion == null || direccion.trim().isEmpty() || direccion.length() > 200) {
            System.err.println(" ERROR dirección inválida");
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Usuario.jsp?error=direccion_invalida");
            return;
        }

        // ✅ REGEX CORREGIDO - CORREO
        if (correo == null || !correo.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
            System.err.println(" ERROR correo inválido: '" + correo + "'");
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Usuario.jsp?error=correo_invalido");
            return;
        }

        // ✅ REGEX CORREGIDO - TELÉFONO 10 DÍGITOS
        if (telefono == null || !telefono.matches("\\d+") || telefono.length() != 10) {
            System.err.println(" ERROR teléfono inválido: '" + telefono + "'");
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Usuario.jsp?error=telefono_invalido");
            return;
        }

        if (rolId <= 0) {
            System.err.println(" ERROR rol obligatorio");
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Usuario.jsp?error=rol_obligatorio");
            return;
        }

        // 4. FOTO (SIN CAMBIOS)
        String nombreFoto = null;
        try {
            Part fotoPart = request.getPart("foto");
            if (fotoPart != null && fotoPart.getSize() > 0) {
                String nombreArchivo = Paths.get(fotoPart.getSubmittedFileName()).getFileName().toString();
                String uploadPath = getServletContext().getRealPath("") + "asset/imagenes/trabajadores/";
                
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }
                fotoPart.write(uploadPath + nombreArchivo);
                nombreFoto = "asset/imagenes/trabajadores/" + nombreArchivo;
                System.out.println(" Foto guardada: " + nombreFoto);
            }
        } catch (Exception e) {
            System.err.println(" Sin foto: " + e.getMessage());
        }

        // 5. GUARDAR
        System.out.println("Guardando usuario...");
        UsuarioDAO dao = new UsuarioDAO();
        boolean exito = dao.crear(nombre, pass, documento, direccion, estado, correo, telefono, rolId, nombreFoto);

        System.out.println(" RESULTADO: " + exito);

        if (exito) {
            System.out.println(" USUARIO CREADO!");
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Usuarios.jsp?registro=exitoso");
        } else {
            System.err.println(" DAO.crear() FALLÓ");
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Usuario.jsp?error=duplicado");
        }
    }
}
