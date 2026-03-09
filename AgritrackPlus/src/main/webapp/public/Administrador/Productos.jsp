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
    <%-- Mantenemos tu hoja de estilo de cultivos para las tarjetas --%>
    <link rel="stylesheet" href="../../asset/Administrador/style_CultivosRegistrados.css"/>
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
        </div>
    </header>

    <main>
        <%-- Bloque de alertas (opcional, basado en tus parámetros) --%>
        <% if ("exitoso".equals(request.getParameter("registro"))) { %>
            <div id="alerta-sistema" class="alerta-exito">¡Producto registrado con éxito!</div>
        <% } %>

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
                // Usamos el DAO que ya corregimos
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
                        <%-- Mantenemos tu lógica de botones de estado --%>
                        <button type="button" class="btn-estado-toggle <%= estado.toLowerCase() %>" style="cursor: default;">
                            <%= estado %>
                        </button>
                    </div>

                    <%-- Contenedor de acciones con el estilo que pediste --%>
                    <div class="acciones-contenedor">
                        <a href="Editar_Producto.jsp?id=<%= id %>" class="boton__editar">Editar</a>
                        
                        <%-- Mantenemos la ruta de tu servlet de eliminar --%>
                        <form action="<%=request.getContextPath()%>/EliminarProductoServlet" method="post" 
                              onsubmit="return confirm('¿Seguro que deseas eliminar este producto?');" 
                              style="display: contents;">
                            <input type="hidden" name="id" value="<%= id %>">
                            <button type="submit" class="boton__eliminar">Eliminar</button>
                        </form>
                    </div>
                </div>
            <%
                    }
                }
            %>
        </div>
    </main>

    <script>
        // Buscador idéntico al de cultivos
        function filtrar(){
            let texto = document.getElementById("buscador").value.toLowerCase();
            let estadoFiltro = document.getElementById("filtroEstado").value.toLowerCase();
            let tarjetas = document.querySelectorAll(".tarjeta__cultivo");

            tarjetas.forEach(function(tarjeta){
                let nombre = tarjeta.querySelector("h3").textContent.toLowerCase();
                let estadoProd = tarjeta.querySelector(".btn-estado-toggle").textContent.toLowerCase().trim();
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