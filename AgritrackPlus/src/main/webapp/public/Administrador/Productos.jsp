<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.agritrack.agritrackplus.DAO.ProductoDAO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Gestión de Productos</title>
    <link rel="stylesheet" href="../../asset/Administrador/style_CultivosRegistrados.css"/>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>
    <header>
        <a href="Admin.jsp">
            <div class="icono__devolver">
                <img src="../../asset/imagenes/devolver.png" alt="Volver">
            </div>
        </a>
        <div class="contenedor__titulo">
            <div class="contenedor__logo">
                <img class="logo" src="../../asset/imagenes/hoja (3).png" alt="Logo Agritrack"/>
            </div>
            <h1 class="titulo">Inventario de Productos</h1>
            <a href="Agregar_Producto.jsp" class="boton">Añadir Producto</a>
        </div>
    </header>

    <main>
        <div class="buscador__contenedor">
            <input type="text" id="buscador" placeholder=" Buscar producto por nombre..."/>
            <select id="filtroEstado">
                <option value="todos">Todos</option>
                <option value="activo">Activo</option>
                <option value="inactivo">Inactivo</option>
            </select>
        </div>

        <div class="contenedor__tarjetas">
            <%
                ProductoDAO dao = new ProductoDAO();
                List<Map<String, String>> productos = dao.listarProductos();

                if (productos == null || productos.isEmpty()) {
            %>
                <div class="mensaje-vacio">
                    <p>No hay productos registrados actualmente.</p>
                </div>
            <%
                } else {
                    for (Map<String, String> p : productos) {
                        String id = p.get("id");
                        String nombre = (p.get("nombre") != null) ? p.get("nombre") : "Sin nombre";
                        String stock = (p.get("cantidad") != null) ? p.get("cantidad") : "0";
                        String unidad = (p.get("unidad_medida") != null) ? p.get("unidad_medida") : "";
                        String precio = (p.get("precio") != null) ? p.get("precio") : "0.00";
                        String estado = (p.get("estado") != null) ? p.get("estado") : "Activo";
            %>
                <div class="tarjeta__cultivo">
                    <h3><%= nombre %></h3>
                    <p><strong>Stock disponible:</strong> <%= stock %> <%= unidad %></p>
                    <p><strong>Precio unitario:</strong> $<%= precio %></p>
                    
                    <div class="estado-contenedor">
                        <p>Estado:</p>
                        <form action="<%=request.getContextPath()%>/CambiarEstadoProductoServlet" method="post" class="form-estado">
                            <input type="hidden" name="id" value="<%= id %>">
                            <input type="hidden" name="estadoActual" value="<%= estado %>">
                            <button type="submit" class="btn-estado-toggle <%= estado.toLowerCase() %>">
                                <%= estado %>
                            </button>
                        </form>
                    </div>

                    <div class="acciones-contenedor">
                        <a href="Agregar_Producto.jsp?id=<%= id %>" class="boton__editar">Editar</a>                        
                        
                        <%-- Botón de eliminación con SweetAlert2 --%>
                        <button type="button" class="boton__eliminar" onclick="confirmarEliminarProducto('<%= id %>', '<%= nombre %>')">
                            Eliminar
                        </button>
                    </div>
                </div>
            <%
                    }
                }
            %>
        </div>
    </main>

    <script>
        // 1. Alertas de éxito al cargar (Registro / Actualización / Estado)
        window.onload = function() {
            const urlParams = new URLSearchParams(window.location.search);
            const registro = urlParams.get('registro');
            const actualizacion = urlParams.get('actualizacion');
            const mensaje = urlParams.get('mensaje');

            let config = {
                timer: 3000,
                timerProgressBar: true,
                showConfirmButton: false,
                icon: 'success'
            };

            if (registro === 'exitoso') {
                Swal.fire({ ...config, title: '¡Producto Registrado!', text: 'Se ha añadido correctamente al inventario.' });
            } else if (actualizacion === 'exitosa') {
                Swal.fire({ ...config, title: '¡Actualización Exitosa!', text: 'Los datos del producto han sido modificados.' });
            } else if (mensaje === 'estado') {
                Swal.fire({ ...config, title: 'Estado Cambiado', text: 'La disponibilidad del producto ha sido actualizada.', icon: 'info' });
            } else if (mensaje === 'eliminado') {
                Swal.fire({ ...config, title: '¡Eliminado!', text: 'El producto se retiró del inventario.' });
            }

            // Limpiar URL
            window.history.replaceState({}, document.title, window.location.pathname);
        };

        // 2. Función para confirmar eliminación con SweetAlert2
        function confirmarEliminarProducto(id, nombre) {
            Swal.fire({
                title: '¿Eliminar producto?',
                text: "Estás por borrar: " + nombre + ". Esta acción es permanente.",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#28a745',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Sí, eliminar',
                cancelButtonText: 'Cancelar'
            }).then((result) => {
                if (result.isConfirmed) {
                    // Crear y enviar formulario dinámico para POST
                    const form = document.createElement('form');
                    form.method = 'POST';
                    form.action = '<%=request.getContextPath()%>/EliminarProductoServlet';
                    
                    const inputId = document.createElement('input');
                    inputId.type = 'hidden';
                    inputId.name = 'id';
                    inputId.value = id;
                    
                    form.appendChild(inputId);
                    document.body.appendChild(form);
                    form.submit();
                }
            });
        }

        // 3. Buscador y Filtro
        function filtrar(){
            let texto = document.getElementById("buscador").value.toLowerCase();
            let estadoFiltro = document.getElementById("filtroEstado").value.toLowerCase();
            let tarjetas = document.querySelectorAll(".tarjeta__cultivo");

            tarjetas.forEach(function(tarjeta){
                let nombre = tarjeta.querySelector("h3").textContent.toLowerCase();
                let estadoBtn = tarjeta.querySelector(".btn-estado-toggle");
                let estadoProd = estadoBtn ? estadoBtn.textContent.toLowerCase().trim() : "";
                
                let coincideNombre = nombre.includes(texto);
                let coincideEstado = (estadoFiltro === "todos" || estadoProd === estadoFiltro);

                tarjeta.style.display = (coincideNombre && coincideEstado) ? "block" : "none";
            });
        }
        document.getElementById("buscador").addEventListener("input", filtrar);
        document.getElementById("filtroEstado").addEventListener("change", filtrar);
    </script>
</body>
</html>