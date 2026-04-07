package com.agritrack.agritrackplus.controlador; // Paquete donde se encuentra la clase controlador

import com.agritrack.agritrackplus.DAO.TareaDAO; // Importación de la clase TareaDAO para interactuar con la base de datos
import jakarta.servlet.ServletException; // Importación de la excepción para manejar errores en servlets
import jakarta.servlet.annotation.WebServlet; // Importación de la anotación para definir un servlet
import jakarta.servlet.http.HttpServlet; // Importación de la clase base para servlets HTTP
import jakarta.servlet.http.HttpServletRequest; // Importación para manejar solicitudes HTTP
import jakarta.servlet.http.HttpServletResponse; // Importación para manejar respuestas HTTP
import jakarta.servlet.http.HttpSession; // Importación para manejar sesiones de usuario
import java.io.IOException; // Importación de la excepción para manejar errores de entrada/salida
import java.util.Map; // Importación de la clase Map (no se utiliza en este código)

//servlet que se encarga de actualizar el estado de una tarea en un sistema de gestión.
//Asegura que solo los usuarios con el rol adecuado puedan realizar la acción, valida los parámetros de
//entrada, actualiza el estado en la base de datos y redirige a la página correspondiente con un mensaje 
//de éxito o error según sea necesario
@WebServlet("/ActualizarEstadoTareaServlet") // Define la URL del servlet que será invocado
public class ActualizarEstadoTareaServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Obtiene la sesión actual; si no existe, devuelve null
        HttpSession sesion = request.getSession(false);
        // Obtiene el rol del usuario desde la sesión
        String rol = (sesion != null) ? (String) sesion.getAttribute("rol") : null;
        // Obtiene el ID del usuario desde la sesión
        Integer idUsuario = (sesion != null) ? (Integer) sesion.getAttribute("usuario_id") : null;

        // Validación de seguridad: se asegura que el usuario esté autenticado y tenga el rol correcto
        if (idUsuario == null || !"trabajador".equalsIgnoreCase(rol)) {
            // Redirige a la página de inicio con un mensaje de error si el acceso es denegado
            response.sendRedirect(request.getContextPath() + "/index.jsp?error=acceso_denegado");
            return; // Sale del método
        }

        // Obtiene el ID de la tarea y el nuevo estado desde los parámetros de la solicitud
        String idTareaStr = request.getParameter("idTarea");
        String nuevoEstado = request.getParameter("estado");
        // Obtiene la observación, si no existe, la inicializa como cadena vacía
        String observacion = request.getParameter("txtObservacion"); 
        
        if (observacion == null) observacion = ""; // Asegura que la observación no sea nula
        observacion = observacion.trim(); // Elimina espacios en blanco al inicio y al final de la observación
        
        // Verifica que el ID de la tarea y el nuevo estado no sean nulos o vacíos
        if (idTareaStr == null || nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            // Redirige a la página de completar tareas con un mensaje de error si los parámetros son incorrectos
            response.sendRedirect(request.getContextPath() + "/public/Trabajador/Completar_Tareas.jsp?status=error_parametros");
            return; // Sale del método
        }

        try {
            // Intenta convertir el ID de la tarea de String a int
            int idTarea = Integer.parseInt(idTareaStr);
            String estadoNormalizado; // Variable para almacenar el estado normalizado

            // Normalización estricta del estado de la tarea basado en el valor proporcionado
            switch (nuevoEstado.toLowerCase()) {
                case "completada":  
                    estadoNormalizado = "Completada"; // Normaliza a "Completada"
                    break;
                case "en proceso":
                case "proceso":     
                    estadoNormalizado = "En Proceso"; // Normaliza a "En Proceso"
                    break;
                default:            
                    estadoNormalizado = "Pendiente";  // Por defecto, se establece como "Pendiente"
                    break;
            }

            // Crea una instancia de TareaDAO para interactuar con la base de datos
            TareaDAO dao = new TareaDAO();
            // Llama al método para actualizar el estado de la tarea en la base de datos
            boolean actualizado = dao.actualizarEstadoTarea(idTarea, estadoNormalizado, observacion);

            // Verifica si la actualización fue exitosa
            if (actualizado) {
                // REFRESCAR DATOS PARA GRÁFICOS: actualiza los datos de sesión con nuevos conteos
                sesion.setAttribute("datosGrafico", dao.obtenerConteosPorEstado(idUsuario));
                sesion.setAttribute("datosCultivos", dao.obtenerCumplimientoCultivos(idUsuario));
                sesion.setAttribute("datosPago", dao.obtenerResumenPagos(idUsuario));

                // Redirige a la lista de tareas con un mensaje de éxito
                response.sendRedirect(request.getContextPath() + "/public/Trabajador/Completar_Tareas.jsp?status=success");
            } else {
                // Redirige a la página de completar tareas con un mensaje de error si la actualización falló
                response.sendRedirect(request.getContextPath() + "/public/Trabajador/Completar_Tareas.jsp?status=error_db");
            }

        } catch (NumberFormatException e) {
            // Redirige a la página de completar tareas con un mensaje de error si hay un problema al convertir el ID
            response.sendRedirect(request.getContextPath() + "/public/Trabajador/Completar_Tareas.jsp?status=error_id");
        }
    }
}
