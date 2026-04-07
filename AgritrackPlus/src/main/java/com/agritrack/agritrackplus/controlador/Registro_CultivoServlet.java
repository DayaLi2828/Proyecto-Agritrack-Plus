package com.agritrack.agritrackplus.controlador; 
// Define que este archivo pertenece a la capa de control del proyecto AgritrackPlus.
// La capa de control se encarga de recibir peticiones del usuario y coordinar la lógica.

import com.agritrack.agritrackplus.DAO.Registro_CultivoDAO; // Importa la clase DAO que contiene los métodos para interactuar con la base de datos (guardar, editar, etc.).
import java.io.IOException; // Importa la clase para manejar errores de entrada/salida (ej. problemas al enviar respuesta al navegador).
import jakarta.servlet.ServletException; // Importa la clase para manejar errores específicos de Servlets (ej. fallos en la ejecución del servlet).
import jakarta.servlet.annotation.WebServlet; // Permite usar la anotación @WebServlet para definir la ruta URL que activará este servlet.
import jakarta.servlet.http.HttpServlet; // Clase base de la cual heredan todos los Servlets en Java.
import jakarta.servlet.http.HttpServletRequest; // Objeto que representa la petición enviada por el usuario desde el navegador (contiene parámetros, cabeceras, etc.).
import jakarta.servlet.http.HttpServletResponse; // Objeto que representa la respuesta que el servidor enviará al navegador del usuario.
/*
maneja las solicitudes POST para registrar o editar cultivos en una base de datos.
Captura datos del formulario, como el ID del cultivo, nombre, fechas, ciclo, estado y detalles de 
insumos y trabajadores. Dependiendo de si se está editando un cultivo existente o registrando uno nuevo,
llama al método correspondiente del Registro_CultivoDAO y redirige al usuario a la página de cultivos
registrados o al formulario de registro con mensajes de éxito o error.
*/
@WebServlet(name = "Registro_CultivoServlet", urlPatterns = {"/Registro_CultivoServlet"})
// Define la URL interna del servlet. Cuando un formulario usa action="Registro_CultivoServlet", se ejecuta este código.

public class Registro_CultivoServlet extends HttpServlet {
    // Clase Servlet que extiende HttpServlet y maneja las peticiones relacionadas con el registro/edición de cultivos.

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Método que se ejecuta automáticamente cuando el formulario usa method="POST".
        // Ideal para enviar datos sensibles o largos.

        request.setCharacterEncoding("UTF-8"); 
        // Configura la codificación de caracteres para que se lean correctamente acentos y eñes.

        // --- 1. CAPTURAR DATOS DEL FORMULARIO ---
        String id = request.getParameter("id"); 
        // Captura el ID del cultivo (solo llega si estamos editando un cultivo existente).

        String nombre = request.getParameter("nombre"); 
        // Captura el nombre del cultivo (ejemplo: "Lote de Maíz").

        String fechaSiembra = request.getParameter("fecha_siembra"); 
        // Captura la fecha de siembra en formato texto (YYYY-MM-DD).

        String fechaCosecha = request.getParameter("fecha_cosecha"); 
        // Captura la fecha estimada de cosecha.

        String ciclo = request.getParameter("ciclo"); 
        // Captura el ciclo del cultivo (ejemplo: Corto, Largo, Perenne).

        String estado = (request.getParameter("estado") != null) ? request.getParameter("estado") : "Activo"; 
        // Captura el estado del cultivo. Si no se envía, se asigna "Activo" por defecto.

        String supervisorIdStr = request.getParameter("supervisor_id"); 
        // Captura el ID del supervisor como texto.

        int supervisorId = (supervisorIdStr != null && !supervisorIdStr.isEmpty())
                           ? Integer.parseInt(supervisorIdStr) : 0; 
        // Convierte el ID del supervisor a número entero. Si no se envía, se asigna 0.

        // --- CAPTURAR LISTAS (DATOS MÚLTIPLES) ---
        String[] productoIds = request.getParameterValues("producto_id"); 
        // Captura todos los IDs de productos/insumos seleccionados.

        String[] cantidades = request.getParameterValues("cantidad_producto"); 
        // Captura las cantidades de cada insumo usado.

        String[] trabajadoresIds = request.getParameterValues("trabajadores"); 
        // Captura los IDs de los trabajadores asignados al cultivo.

        Registro_CultivoDAO dao = new Registro_CultivoDAO(); 
        // Instancia el DAO para poder llamar a sus métodos de guardar/editar cultivos.

