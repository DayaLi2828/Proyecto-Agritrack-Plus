package com.agritrack.agritrackplus.controlador; // Declara el paquete donde se encuentra la clase, que forma parte de la aplicación Agritrack Plus.

import com.agritrack.agritrackplus.DAO.TareaDAO; // Importa la clase TareaDAO, que maneja la lógica de acceso a datos relacionada con las tareas.
import com.agritrack.agritrackplus.DAO.UsuarioDAO; // Importa la clase UsuarioDAO, que maneja la lógica de acceso a datos para usuarios.
import java.io.IOException; // Importa la excepción IOException, que se lanza cuando hay un error de entrada/salida.
import jakarta.servlet.ServletException; // Importa la excepción ServletException, que se lanza cuando hay un problema en el servlet.
import jakarta.servlet.annotation.WebServlet; // Importa la anotación WebServlet, que define la configuración del servlet.
import jakarta.servlet.http.HttpServlet; // Importa la clase base HttpServlet, que proporciona la funcionalidad para manejar solicitudes HTTP.
import jakarta.servlet.http.HttpServletRequest; // Importa la clase HttpServletRequest, que representa la solicitud HTTP del cliente.
import jakarta.servlet.http.HttpServletResponse; // Importa la clase HttpServletResponse, que representa la respuesta HTTP que se enviará al cliente.
import jakarta.servlet.http.HttpSession; // Importa la clase HttpSession, que se utiliza para manejar la sesión del usuario.
import java.util.Map; // Importa la interfaz Map, que se utiliza para almacenar pares clave-valor.

@WebServlet("/LoginServlet") // Define el servlet con la URL "/LoginServlet" a la que responde.
public class LoginServlet extends HttpServlet { // Declara la clase LoginServlet que extiende HttpServlet, permitiendo manejar solicitudes HTTP.

    @Override // Indica que este método sobrescribe un método de la clase padre HttpServlet.
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException { 
        // Método que maneja las solicitudes POST. Recibe el objeto request (contiene datos de la solicitud) y el objeto response (para enviar la respuesta).

        String email = request.getParameter("email"); // Obtiene el parámetro "email" de la solicitud HTTP, que contiene el correo del usuario que intenta iniciar sesión.
        String pass = request.getParameter("password"); // Obtiene el parámetro "password" de la solicitud HTTP, que contiene la contraseña del usuario.

        System.out.println("---------- INTENTO DE LOGIN ----------"); // Imprime un mensaje en la consola para indicar que se está intentando un inicio de sesión.

        try {
            UsuarioDAO dao = new UsuarioDAO(); // Crea una instancia de UsuarioDAO para acceder a la lógica de validación de usuarios.
            Map<String, Object> user = dao.validarAcceso(email, pass); // Llama al método validarAcceso en UsuarioDAO, pasando el email y la contraseña, y obtiene un mapa con los datos del usuario si la validación es exitosa.

            if (user != null) { // Verifica si el usuario fue encontrado (es decir, si la validación fue exitosa).
                HttpSession session = request.getSession(); // Obtiene la sesión actual del usuario o crea una nueva si no existe.
                int idUsuario = (int) user.get("id"); // Extrae el ID del usuario del mapa devuelto por validarAcceso.
                String rol = (String) user.get("rol"); // Extrae el rol del usuario (trabajador, administrador, supervisor) del mapa devuelto.

                // Datos básicos de sesión
                session.setAttribute("usuario_id", idUsuario); // Almacena el ID del usuario en la sesión para su uso posterior.
                session.setAttribute("usuario_nombre", user.get("nombre")); // Almacena el nombre del usuario en la sesión.
                session.setAttribute("rol", rol); // Almacena el rol del usuario en la sesión.

                // --- CARGA DE DATOS PARA TRABAJADOR ---
                if ("trabajador".equalsIgnoreCase(rol)) { // Verifica si el rol del usuario es "trabajador".
                    // Usamos TareaDAO para obtener las estadísticas frescas
                    TareaDAO tareaDAO = new TareaDAO(); // Crea una instancia de TareaDAO para acceder a la lógica de tareas.
                    
                    // 1. Resumen de tareas (Líneas)
                    session.setAttribute("datosGrafico", tareaDAO.obtenerConteosPorEstado(idUsuario)); 
                    // Obtiene los conteos de tareas por estado para el usuario y los almacena en la sesión para usarlos en gráficos.

                    // 2. Cumplimiento por cultivo (Barras)
                    session.setAttribute("datosCultivos", tareaDAO.obtenerCumplimientoCultivos(idUsuario)); 
                    // Obtiene datos sobre el cumplimiento de cultivos para el usuario y los almacena en la sesión.

                    // 3. Resumen de pagos (Dona)
                    session.setAttribute("datosPago", tareaDAO.obtenerResumenPagos(idUsuario)); 
                    // Obtiene un resumen de pagos para el usuario y lo almacena en la sesión.

                    response.sendRedirect(request.getContextPath() + "/public/Trabajador/Trabajador.jsp"); 
                    // Redirige al usuario a la página de trabajador después de un inicio de sesión exitoso.
                } 
                else if ("administrador".equalsIgnoreCase(rol)) { // Verifica si el rol del usuario es "administrador".
                    response.sendRedirect(request.getContextPath() + "/public/Administrador/Admin.jsp"); 
                    // Redirige al usuario a la página de administrador.
                } 
                else if ("supervisor".equalsIgnoreCase(rol)) { // Verifica si el rol del usuario es "supervisor".
                    response.sendRedirect(request.getContextPath() + "/public/Supervisor/Supervisor.jsp"); 
                    // Redirige al usuario a la página de supervisor.
                } 
                else { // Si el rol no coincide con ninguno de los anteriores.
                    response.sendRedirect(request.getContextPath() + "/index.jsp?error=role"); 
                    // Redirige a la página de inicio con un mensaje de error indicando que el rol es inválido.
                }
            } else { // Si el usuario no fue encontrado (es decir, la validación falló).
                response.sendRedirect(request.getContextPath() + "/index.jsp?error=true"); 
                // Redirige a la página de inicio con un mensaje de error indicando que las credenciales son incorrectas.
            }
        } catch (Exception e) { // Captura cualquier excepción que ocurra durante el proceso de inicio de sesión.
            e.printStackTrace(); // Imprime la traza de la excepción en la consola para fines de depuración.
            response.sendRedirect(request.getContextPath() + "/index.jsp?error=db"); 
            // Redirige a la página de inicio con un mensaje de error indicando que hubo un problema con la base de datos.
        }
    }
}