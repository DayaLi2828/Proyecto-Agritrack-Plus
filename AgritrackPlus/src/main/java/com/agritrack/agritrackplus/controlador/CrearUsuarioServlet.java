package com.agritrack.agritrackplus.controlador; // Declara el paquete donde se encuentra esta clase.

import com.agritrack.agritrackplus.DAO.UsuarioDAO; // Importa la clase UsuarioDAO que maneja la persistencia de datos de usuario.
import java.io.File; // Importa la clase File para manejar archivos en el sistema.
import java.io.IOException; // Importa la clase IOException para manejar excepciones de entrada/salida.
import java.nio.file.Paths; // Importa la clase Paths para trabajar con rutas de archivos.
import java.net.URLEncoder; // Importa la clase URLEncoder para codificar parámetros de URL.
import jakarta.servlet.ServletException; // Importa la clase ServletException para manejar excepciones en servlets.
import jakarta.servlet.annotation.MultipartConfig; // Importa la anotación MultipartConfig para manejar archivos subidos.
import jakarta.servlet.annotation.WebServlet; // Importa la anotación WebServlet para definir un servlet.
import jakarta.servlet.http.HttpServlet; // Importa la clase HttpServlet, la cual es la clase base para servlets HTTP.
import jakarta.servlet.http.HttpServletRequest; // Importa la clase HttpServletRequest para manejar solicitudes HTTP.
import jakarta.servlet.http.HttpServletResponse; // Importa la clase HttpServletResponse para manejar respuestas HTTP.
import jakarta.servlet.http.Part; // Importa la clase Part para manejar partes de una solicitud multipart/form-data.

