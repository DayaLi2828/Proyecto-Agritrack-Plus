package com.agritrack.agritrackplus.controlador; // Paquete que contiene la clase del controlador para la actualización de perfil

import com.agritrack.agritrackplus.DAO.UsuarioDAO; // Importación de la clase UsuarioDAO para interactuar con la base de datos de usuarios
import java.io.File; // Importación de la clase File para manejar archivos en el sistema de archivos
import java.io.IOException; // Importación de la excepción para manejar errores de entrada/salida
import jakarta.servlet.ServletException; // Importación de la excepción para manejar errores en servlets
import jakarta.servlet.annotation.MultipartConfig; // Importación para configurar el servlet para recibir archivos
import jakarta.servlet.annotation.WebServlet; // Importación de la anotación para definir un servlet
import jakarta.servlet.http.HttpServlet; // Importación de la clase base para servlets HTTP
import jakarta.servlet.http.HttpServletRequest; // Importación para manejar solicitudes HTTP
import jakarta.servlet.http.HttpServletResponse; // Importación para manejar respuestas HTTP
import jakarta.servlet.http.HttpSession; // Importación para manejar sesiones de usuario
import jakarta.servlet.http.Part; // Importación para manejar partes de una solicitud multipart (como archivos)

//Este servlet se encarga de actualizar el perfil de un usuario en el sistema. 
//Primero, valida que el usuario esté autenticado mediante la sesión. 
//Luego, captura los datos del formulario, procesa la imagen de perfil si se ha subido, 
//y finalmente, actualiza la información del usuario en la base de datos. Dependiendo del rol del 
//usuario, redirige a la página correspondiente con un mensaje de éxito o error.
@WebServlet(name = "ActualizarPerfilServlet", urlPatterns = {"/ActualizarPerfilServlet"}) // Define el servlet y su URL
@MultipartConfig( // Configuración para manejar archivos subidos
    fileSizeThreshold = 1024 * 1024 * 2, // Tamaño máximo del archivo en memoria (2MB)
    maxFileSize = 1024 * 1024 * 10,      // Tamaño máximo de un archivo subido (10MB)
    maxRequestSize = 1024 * 1024 * 50    // Tamaño máximo de la solicitud completa (50MB)
)
public class ActualizarPerfilServlet extends HttpServlet { // Clase que extiende HttpServlet para manejar solicitudes HTTP

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException { // Método que maneja solicitudes POST
        
        // 1. Validar sesión
        HttpSession session = request.getSession(false); // Intenta obtener la sesión actual; si no existe, devuelve null
        if (session == null || session.getAttribute("usuario_id") == null) { // Verifica si la sesión es nula o si el ID de usuario no está presente
            // Redirige a la página de inicio si no hay sesión activa
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return; // Sale del método
        }

        // Obtiene el ID del usuario y el rol desde la sesión
        Integer usuarioId = (Integer) session.getAttribute("usuario_id"); 
        String rolUsuario = (String) session.getAttribute("rol"); 

        // 2. Capturar parámetros del formulario
        // Captura los datos del formulario enviados en la solicitud
        String nombre = request.getParameter("txtNombre"); // Captura el nombre del usuario
        String correo = request.getParameter("txtCorreo"); // Captura el correo electrónico del usuario
        String telefono = request.getParameter("txtTelefono"); // Captura el número de teléfono del usuario
        String direccion = request.getParameter("txtDireccion"); // Captura la dirección del usuario
        String nuevaPass = request.getParameter("txtPassword"); // Captura la nueva contraseña del usuario
        
        // Captura el documento del usuario, se espera que sea un campo oculto en el formulario
        String documento = request.getParameter("txtDocumento"); 

        // 3. Procesar la Foto con nombre único (evita problemas de caché)
        Part filePart = request.getPart("fotoPerfil"); // Obtiene la parte del archivo subido (foto de perfil)
        String nombreArchivoFinal = null; // Inicializa la variable para el nombre del archivo final

        // Verifica si se ha subido un archivo y que su tamaño sea mayor a 0
        if (filePart != null && filePart.getSize() > 0) {
            // Crea un nombre único para el archivo: "perfil_ID_MarcaDeTiempo.jpg"
            nombreArchivoFinal = "perfil_" + usuarioId + "_" + System.currentTimeMillis() + ".jpg";
            
            // Obtiene la ruta física en el servidor donde se guardará la imagen: /public/asset/imagenes
            String uploadPath = getServletContext().getRealPath("") + File.separator + "asset" + File.separator + "imagenes";
            
            // Crea un objeto File para la carpeta de subida
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) { // Verifica si la carpeta no existe
                uploadDir.mkdirs(); // Crea la carpeta si no existe
            }

            // Escribe el archivo en la ruta especificada en el servidor
            filePart.write(uploadPath + File.separator + nombreArchivoFinal);
        }

        // 4. Ejecutar actualización en la Base de Datos
        UsuarioDAO dao = new UsuarioDAO(); // Crea una instancia de UsuarioDAO para interactuar con la base de datos
        // Llama al método para actualizar el perfil del usuario en la base de datos
        boolean exito = dao.actualizarPerfil(usuarioId, nombre, documento, direccion, nuevaPass, correo, telefono, nombreArchivoFinal);

        // 5. DETERMINAR RUTA DE REGRESO (Redirección Dinámica)
        String destino = "Supervisor/Supervisor.jsp"; // Establece la ruta de destino por defecto

        // Verifica el rol del usuario para determinar la página de redirección
        if (rolUsuario != null) {
            if (rolUsuario.equalsIgnoreCase("administrador")) { // Si el rol es administrador
                destino = "Administrador/Admin.jsp"; // Redirige a la página del administrador
            } else if (rolUsuario.equalsIgnoreCase("trabajador")) { // Si el rol es trabajador
                destino = "Trabajador/Trabajador.jsp"; // Redirige a la página del trabajador
            }
        }

        // 6. Respuesta final
        // Verifica si la actualización fue exitosa
        if (exito) {
            session.setAttribute("usuario_nombre", nombre); // Actualiza el nombre del usuario en la sesión
            // Redirige a la página correspondiente con un mensaje de éxito
            response.sendRedirect(request.getContextPath() + "/public/" + destino + "?update=success");
        } else {
            // Redirige a la página correspondiente con un mensaje de error si la actualización falló
            response.sendRedirect(request.getContextPath() + "/public/" + destino + "?update=error");
        }
    }
}