        try {
            // --- DECISIÓN LÓGICA: ¿EDITAR O REGISTRAR? ---
            if (id != null && !id.isEmpty()) {
                // Si el ID no está vacío, significa que el cultivo ya existe y se debe EDITAR.

                String resultado = dao.editarCultivoCompleto(
                    id, nombre, fechaSiembra, fechaCosecha, ciclo, estado, supervisorId,
                    productoIds, cantidades, trabajadoresIds
                ); 
                // Llama al método del DAO para editar el cultivo con todos los datos capturados.

                // --- MANEJO DE RESPUESTAS TRAS EDITAR ---
                if (resultado.equals("ok")) {
                    // Si el DAO devuelve "ok", redirige al listado con mensaje de éxito.
                    response.sendRedirect(request.getContextPath() +
                        "/public/Administrador/Cultivos_Registrados.jsp?mensaje=actualizado");

                } else if (resultado.startsWith("agotado:")) {
                    // Si algún producto quedó agotado, se avisa en la URL.
                    String nombresAgotados = resultado.substring("agotado:".length());
                    response.sendRedirect(request.getContextPath() +
                        "/public/Administrador/Cultivos_Registrados.jsp?mensaje=actualizado&agotado=" +
                        java.net.URLEncoder.encode(nombresAgotados, "UTF-8"));

                } else if (resultado.startsWith("insuficiente:")) {
                    // Si se intentó usar más insumos de los disponibles, se devuelve al formulario con error.
                    String detalle = resultado.substring("insuficiente:".length());
                    response.sendRedirect(request.getContextPath() +
                        "/public/Administrador/Registro_Cultivos.jsp?id=" + id + "&error=insuficiente&detalle=" +
                        java.net.URLEncoder.encode(detalle, "UTF-8"));

                } else {
                    // Cualquier otro error redirige con mensaje de error general.
                    response.sendRedirect(request.getContextPath() +
                        "/public/Administrador/Registro_Cultivos.jsp?id=" + id + "&error=true");
                }

            } else {
                // --- MODO REGISTRO NUEVO ---
                // Si no hay ID, significa que es un cultivo nuevo y se debe REGISTRAR.

                String resultado = dao.registrarCultivoCompleto(
                    nombre, fechaSiembra, ciclo, supervisorId,
                    productoIds, cantidades, trabajadoresIds
                ); 
                // Llama al método del DAO para registrar un cultivo nuevo.

                // --- MANEJO DE RESPUESTAS TRAS REGISTRAR ---
                if (resultado.equals("ok")) { // Verifica si el resultado de la operación es "ok", lo que indica que el registro fue exitoso.
                    // Registro exitoso: redirige al listado con mensaje de confirmación.
                    response.sendRedirect(request.getContextPath() + // Redirige al cliente a la página especificada.
                        "/public/Administrador/Cultivos_Registrados.jsp?mensaje=registrado"); // La URL de redirección incluye un parámetro "mensaje" con el valor "registrado" para indicar éxito.
                
                } else if (resultado.startsWith("agotado:")) { 
                    // Verifica si el resultado comienza con "agotado:", indicando que algunos productos se quedaron sin stock.
                    // Registro exitoso pero algún producto quedó agotado.
                    String nombresAgotados = resultado.substring("agotado:".length()); 
                    // Extrae el nombre de los productos agotados desde el resultado, omitiendo la parte "agotado:".
                    response.sendRedirect(request.getContextPath() + // Redirige al cliente a la página de cultivos registrados.
                        "/public/Administrador/Cultivos_Registrados.jsp?mensaje=registrado&agotado=" + 
                        // La URL incluye un parámetro "mensaje" con valor "registrado" y un parámetro "agotado" con los nombres de los productos agotados.
                        java.net.URLEncoder.encode(nombresAgotados, "UTF-8")); 
                        // Codifica los nombres de los productos agotados para asegurar que sean válidos en una URL.
                
                } else if (resultado.startsWith("insuficiente:")) { 
                    // Verifica si el resultado comienza con "insuficiente:", indicando que no hay suficiente stock para completar el registro.
                    // No se pudo registrar por falta de stock en inventario.
                    String detalle = resultado.substring("insuficiente:".length()); 
                    // Extrae el detalle del error desde el resultado, omitiendo la parte "insuficiente:".
                    response.sendRedirect(request.getContextPath() + // Redirige al cliente a la página de registro de cultivos.
                        "/public/Administrador/Registro_Cultivos.jsp?error=insuficiente&detalle=" + 
                        // La URL incluye un parámetro "error" con valor "insuficiente" y un parámetro "detalle" con la información del error.
                        java.net.URLEncoder.encode(detalle, "UTF-8")); // Codifica el detalle del error para asegurar que sea válido en una URL.
                
                } else { // Si el resultado no coincide con ninguno de los casos anteriores, se considera un error inesperado.
                    // Error inesperado en el registro.
                    response.sendRedirect(request.getContextPath() + // Redirige al cliente a la página de registro de cultivos.
                        "/public/Administrador/Registro_Cultivos.jsp?error=true"); 
                    // La URL incluye un parámetro "error" con valor "true" para indicar que ocurrió un error inesperado.
                }
            }

        } catch (Exception e) {
            e.printStackTrace(); 
            // Captura errores graves (ej. desconexión de la base de datos) y los imprime en consola.

            response.sendRedirect(request.getContextPath() +
                "/public/Administrador/Registro_Cultivos.jsp?error=excepcion"); 
            // Redirige al formulario con mensaje de error por excepción.
        }
    }
}