/*
servlet en Java que gestiona la creación de nuevos usuarios en una aplicación web. 
Al recibir una solicitud POST, extrae parámetros como nombre, contraseña, documento, dirección, 
correo y teléfono. Realiza varias validaciones, incluyendo la verificación del formato del correo, 
la longitud de la contraseña y la existencia de duplicados en la base de datos. Si alguna validación
falla, redirige al usuario a la página de creación con un mensaje de error correspondiente. 
Además, permite la subida de una foto de perfil, generando un nombre único para el archivo y 
guardándolo en el servidor. Finalmente, si todas las validaciones son exitosas, se crea el nuevo 
usuario en la base de datos y se redirige al usuario a una página de éxito.
*/
@WebServlet(name = "CrearUsuarioServlet", urlPatterns = {"/CrearUsuarioServlet"}) // Define el servlet y su patrón de URL.
@MultipartConfig // Indica que este servlet puede manejar solicitudes que contienen archivos.
public class CrearUsuarioServlet extends HttpServlet { // Declara la clase CrearUsuarioServlet que extiende HttpServlet.

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { // Método que maneja solicitudes POST.
        request.setCharacterEncoding("UTF-8"); // Establece la codificación de caracteres de la solicitud a UTF-8.
        UsuarioDAO dao = new UsuarioDAO(); // Crea una instancia de UsuarioDAO para interactuar con la base de datos.

        // Recepción de parámetros desde la solicitud HTTP.
        String nombre = request.getParameter("nombre"); // Obtiene el parámetro "nombre" de la solicitud.
        String pass = request.getParameter("pass"); // Obtiene el parámetro "pass" (contraseña) de la solicitud.
        String documento = request.getParameter("documento"); // Obtiene el parámetro "documento" de la solicitud.
        String direccion = request.getParameter("direccion"); // Obtiene el parámetro "direccion" de la solicitud.
        String correo = request.getParameter("correo"); // Obtiene el parámetro "correo" de la solicitud.
        String telefono = request.getParameter("telefono"); // Obtiene el parámetro "telefono" de la solicitud.
        String rolIdStr = request.getParameter("rol_id"); // Obtiene el parámetro "rol_id" de la solicitud.
        String regexEstricto = "^[\\w.-]+@(gmail\\.com|outlook\\.com|hotmail\\.com)$"; // Define una expresión regular para validar correos electrónicos específicos.

        // Validación del correo electrónico.
        if (!correo.matches(regexEstricto)) { // Comprueba si el correo no coincide con el patrón definido.
            redirigirConErrores(request, response, "error_correo", nombre, documento, direccion, correo, telefono, rolIdStr, pass); // Redirige con un mensaje de error.
            return; // Termina la ejecución del método si hay un error.
        }

        // --- VALIDACIÓN 1: NOMBRE (Solo letras y espacios) ---
        // Expresión regular: permite letras (con tildes y ñ) y espacios. No permite números.
        if (nombre == null || !nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) { // Comprueba si el nombre es nulo o no coincide con el patrón.
            redirigirConErrores(request, response, "error_nombre", nombre, documento, direccion, correo, telefono, rolIdStr, pass); // Redirige con un mensaje de error.
            return; // Termina la ejecución del método si hay un error.
        }

        // Validación del documento: debe ser un número de 10 dígitos.
        if (documento == null || !documento.matches("\\d{10}")) { // Comprueba si el documento es nulo o no tiene 10 dígitos.
            redirigirConErrores(request, response, "error_doc", nombre, documento, direccion, correo, telefono, rolIdStr, pass); // Redirige con un mensaje de error.
            return; // Termina la ejecución del método si hay un error.
        }

        // Validación del teléfono: debe ser un número de 10 dígitos.
        if (telefono == null || !telefono.matches("\\d{10}")) { // Comprueba si el teléfono es nulo o no tiene 10 dígitos.
            redirigirConErrores(request, response, "error_tel", nombre, documento, direccion, correo, telefono, rolIdStr, pass); // Redirige con un mensaje de error.
            return; // Termina la ejecución del método si hay un error.
        }

        // --- VALIDACIÓN 2: ROL ---
        int rolId; // Declara una variable para almacenar el ID del rol.
        try {
            rolId = (rolIdStr != null && !rolIdStr.isEmpty()) ? Integer.parseInt(rolIdStr) : 2; // Intenta convertir el rol a un entero, o asigna 2 por defecto si está vacío.
        } catch (NumberFormatException e) { // Captura excepciones si la conversión falla.
            rolId = 2; // Asigna 2 como rol por defecto en caso de error.
        }

        // --- VALIDACIÓN 3: CONTRASEÑA ---
        if (pass == null || pass.trim().length() < 6) { // Comprueba si la contraseña es nula o tiene menos de 6 caracteres.
            redirigirConErrores(request, response, "error_pass", nombre, documento, direccion, correo, telefono, rolIdStr, pass); // Redirige con un mensaje de error.
            return; // Termina la ejecución del método si hay un error.
        }

        // --- VALIDACIÓN 4: DUPLICADOS ---
        // Verificar Documento
        if (dao.existeDocumento(documento)) { // Llama al método existeDocumento en UsuarioDAO para verificar si el documento ya existe.
            redirigirConErrores(request, response, "error_duplicado_doc", nombre, documento, direccion, correo, telefono, rolIdStr, pass); // Redirige con un mensaje de error.
            return; // Termina la ejecución del método si hay un error.
        }

        // Verificar Correo (Necesitas crear este método en tu DAO)
        if (dao.existeCorreo(correo)) { // Llama al método existeCorreo en UsuarioDAO para verificar si el correo ya existe.
            redirigirConErrores(request, response, "error_duplicado_correo", nombre, documento, direccion, correo, telefono, rolIdStr, pass); // Redirige con un mensaje de error.
            return; // Termina la ejecución del método si hay un error.
        }

        // Verificar Teléfono (Necesitas crear este método en tu DAO)
        if (dao.existeTelefono(telefono)) { // Llama al método existeTelefono en UsuarioDAO para verificar si el teléfono ya existe.
            redirigirConErrores(request, response, "error_duplicado_tel", nombre, documento, direccion, correo, telefono, rolIdStr, pass); // Redirige con un mensaje de error.
            return; // Termina la ejecución del método si hay un error.
        }

        // --- PROCESAMIENTO DE FOTO ---
        String nombreFoto = "asset/imagenes/default-avatar.png"; // Establece una imagen por defecto para el usuario.
        try {
            Part fotoPart = request.getPart("foto"); // Obtiene la parte del archivo "foto" de la solicitud.
            if (fotoPart != null && fotoPart.getSize() > 0) { // Comprueba si se ha subido una foto.
                String fileName = System.currentTimeMillis() + "_" + Paths.get(fotoPart.getSubmittedFileName()).getFileName().toString(); // Genera un nombre de archivo único basado en la fecha y el nombre original.
                String uploadPath = getServletContext().getRealPath("/") + "asset/imagenes/trabajadores/"; // Define la ruta de subida para las imágenes.
                File uploadDir = new File(uploadPath); // Crea un objeto File para la ruta de subida.
                if (!uploadDir.exists()) uploadDir.mkdirs(); // Crea el directorio si no existe.
                fotoPart.write(uploadPath + fileName); // Guarda la foto en la ruta especificada.
                nombreFoto = "asset/imagenes/trabajadores/" + fileName; // Actualiza la variable nombreFoto con la ruta de la imagen subida.
            }
        } catch (Exception e) { // Captura cualquier excepción que ocurra durante el procesamiento de la foto.
            System.out.println("Error procesando foto: " + e.getMessage()); // Imprime un mensaje de error en la consola.
        }

        // --- PERSISTENCIA EN BD ---
        boolean exito = dao.crear( // Llama al método crear en UsuarioDAO para guardar el nuevo usuario en la base de datos.
            nombre.trim(), // El nombre del usuario.
            pass, // La contraseña del usuario.
            documento.trim(), // El documento del usuario.
            direccion, // La dirección del usuario.
            correo, // El correo del usuario.
            telefono, // El teléfono del usuario.
            rolId, // El ID del rol del usuario.
            nombreFoto // La ruta de la foto del usuario.
        );

        if (exito) { // Comprueba si la creación del usuario fue exitosa.
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Usuarios.jsp?mensaje=creado"); // Redirige a la página de usuarios con un mensaje de éxito.
        } else {
            redirigirConErrores(request, response, "error_db", nombre, documento, direccion, correo, telefono, rolIdStr, pass); // Redirige con un mensaje de error si la creación falla.
        }
    }

