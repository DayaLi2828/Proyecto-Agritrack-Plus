package com.agritrack.agritrackplus.controlador; // Paquete que contiene la clase del controlador para agregar productos
import com.agritrack.agritrackplus.DAO.ProductoDAO; // Importación de la clase ProductoDAO para interactuar con la base de datos de productos
import java.io.IOException; // Importación de la excepción para manejar errores de entrada/salida
import jakarta.servlet.ServletException; // Importación de la excepción para manejar errores en servlets
import jakarta.servlet.annotation.WebServlet; // Importación de la anotación para definir un servlet
import jakarta.servlet.http.HttpServlet; // Importación de la clase base para servlets HTTP
import jakarta.servlet.http.HttpServletRequest; // Importación para manejar solicitudes HTTP
import jakarta.servlet.http.HttpServletResponse; // Importación para manejar respuestas HTTP

/*Este servlet se encarga de agregar o editar un producto en el sistema. 
Primero, captura los datos enviados desde un formulario y valida que los campos obligatorios 
estén completos. Luego, dependiendo de si se está editando un producto existente o creando uno nuevo, 
llama a los métodos apropiados en la clase ProductoDAO para realizar la operación en la base de datos. 
Finalmente, redirige al usuario a la página correspondiente con mensajes de éxito o error según el 
resultado de la operación.*/

@WebServlet(name = "AgregarProductoServlet", urlPatterns = {"/AgregarProductoServlet"}) // Define el servlet y su URL
public class AgregarProductoServlet extends HttpServlet { // Clase que extiende HttpServlet para manejar solicitudes HTTP

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) // Método que maneja solicitudes POST
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8"); // Establece la codificación de caracteres para la solicitud a UTF-8

        try {
            // 1. Obtención de parámetros (incluyendo el ID para edición)
            String idStr = request.getParameter("id"); // Captura el ID del producto (si está presente, para editar)
            String nombre = request.getParameter("nombre"); // Captura el nombre del producto
            String unidadMedida = request.getParameter("unidad_medida"); // Captura la unidad de medida del producto
            String precioStr = request.getParameter("precio"); // Captura el precio del producto como cadena
            String fechaCompra = request.getParameter("fecha_compra"); // Captura la fecha de compra del producto
            String fechaVencimiento = request.getParameter("fecha_vencimiento"); // Captura la fecha de vencimiento del producto
            String tipoProducto = request.getParameter("tipo_producto_id"); // Captura el ID del tipo de producto
            String cantidadStr = request.getParameter("cantidad"); // Captura la cantidad del producto como cadena

            // 2. Validación de campos obligatorios
            // Verifica que los campos requeridos no estén vacíos
            if (nombre == null || nombre.isEmpty() || 
                precioStr == null || precioStr.isEmpty() ||
                tipoProducto == null || tipoProducto.isEmpty() || 
                cantidadStr == null || cantidadStr.isEmpty()) {
                
                // Redirige a la página de agregar producto con un error si faltan campos obligatorios
                response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Producto.jsp?error=true");
                return; // Sale del método
            }

            // 3. Conversión de tipos de datos
            // Convierte el precio y la cantidad de cadenas a sus tipos numéricos correspondientes
            double precio = Double.parseDouble(precioStr); // Convierte el precio a tipo double
            int cantidad = Integer.parseInt(cantidadStr); // Convierte la cantidad a tipo int

            // Verifica si la fecha de vencimiento está vacía y la establece en null si es necesario
            if (fechaVencimiento == null || fechaVencimiento.trim().isEmpty()) {
                fechaVencimiento = null; // Asigna null si no hay fecha de vencimiento
            }

            ProductoDAO dao = new ProductoDAO(); // Crea una instancia de ProductoDAO para interactuar con la base de datos
            
            // 4. Lógica de Decisión: ¿Editar o Insertar?
            // Verifica si se está editando un producto existente o agregando uno nuevo
            if (idStr != null && !idStr.isEmpty()) {
                // MODO EDICIÓN
                int id = Integer.parseInt(idStr); // Convierte el ID del producto a tipo int
                int tipoId = Integer.parseInt(tipoProducto); // Convierte el ID del tipo de producto a tipo int
                
                // Llama al método para editar el producto en la base de datos
                boolean exito = dao.editarProducto(id, nombre, unidadMedida, precio, cantidad, tipoId);
                
                // Verifica si la edición fue exitosa y redirige a la página correspondiente
                if (exito) {
                    response.sendRedirect(request.getContextPath() + "/public/Administrador/Productos.jsp?actualizacion=exitosa");
                } else {
                    // Redirige a la página de agregar producto con un error si la edición falló
                    response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Producto.jsp?id=" + id + "&error=true");
                }
            } else {
                // MODO REGISTRO NUEVO
                // Llama al método para insertar un nuevo producto en la base de datos
                boolean exito = dao.insertarProducto(nombre, tipoProducto, unidadMedida, precio, cantidad, fechaCompra, fechaVencimiento);
                
                // Verifica si la inserción fue exitosa y redirige a la página correspondiente
                if (exito) {
                    response.sendRedirect(request.getContextPath() + "/public/Administrador/Productos.jsp?registro=exitoso");
                } else {
                    // Redirige a la página de agregar producto con un error si la inserción falló
                    response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Producto.jsp?error=true");
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace(); // Imprime la traza de la excepción en la consola para depuración
            // Redirige a la página de agregar producto con un error en caso de excepción
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Producto.jsp?error=true");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) // Método que maneja solicitudes GET
            throws ServletException, IOException {
        // Redirige a la página de agregar producto si se accede mediante GET
        response.sendRedirect(request.getContextPath() + "/public/Administrador/Agregar_Producto.jsp");
    }
}