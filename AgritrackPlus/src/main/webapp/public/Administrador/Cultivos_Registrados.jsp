<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.agritrack.agritrackplus.DAO.Registro_CultivoDAO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Cultivos Registrados</title>
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
                <img class="logo" src="../../asset/imagenes/hoja (3).png" alt="hoja del logo"/>
            </div>
            <h1 class="titulo">Cultivos Registrados</h1>
        </div>
    </header>

    <main>
        <div class="buscador__contenedor">
            <input type="text" id="buscador" placeholder=" Buscar cultivo por nombre..."/>
            <select id="filtroEstado">
                <option value="todos">Todos</option>
                <option value="activo">Activo</option>
                <option value="inactivo">Inactivo</option>
            </select>
        </div>

        <div class="contenedor__tarjetas">
            <%
                // Instanciamos el DAO y obtenemos la lista
                Registro_CultivoDAO dao = new Registro_CultivoDAO();
                List<Map<String, String>> cultivos = dao.listarCultivos();

                // Depuración rápida: Esto saldrá en la consola de NetBeans (Output)
                if (cultivos != null) {
                    System.out.println("DEBUG: Se encontraron " + cultivos.size() + " cultivos en la base de datos.");
                }

                if (cultivos == null || cultivos.isEmpty()) {
            %>
                <div class="mensaje-vacio">
                    <p>No hay cultivos registrados actualmente.</p>
                </div>
            <%
                } else {
                    for (Map<String, String> cultivo : cultivos) {
                        // Aseguramos que el ID y Nombre no sean nulos para evitar errores visuales
                        String id = (cultivo.get("id") != null) ? cultivo.get("id") : "0";
                        String nombre = (cultivo.get("nombre") != null) ? cultivo.get("nombre") : "Sin nombre";
                        String estado = (cultivo.get("estado") != null) ? cultivo.get("estado") : "Activo";
                        String siembra = (cultivo.get("fecha_siembra") != null) ? cultivo.get("fecha_siembra") : "No registrada";
                        String cosecha = (cultivo.get("fecha_cosecha") != null) ? cultivo.get("fecha_cosecha") : "Pendiente";
            %>
                <div class="tarjeta__cultivo">
                    <h3><%= nombre %></h3>
                    <p><strong>Siembra:</strong> <%= siembra %></p>
                    <p><strong>Cosecha:</strong> <%= cosecha %></p>
                    
                    <div class="estado-contenedor">
                        <p>Estado:</p>
                        <form action="<%=request.getContextPath()%>/CambiarEstadoCultivoServlet" method="post" class="form-estado">
                            <input type="hidden" name="id" value="<%= id %>">
                            <input type="hidden" name="estadoActual" value="<%= estado %>">
                            <button type="submit" class="btn-estado-toggle <%= estado.toLowerCase() %>">
                                <%= estado %>
                            </button>
                        </form>
                    </div>

                    <a href="Detalles_Cultivo.jsp?id=<%= id %>" class="boton__ver">Ver cultivo</a>
                    <a href="Editar_Cultivo.jsp?id=<%= id %>" class="boton__editar" style="background:#f39c12; color:white; padding:5px; border-radius:5px; text-decoration:none; display:inline-block; margin-top:5px;">Editar</a>
                </div>
            <%
                    }
                }
            %>
        </div>
    </main>

    <script>
        function filtrar(){
            let texto = document.getElementById("buscador").value.toLowerCase();
            let estadoFiltro = document.getElementById("filtroEstado").value.toLowerCase();
            let tarjetas = document.querySelectorAll(".tarjeta__cultivo");

            tarjetas.forEach(function(tarjeta){
                let nombre = tarjeta.querySelector("h3").textContent.toLowerCase();
                let estadoCultivo = tarjeta.querySelector(".btn-estado-toggle").textContent.toLowerCase().trim();
                let coincideNombre = nombre.includes(texto);
                let coincideEstado = (estadoFiltro === "todos" || estadoCultivo === estadoFiltro);

                tarjeta.style.display = (coincideNombre && coincideEstado) ? "block" : "none";
            });
        }
        document.getElementById("buscador").addEventListener("input", filtrar);
        document.getElementById("filtroEstado").addEventListener("change", filtrar);
    </script>
</body>
</html>