    private void redirigirConErrores(HttpServletRequest request, HttpServletResponse response, // Método privado para redirigir con errores.
                                   String tipoError, String nombre, String documento, String direccion, 
                                   String correo, String telefono, String rolIdStr, String pass) throws IOException { // Define los parámetros necesarios para redirigir.
        response.sendRedirect(request.getContextPath() + // Redirige a la página de agregar usuario con los parámetros de error.
            "/public/Administrador/Agregar_Usuario.jsp?" +
            "nombre=" + URLEncoder.encode(nombre != null ? nombre : "", "UTF-8") + // Codifica el nombre para la URL.
            "&documento=" + URLEncoder.encode(documento != null ? documento : "", "UTF-8") + // Codifica el documento para la URL.
            "&direccion=" + URLEncoder.encode(direccion != null ? direccion : "", "UTF-8") + // Codifica la dirección para la URL.
            "&correo=" + URLEncoder.encode(correo != null ? correo : "", "UTF-8") + // Codifica el correo para la URL.
            "&telefono=" + URLEncoder.encode(telefono != null ? telefono : "", "UTF-8") + // Codifica el teléfono para la URL.
            "&rol_id=" + URLEncoder.encode(rolIdStr != null ? rolIdStr : "2", "UTF-8") + // Codifica el rol para la URL, asignando 2 por defecto.
            "&pass=" + URLEncoder.encode(pass != null ? pass : "", "UTF-8") + // Codifica la contraseña para la URL.
            "&" + tipoError + "=true"); // Añade el tipo de error a la URL.
    }
}