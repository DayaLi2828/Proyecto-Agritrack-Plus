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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        UsuarioDAO dao = new UsuarioDAO();

        String nombre = request.getParameter("nombre");
        String pass = request.getParameter("pass");
        String documento = request.getParameter("documento");
        String direccion = request.getParameter("direccion");
        String correo = request.getParameter("correo");
        String telefono = request.getParameter("telefono");
        String rolStr = request.getParameter("rol");  // ✅ CAMBIO: "Trabajador" o "Administrador"

        // ✅ CORRECCIÓN: Mapear STRING ROL → INT rolId
        int rolId;
        if ("Trabajador".equals(rolStr)) {
            rolId = 2;  //  ROL TRABAJADOR = ID 2
        } else {
            rolId = 1;  //  ROL ADMIN = ID 1
        }
        
        System.out.println(" DEBUG - Rol recibido: " + rolStr + " → rolId: " + rolId);

        // VALIDACIÓN DE CONTRASEÑA (El motivo por el cual no creaba antes)
        if (pass == null || pass.trim().length() < 6) {
            redirigirConErrores(request, response, "error_pass", nombre, documento, direccion, correo, telefono, rolStr, pass);
            return;
        }

        // Validaciones de negocio (simplificadas para el ejemplo)
        if (dao.existeDocumento(documento)) {
            redirigirConErrores(request, response, "error_duplicado", nombre, documento, direccion, correo, telefono, rolStr, pass);
            return;
        }

        String nombreFoto = "asset/imagenes/default-avatar.png";

        // PROCESAR FOTO
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
            System.out.println(" Error foto: " + e.getMessage()); 
        }

        boolean exito = dao.crear(nombre, pass, documento, direccion, "Activo", correo, telefono, rolId, nombreFoto);

        System.out.println(" USUARIO CREADO: " + exito + " con rolId=" + rolId);

        if (exito) {
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Usuarios.jsp?mensaje=creado");
        } else {
            redirigirConErrores(request, response, "error_db", nombre, documento, direccion, correo, telefono, rolStr, pass);
        }
    }

    // ✅ MÉTODO AUXILIAR PARA REDIRIGIR CON ERRORES Y MANTENER DATOS
    private void redirigirConErrores(HttpServletRequest request, HttpServletResponse response, 
                                   String tipoError, String nombre, String documento, String direccion, 
                                   String correo, String telefono, String rolStr, String pass) throws IOException {
        response.sendRedirect(request.getContextPath() + 
            "/public/Administrador/Agregar_Usuario.jsp?" +
            "nombre=" + URLEncoder.encode(nombre != null ? nombre : "", "UTF-8") +
            "&documento=" + URLEncoder.encode(documento != null ? documento : "", "UTF-8") +
            "&direccion=" + URLEncoder.encode(direccion != null ? direccion : "", "UTF-8") +
            "&correo=" + URLEncoder.encode(correo != null ? correo : "", "UTF-8") +
            "&telefono=" + URLEncoder.encode(telefono != null ? telefono : "", "UTF-8") +
            "&rol=" + URLEncoder.encode(rolStr != null ? rolStr : "", "UTF-8") +
            "&pass=" + URLEncoder.encode(pass != null ? pass : "", "UTF-8") +
            "&" + tipoError + "=true");
    }
}
