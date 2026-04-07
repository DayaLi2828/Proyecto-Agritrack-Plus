package com.agritrack.agritrackplus.controlador; // Paquete que contiene la clase del controlador del servlet

import com.agritrack.agritrackplus.DAO.Registro_CultivoDAO; // Importa la clase DAO para manejar operaciones de cultivo
import java.io.IOException; // Importa la clase IOException para manejar errores de entrada/salida
import jakarta.servlet.ServletException; // Importa la clase ServletException para manejar errores en los servlets
import jakarta.servlet.annotation.WebServlet; // Importa la anotación WebServlet para definir el servlet
import jakarta.servlet.http.HttpServlet; // Importa la clase HttpServlet, la cual es la clase base para los servlets HTTP
import jakarta.servlet.http.HttpServletRequest; // Importa la clase HttpServletRequest para manejar las solicitudes HTTP
import jakarta.servlet.http.HttpServletResponse; // Importa la clase HttpServletResponse para manejar las respuestas HTTP
/*
procesa solicitudes HTTP POST para editar cultivos en una aplicación web. 
Extrae parámetros como el ID del cultivo, nombre, fechas, estado y detalles de productos y trabajadores 
desde la solicitud. Utiliza un objeto de Registro_CultivoDAO para actualizar la información del cultivo
en la base de datos mediante el método editarCultivoCompleto. Según el resultado de la operación, redirige
al usuario a diferentes páginas: a una lista de cultivos si la actualización es exitosa, o a la página de 
edición con mensajes de error si hay problemas como productos agotados o insuficientes. También maneja 
excepciones y redirige con un mensaje de error en caso de fallos.
*/
@WebServlet(name = "EditarCultivoServlet", urlPatterns = {"/EditarCultivoServlet"}) // Define el servlet con un nombre y una URL
public class EditarCultivoServlet extends HttpServlet { // Clase que extiende HttpServlet para manejar solicitudes HTTP

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) // Método que maneja las solicitudes POST
            throws ServletException, IOException { // Declara que puede lanzar excepciones ServletException e IOException

        request.setCharacterEncoding("UTF-8"); // Establece la codificación de caracteres de la solicitud a UTF-8 para manejar caracteres especiales

        String id = request.getParameter("id"); // Obtiene el parámetro "id" de la solicitud, que identifica el cultivo a editar
        String nombre = request.getParameter("nombre"); // Obtiene el nombre del cultivo desde la solicitud
        String fechaSiembra = request.getParameter("fecha_siembra"); // Obtiene la fecha de siembra del cultivo desde la solicitud
        String fechaCosecha = request.getParameter("fecha_cosecha"); // Obtiene la fecha de cosecha del cultivo desde la solicitud
        String ciclo = request.getParameter("ciclo"); // Obtiene el ciclo del cultivo desde la solicitud
        // Obtiene el estado del cultivo, si no se proporciona, se establece como "Activo"
        String estado = (request.getParameter("estado") != null) ? request.getParameter("estado") : "Activo"; 

        String supervisorIdStr = request.getParameter("supervisor_id"); // Obtiene el ID del supervisor desde la solicitud
        // Convierte el ID del supervisor a un entero, si no se proporciona, se establece en 0
        int supervisorId = (supervisorIdStr != null && !supervisorIdStr.isEmpty())
                           ? Integer.parseInt(supervisorIdStr) : 0; 

        String[] productoIds = request.getParameterValues("producto_id"); // Obtiene un array de IDs de productos desde la solicitud
        String[] cantidades = request.getParameterValues("cantidad_producto"); // Obtiene un array de cantidades de productos desde la solicitud
        String[] trabajadoresIds = request.getParameterValues("trabajadores"); // Obtiene un array de IDs de trabajadores desde la solicitud

        Registro_CultivoDAO dao = new Registro_CultivoDAO(); // Crea una instancia del DAO para manejar operaciones de cultivo

        try {
            // Llama al método editarCultivoCompleto del DAO para actualizar el cultivo con los datos proporcionados
            String resultado = dao.editarCultivoCompleto(
                id, nombre, fechaSiembra, fechaCosecha, ciclo, estado, supervisorId,
                productoIds, cantidades, trabajadoresIds
            );

            // Verifica si el resultado de la operación es "ok", lo que indica que la actualización fue exitosa
            if (resultado.equals("ok")) {
                // Redirige al usuario a la página de cultivos registrados con un mensaje de éxito
                response.sendRedirect(request.getContextPath() +
                    "/public/Administrador/Cultivos_Registrados.jsp?mensaje=actualizado");

            // Verifica si el resultado indica que algunos productos están agotados
            } else if (resultado.startsWith("agotado:")) {
                // Extrae los nombres de los productos agotados del resultado
                String nombresAgotados = resultado.substring("agotado:".length());
                // Redirige a la página de cultivos registrados con un mensaje de éxito y los productos agotados
                response.sendRedirect(request.getContextPath() +
                    "/public/Administrador/Cultivos_Registrados.jsp?mensaje=actualizado&agotado=" +
                    java.net.URLEncoder.encode(nombresAgotados, "UTF-8"));

            // Verifica si el resultado indica que hay una cantidad insuficiente de productos
            } else if (resultado.startsWith("insuficiente:")) {
                // Extrae los detalles de la insuficiencia del resultado
                String detalle = resultado.substring("insuficiente:".length());
                // Redirige a la página de edición del cultivo con un mensaje de error por insuficiencia
                response.sendRedirect(request.getContextPath() +
                    "/public/Administrador/Editar_Cultivo.jsp?id=" + id + "&error=insuficiente&detalle=" +
                    java.net.URLEncoder.encode(detalle, "UTF-8"));

            } else {
                // Si el resultado no es ninguno de los anteriores, redirige a la página de edición del cultivo con un error genérico
                response.sendRedirect(request.getContextPath() +
                    "/public/Administrador/Editar_Cultivo.jsp?id=" + id + "&error=true");
            }

        } catch (Exception e) { // Captura cualquier excepción que ocurra durante el proceso
            e.printStackTrace(); // Imprime el stack trace de la excepción para depuración
            // Redirige a la página de edición del cultivo con un mensaje de error por excepción
            response.sendRedirect(request.getContextPath() +
                "/public/Administrador/Editar_Cultivo.jsp?id=" + id + "&error=excepcion");
        }
    }
}