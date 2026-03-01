<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.agritrack.agritrackplus.DAO.Registro_CultivoDAO" %>
<%@ page import="java.util.List, java.util.Map" %>
<%
    String id = request.getParameter("id");
    if (id == null) { response.sendRedirect("Cultivos_Registrados.jsp"); return; }
    
    Registro_CultivoDAO dao = new Registro_CultivoDAO();
    Map<String, String> cultivo = dao.obtenerPorId(id);
    List<Map<String, String>> productos = dao.obtenerProductosCultivo(Integer.parseInt(id));
    Map<String, String> supervisor = dao.obtenerSupervisorCultivo(Integer.parseInt(id));
    List<Map<String, String>> trabajadores = dao.obtenerTrabajadoresCultivo(Integer.parseInt(id));
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Detalle del cultivo</title>
    <link rel="stylesheet" href="../../asset/Administrador/style-Detalles-cultivo.css"/>
</head>
<body>
    <header>
        <a href="Cultivos_Registrados.jsp">
            <div class="icono__devolver">
                <img src="../../asset/imagenes/devolver.png" alt="volver">
            </div>
        </a>
        <div class="contenedor__titulo">
            <div class="contenedor__logo">
                <img class="logo" src="../../asset/imagenes/hoja (3).png" alt="logo"/>
            </div>
            <h1 class="titulo">Detalle del Cultivo</h1>
            <a href="Editar_Cultivo.jsp?id=<%= id %>" class="boton">Editar Cultivo</a>
        </div>
    </header>

    <main>
        <div class="main__contenedor">
            <section class="contendor__informacion contenedor__cultivo">
                <div class="caja__titulo">
                    <img class="logo" src="../../asset/imagenes/planta (2).png" alt="cultivo"/>
                    <h2 class="titulo">Información General</h2>
                </div>
                <div class="contenedor__registros">
                    <div class="fila__cultivo">
                        <div class="dato__cultivo"><p><strong>Nombre:</strong> <%= cultivo.get("nombre") %></p></div>
                        <div class="dato__cultivo"><p><strong>Fecha Siembra:</strong> <%= cultivo.get("fecha_siembra") %></p></div>
                    </div>
                    <div class="fila__cultivo">
                        <div class="dato__cultivo"><p><strong>Ciclo:</strong> <%= cultivo.get("ciclo") %></p></div>
                        <div class="dato__cultivo"><p><strong>Estado:</strong> <%= cultivo.get("estado") %></p></div>
                    </div>
                </div>
            </section>

            <section class="contendor__informacion contenedor__stock">
                <div class="caja__titulo">
                    <img class="logo" src="../../asset/imagenes/stock.png" alt="stock"/>
                    <h2 class="titulo">Insumos Utilizados</h2>
                </div>
                <% if (productos.isEmpty()) { %>
                    <p>No hay productos registrados.</p>
                <% } else { 
                    for (Map<String, String> p : productos) { %>
                    <div class="fila__stock">
                        <div class="item__stock"><p><%= p.get("nombre") %></p></div>
                        <div class="cantidad__stock"><strong><%= p.get("cantidad") %></strong></div>
                    </div>
                <% } } %>
            </section>

            <section class="contendor__informacion contenedor__admin">
                <div class="caja__titulo">
                    <img class="logo" src="../../asset/imagenes/supervisor.png" alt="supervisor"/>
                    <h2 class="titulo">Responsable</h2>
                </div>
                <div class="info__admin">
                    <% if (supervisor.isEmpty()) { %>
                        <p>Sin supervisor asignado.</p>
                    <% } else { %>
                        <h3><%= supervisor.get("nombre") %></h3>
                    <% } %>
                </div>
            </section>

            <section class="contendor__informacion contenedor__trab">
                <div class="caja__titulo">
                    <img class="logo" src="../../asset/imagenes/obrero.png" alt="obreros"/>
                    <h2 class="titulo">Equipo de Trabajo</h2>
                </div>
                <div class="fila__trab">
                    <% if (trabajadores.isEmpty()) { %>
                        <p>No hay trabajadores asignados.</p>
                    <% } else { 
                        for (Map<String, String> t : trabajadores) { 
                            String foto = t.get("foto");
                            String imgSrc = (foto != null && !foto.isEmpty()) ? "../../asset/imagenes/trabajadores/" + foto : "../../asset/imagenes/obrero.png";
                    %>
                        <div class="bloque__trab">
                            <img class="logo__trab" src="<%= imgSrc %>" alt="trabajador" onerror="this.src='../../asset/imagenes/obrero.png';">
                            <div class="info__trabajador">
                                <h3 class="titulo__trabajador"><%= t.get("nombre") %></h3>
                            </div>
                        </div>
                    <% } } %>
                </div>
            </section>
        </div>
    </main>
</body>
</html>