package com.agritrack.agritrackplus.controlador;

import com.agritrack.agritrackplus.DAO.UsuarioDAO;
import java.io.File;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet(name = "ActualizarPerfilServlet", urlPatterns = {"/ActualizarPerfilServlet"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2, // 2MB
    maxFileSize = 1024 * 1024 * 10,      // 10MB
    maxRequestSize = 1024 * 1024 * 50    // 50MB
)
public class ActualizarPerfilServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Integer usuarioId = (Integer) session.getAttribute("usuario_id"); 

        if (usuarioId == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        // 1. Capturar parámetros del formulario
        String nombre = request.getParameter("txtNombre");
        String correo = request.getParameter("txtCorreo");
        String telefono = request.getParameter("txtTelefono");
        String direccion = request.getParameter("txtDireccion");
        String nuevaPass = request.getParameter("txtPassword");
        
        // El documento es necesario para tu método DAO actual
        // Si no lo tienes en el JSP como input, podrías obtenerlo de la sesión o enviarlo oculto
        String documento = request.getParameter("documento"); 

        // 2. Procesar la Foto (Guardado físico)
        Part filePart = request.getPart("fotoPerfil"); 
        String nombreArchivoFinal = null;

        if (filePart != null && filePart.getSize() > 0) {
            // Creamos un nombre único para evitar conflictos (ej: perfil_4.jpg)
            nombreArchivoFinal = "perfil_" + usuarioId + ".jpg";
            
            // Definimos la ruta de la carpeta "imagenes" dentro de "asset"
            // getRealPath extrae la ruta física en el servidor (donde vive tu proyecto desplegado)
            String uploadPath = getServletContext().getRealPath("") + File.separator + "asset" + File.separator + "imagenes";
            
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs(); // Crea la carpeta si no existe
            }

            // Escribimos el archivo en el disco
            filePart.write(uploadPath + File.separator + nombreArchivoFinal);
        }

        UsuarioDAO dao = new UsuarioDAO();
        
        // 3. Llamar al DAO
        // Enviamos 'nombreArchivoFinal' que contiene solo el nombre (ej: "perfil_4.jpg")
        boolean exito = dao.actualizarPerfil(usuarioId, nombre, documento, direccion, nuevaPass, correo, telefono, nombreArchivoFinal);

        if (exito) {
            session.setAttribute("usuario_nombre", nombre);
            response.sendRedirect(request.getContextPath() + "/public/Supervisor/Supervisor.jsp?update=success");
        } else {
            response.sendRedirect(request.getContextPath() + "/public/Supervisor/Supervisor.jsp?update=error");
        }
    }
}