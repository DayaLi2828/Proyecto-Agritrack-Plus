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
            rolId = Integer.parseInt(rolIdStr);
        }

        // Manejo de la foto
        String nombreFoto = null;
        Part fotoPart = request.getPart("foto");
        if (fotoPart != null && fotoPart.getSize() > 0) {
            String nombreArchivo = Paths.get(fotoPart.getSubmittedFileName()).getFileName().toString();

            // Carpeta donde se guardarán las fotos (ajusta según tu proyecto)
            String uploadPath = getServletContext().getRealPath("") + "asset/imagenes/trabajadores/";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            // Guardar archivo en disco
            fotoPart.write(uploadPath + nombreArchivo);

            // Guardar solo el nombre del archivo en BD
            nombreFoto = "asset/imagenes/trabajadores/" + nombreArchivo;
        }

        UsuarioDAO dao = new UsuarioDAO();
        boolean exito = dao.crear(nombre, pass, documento, direccion, estado, correo, telefono, rolId, nombreFoto);

        if (exito) {
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Usuarios.jsp?registro=exitoso");
        } else {
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Usuario.jsp?error=true");
        }
    }
}
