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
        
        // 1. Validar sesión
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario_id") == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        Integer usuarioId = (Integer) session.getAttribute("usuario_id"); 
        String rolUsuario = (String) session.getAttribute("rol"); 

        // 2. Capturar parámetros del formulario
        String nombre = request.getParameter("txtNombre");
        String correo = request.getParameter("txtCorreo");
        String telefono = request.getParameter("txtTelefono");
        String direccion = request.getParameter("txtDireccion");
        String nuevaPass = request.getParameter("txtPassword");
        
        // Es vital que en los JSP tengas el documento como hidden: <input type="hidden" name="txtDocumento" ...>
        String documento = request.getParameter("txtDocumento"); 

        // 3. Procesar la Foto con nombre único (evita problemas de caché)
        Part filePart = request.getPart("fotoPerfil"); 
        String nombreArchivoFinal = null;

        if (filePart != null && filePart.getSize() > 0) {
            // Nombre: perfil_ID_MarcaDeTiempo.jpg
            nombreArchivoFinal = "perfil_" + usuarioId + "_" + System.currentTimeMillis() + ".jpg";
            
            // Ruta física en el servidor: /public/asset/imagenes
            String uploadPath = getServletContext().getRealPath("") + File.separator + "asset" + File.separator + "imagenes";
            
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            filePart.write(uploadPath + File.separator + nombreArchivoFinal);
        }

        // 4. Ejecutar actualización en la Base de Datos
        UsuarioDAO dao = new UsuarioDAO();
        boolean exito = dao.actualizarPerfil(usuarioId, nombre, documento, direccion, nuevaPass, correo, telefono, nombreArchivoFinal);

        // 5. DETERMINAR RUTA DE REGRESO (Redirección Dinámica)
        String destino = "Supervisor/Supervisor.jsp"; // Por defecto

        if (rolUsuario != null) {
            if (rolUsuario.equalsIgnoreCase("administrador")) {
                destino = "Administrador/Admin.jsp";
            } else if (rolUsuario.equalsIgnoreCase("trabajador")) {
                destino = "Trabajador/Trabajador.jsp";
            }
        }

        // 6. Respuesta final
        if (exito) {
            session.setAttribute("usuario_nombre", nombre);
            response.sendRedirect(request.getContextPath() + "/public/" + destino + "?update=success");
        } else {
            response.sendRedirect(request.getContextPath() + "/public/" + destino + "?update=error");
        }
    }
}