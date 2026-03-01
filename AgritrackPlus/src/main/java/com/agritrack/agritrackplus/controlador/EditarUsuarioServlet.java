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

@WebServlet(name = "EditarUsuarioServlet", urlPatterns = {"/EditarUsuarioServlet"})
@MultipartConfig
public class EditarUsuarioServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // Obtener parámetros del formulario
        String id = request.getParameter("id");
        String nombre = request.getParameter("nombre");
        String documento = request.getParameter("documento");
        String direccion = request.getParameter("direccion");
        String correo = request.getParameter("correo");
        String telefono = request.getParameter("telefono");
        String pass = request.getParameter("pass");
        String rolId = request.getParameter("rol_id");
        String estado = request.getParameter("estado");

        // Manejo de la foto
        String nombreFoto = null;
        Part fotoPart = request.getPart("foto");
        if (fotoPart != null && fotoPart.getSize() > 0) {
            String nombreArchivo = Paths.get(fotoPart.getSubmittedFileName()).getFileName().toString();

            // Carpeta donde se guardarán las fotos
            String uploadPath = getServletContext().getRealPath("") + "asset/imagenes/trabajadores/";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            // Guardar archivo en disco
            fotoPart.write(uploadPath + nombreArchivo);

            // Ruta relativa para BD
            nombreFoto = "asset/imagenes/trabajadores/" + nombreArchivo;
        }

        UsuarioDAO dao = new UsuarioDAO();
        boolean exito = dao.editarUsuario(id, nombre, documento, direccion, correo, telefono, pass, rolId, estado,nombreFoto);

        // Si hay nueva foto, actualizarla en la tabla fotos_usuario
        if (exito && nombreFoto != null) {
            try {
                // Eliminar foto anterior y guardar la nueva
                java.sql.Connection conn = com.agritrack.agritrackplus.db.Conexion.getConnection();
                java.sql.PreparedStatement ps = conn.prepareStatement(
                    "UPDATE fotos_usuario SET ruta=? WHERE usuario_id=?"
                );
                ps.setString(1, nombreFoto);
                ps.setInt(2, Integer.parseInt(id));
                int filas = ps.executeUpdate();
                ps.close();

                // Si no existía foto, insertar nueva
                if (filas == 0) {
                    java.sql.PreparedStatement psInsert = conn.prepareStatement(
                        "INSERT INTO fotos_usuario (usuario_id, ruta) VALUES (?, ?)"
                    );
                    psInsert.setInt(1, Integer.parseInt(id));
                    psInsert.setString(2, nombreFoto);
                    psInsert.executeUpdate();
                    psInsert.close();
                }
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (exito) {
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Usuarios.jsp?edicion=exitosa");
        } else {
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Usuario.jsp?id=" + id + "&error=true");
        }
    }
}
