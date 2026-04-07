package com.agritrack.agritrackplus.controlador; // Paquete que contiene la clase del controlador del servlet

import com.agritrack.agritrackplus.DAO.UsuarioDAO; // Importa la clase UsuarioDAO para manejar operaciones relacionadas con usuarios
import java.io.File; // Importa la clase File para trabajar con archivos en el sistema
import java.io.IOException; // Importa la clase IOException para manejar errores de entrada/salida
import java.nio.file.Paths; // Importa la clase Paths para trabajar con rutas de archivos
import jakarta.servlet.ServletException; // Importa la clase ServletException para manejar errores en los servlets
import jakarta.servlet.annotation.MultipartConfig; // Importa la anotación MultipartConfig para permitir la carga de archivos
import jakarta.servlet.annotation.WebServlet; // Importa la anotación WebServlet para definir el servlet
import jakarta.servlet.http.HttpServlet; // Importa la clase HttpServlet, la cual es la clase base para los servlets HTTP
import jakarta.servlet.http.HttpServletRequest; // Importa la clase HttpServletRequest para manejar las solicitudes HTTP
import jakarta.servlet.http.HttpServletResponse; // Importa la clase HttpServletResponse para manejar las respuestas HTTP
import jakarta.servlet.http.Part; // Importa la clase Part para manejar las partes de una solicitud multipart
/*
maneja solicitudes HTTP POST para editar la información de un usuario en una aplicación web. 
Extrae parámetros del formulario, incluyendo datos personales y una foto, y guarda la imagen en el servidor.
Utiliza un objeto UsuarioDAO para actualizar la información del usuario en la base de datos, incluyendo 
la ruta de la foto. Si la edición es exitosa, redirige al usuario a una lista de usuarios; de lo contrario,
redirige a la página de agregar usuario con un mensaje de error.
*/
@WebServlet(name = "EditarUsuarioServlet", urlPatterns = {"/EditarUsuarioServlet"}) // Define el servlet con un nombre y una URL
@MultipartConfig // Anotación que indica que el servlet puede manejar solicitudes con archivos adjuntos
public class EditarUsuarioServlet extends HttpServlet { // Clase que extiende HttpServlet para manejar solicitudes HTTP

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) // Método que maneja las solicitudes POST
            throws ServletException, IOException { // Declara que puede lanzar excepciones ServletException e IOException

        request.setCharacterEncoding("UTF-8"); // Establece la codificación de caracteres de la solicitud a UTF-8 para manejar caracteres especiales

        // Obtener parámetros del formulario
        String id = request.getParameter("id"); // Obtiene el ID del usuario a editar desde la solicitud
        String nombre = request.getParameter("nombre"); // Obtiene el nombre del usuario desde la solicitud
        String documento = request.getParameter("documento"); // Obtiene el documento de identificación del usuario desde la solicitud
        String direccion = request.getParameter("direccion"); // Obtiene la dirección del usuario desde la solicitud
        String correo = request.getParameter("correo"); // Obtiene el correo electrónico del usuario desde la solicitud
        String telefono = request.getParameter("telefono"); // Obtiene el número de teléfono del usuario desde la solicitud
        String pass = request.getParameter("pass"); // Obtiene la contraseña del usuario desde la solicitud
        String rolId = request.getParameter("rol_id"); // Obtiene el ID del rol del usuario desde la solicitud
        String estado = request.getParameter("estado"); // Obtiene el estado del usuario desde la solicitud

        // Manejo de la foto
        String nombreFoto = null; // Inicializa la variable para almacenar el nombre de la foto
        Part fotoPart = request.getPart("foto"); // Obtiene la parte del formulario que corresponde a la foto
        if (fotoPart != null && fotoPart.getSize() > 0) { // Verifica si se ha subido una foto
            String nombreArchivo = Paths.get(fotoPart.getSubmittedFileName()).getFileName().toString(); // Obtiene el nombre del archivo subido

            // Carpeta donde se guardarán las fotos
            String uploadPath = getServletContext().getRealPath("") + "asset/imagenes/trabajadores/"; // Define la ruta de subida en el servidor
            File uploadDir = new File(uploadPath); // Crea un objeto File para la carpeta de subida
            if (!uploadDir.exists()) uploadDir.mkdirs(); // Crea la carpeta si no existe

            // Guardar archivo en disco
            fotoPart.write(uploadPath + nombreArchivo); // Escribe el archivo en el disco en la ruta especificada

            // Ruta relativa para BD
            nombreFoto = "asset/imagenes/trabajadores/" + nombreArchivo; // Establece la ruta relativa de la foto para almacenar en la base de datos
        }

        UsuarioDAO dao = new UsuarioDAO(); // Crea una instancia del DAO para manejar operaciones de usuario
        // Llama al método editarUsuario del DAO para actualizar la información del usuario en la base de datos
        boolean exito = dao.editarUsuario(id, nombre, documento, direccion, correo, telefono, pass, rolId, estado, nombreFoto); 

        // Si hay nueva foto, actualizarla en la tabla fotos_usuario
        if (exito && nombreFoto != null) { // Verifica si la edición fue exitosa y si se ha subido una nueva foto
            try {
                // Eliminar foto anterior y guardar la nueva
                java.sql.Connection conn = com.agritrack.agritrackplus.db.Conexion.getConnection(); // Obtiene una conexión a la base de datos
                java.sql.PreparedStatement ps = conn.prepareStatement(
                    "UPDATE fotos_usuario SET ruta=? WHERE usuario_id=?" // Prepara la consulta para actualizar la ruta de la foto
                );
                ps.setString(1, nombreFoto); // Establece la nueva ruta de la foto
                ps.setInt(2, Integer.parseInt(id)); // Establece el ID del usuario
                int filas = ps.executeUpdate(); // Ejecuta la actualización y obtiene el número de filas afectadas
                ps.close(); // Cierra el PreparedStatement

                // Si no existía foto, insertar nueva
                if (filas == 0) { // Verifica si no se actualizó ninguna fila (es decir, no había foto anterior)
                    java.sql.PreparedStatement psInsert = conn.prepareStatement(
                        "INSERT INTO fotos_usuario (usuario_id, ruta) VALUES (?, ?)" // Prepara la consulta para insertar una nueva foto
                    );
                    psInsert.setInt(1, Integer.parseInt(id)); // Establece el ID del usuario
                    psInsert.setString(2, nombreFoto); // Establece la ruta de la nueva foto
                    psInsert.executeUpdate(); // Ejecuta la inserción
                    psInsert.close(); // Cierra el PreparedStatement
                }
                conn.close(); // Cierra la conexión a la base de datos
            } catch (Exception e) { // Captura cualquier excepción que ocurra durante el manejo de la foto
                e.printStackTrace(); // Imprime el stack trace de la excepción para depuración
            }
        }

        // Verifica si la edición del usuario fue exitosa
        if (exito) {
            // Redirige al usuario a la página de usuarios con un mensaje de éxito
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Usuarios.jsp?edicion=exitosa");
        } else {
            // Redirige a la página de agregar usuario con un mensaje de error
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Usuario.jsp?id=" + id + "&error=true");
        }
    }
}