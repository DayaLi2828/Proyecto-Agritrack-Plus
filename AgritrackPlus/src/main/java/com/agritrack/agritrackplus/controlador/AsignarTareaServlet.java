package com.agritrack.agritrackplus.controlador; // Paquete que contiene la clase del controlador para asignar tareas

import com.agritrack.agritrackplus.DAO.TareaDAO; // Importación de la clase TareaDAO para interactuar con la base de datos de tareas
import java.io.IOException; // Importación de la excepción para manejar errores de entrada/salida
import jakarta.servlet.ServletException; // Importación de la excepción para manejar errores en servlets
import jakarta.servlet.annotation.WebServlet; // Importación de la anotación para definir un servlet
import jakarta.servlet.http.HttpServlet; // Importación de la clase base para servlets HTTP
import jakarta.servlet.http.HttpServletRequest; // Importación para manejar solicitudes HTTP
import jakarta.servlet.http.HttpServletResponse; // Importación para manejar respuestas HTTP

/*
Este servlet se encarga de asignar tareas a trabajadores. Primero, captura los datos enviados desde un 
formulario y realiza una validación rápida para asegurarse de que los campos obligatorios estén completos. 
Luego, convierte los IDs de cultivo y trabajador a enteros y llama al método correspondiente en la clase
TareaDAO para agregar la tarea en la base de datos. Dependiendo del resultado de la operación, redirige
al usuario a la página de tareas o de agregar tarea con mensajes de éxito o error.
*/
@WebServlet("/AsignarTareaServlet") // Define el servlet y su URL
public class AsignarTareaServlet extends HttpServlet { // Clase que extiende HttpServlet para manejar solicitudes HTTP

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) // Método que maneja solicitudes POST
            throws ServletException, IOException {

        try {
            // 1. Capturamos los datos
            String nombreTareaManual = request.getParameter("txtNombreTarea"); // Captura el nombre de la tarea desde el formulario
            String idCultivoStr = request.getParameter("cboCultivo"); // Captura el ID del cultivo seleccionado desde el formulario
            String descripcion = request.getParameter("txtDescripcion"); // Captura la descripción de la tarea
            String jornada = request.getParameter("cboJornada"); // Captura la jornada de trabajo seleccionada
            String idTrabajadorStr = request.getParameter("cboTrabajador"); // Captura el ID del trabajador asignado desde el formulario

            // DEBUG: Esto saldrá en la consola de tu IDE para que verifiques si llegan los datos
            System.out.println("--- INTENTO DE REGISTRO DE TAREA ---"); // Mensaje de depuración para indicar que se está intentando registrar una tarea
            System.out.println("Nombre Tarea: " + nombreTareaManual); // Imprime el nombre de la tarea en la consola
            System.out.println("ID Cultivo: " + idCultivoStr); // Imprime el ID del cultivo en la consola
            System.out.println("ID Trabajador: " + idTrabajadorStr); // Imprime el ID del trabajador en la consola

            // 2. Validación rápida para evitar el NumberFormatException
            // Verifica que los campos obligatorios no sean nulos
            if (idCultivoStr == null || idTrabajadorStr == null || nombreTareaManual == null) {
                System.out.println("ERROR: Uno de los campos llegó NULL del formulario"); // Mensaje de error si algún campo es nulo
                // Redirige a la página de agregar tarea con un estado de error si falta algún dato
                response.sendRedirect(request.getContextPath() + "/public/Supervisor/Agregar_Tarea.jsp?status=missing_data");
                return; // Sale del método si hay un error
            }

            // Convierte los IDs de cultivo y trabajador de cadenas a enteros
            int idCultivo = Integer.parseInt(idCultivoStr); // Convierte el ID del cultivo a tipo int
            int idTrabajador = Integer.parseInt(idTrabajadorStr); // Convierte el ID del trabajador a tipo int

            TareaDAO tdao = new TareaDAO(); // Crea una instancia de TareaDAO para interactuar con la base de datos
            // Llama al método para agregar una tarea manual en la base de datos
            boolean insertado = tdao.agregarTareaManual(idCultivo, descripcion, nombreTareaManual, jornada, idTrabajador);

            // Verifica si la inserción fue exitosa
            if (insertado) {
                System.out.println("REGISTRO EXITOSO"); // Mensaje de éxito si la tarea se registró correctamente
                // Redirige a la página de tareas con un estado de éxito
                response.sendRedirect(request.getContextPath() + "/public/Supervisor/Tareas.jsp?status=success");
            } else {
                System.out.println("ERROR: El DAO devolvió false (Error en el INSERT)"); // Mensaje de error si la inserción falló
                // Redirige a la página de agregar tarea con un estado de error
                response.sendRedirect(request.getContextPath() + "/public/Supervisor/Agregar_Tarea.jsp?status=error");
            }

        } catch (NumberFormatException e) { // Captura excepciones de formato numérico
            System.out.println("ERROR: No se pudo convertir un ID a número"); // Mensaje de error si la conversión falla
            e.printStackTrace(); // Imprime la traza de la excepción en la consola para depuración
            // Redirige a la página de agregar tarea con un estado de error por formato inválido
            response.sendRedirect(request.getContextPath() + "/public/Supervisor/Agregar_Tarea.jsp?status=invalid_format");
        } catch (Exception e) { // Captura cualquier otra excepción
            System.out.println("ERROR FATAL: " + e.getMessage()); // Mensaje de error fatal con detalles de la excepción
            e.printStackTrace(); // Imprime la traza de la excepción en la consola para depuración
            // Redirige a la página de agregar tarea con un estado de error fatal
            response.sendRedirect(request.getContextPath() + "/public/Supervisor/Agregar_Tarea.jsp?status=fatal_error");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) // Método que maneja solicitudes GET
            throws ServletException, IOException {
        // Redirige al formulario de agregar tarea si se accede mediante GET
        response.sendRedirect(request.getContextPath() + "/public/Supervisor/Agregar_Tarea.jsp");
    }
}