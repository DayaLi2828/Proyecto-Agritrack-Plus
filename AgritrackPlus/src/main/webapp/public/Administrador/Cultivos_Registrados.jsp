<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.agritrack.agritrackplus.DAO.Registro_CultivoDAO" %>
<%@ page import="java.util.List, java.util.Map" %>
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
        <img src="../../asset/imagenes/devolver.png" id="icono de devolver">
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
        <option value="Activo">Activo</option>
        <option value="Inactivo">Inactivo</option>
      </select>
    </div>

    <div class="contenedor__tarjetas">
      <%
        Registro_CultivoDAO dao = new Registro_CultivoDAO();
        List<Map<String, String>> cultivos = dao.listarCultivos();
        if (cultivos.isEmpty()) {
      %>
        <p>No hay cultivos registrados.</p>
      <%
        } else {
          for (Map<String, String> cultivo : cultivos) {
      %>
        <div class="tarjeta__cultivo">
          <h3><%= cultivo.get("nombre") %></h3>
          <p>Siembra: <%= cultivo.get("fecha_siembra") %></p>
          <p>Estado: <%= cultivo.get("estado") %></p>
          <a href="Detalles_Cultivo.jsp?id=<%= cultivo.get("id") %>" class="boton__ver">Ver cultivo</a>
        </div>
      <%
          }
        }
      %>
    </div>
  </main>
    <script>
        function filtrar() {
          let texto = document.getElementById("buscador").value.toLowerCase();
          let estado = document.getElementById("filtroEstado").value.toLowerCase();
          let tarjetas = document.querySelectorAll(".tarjeta__cultivo");

          tarjetas.forEach(function(tarjeta) {
            let nombre = tarjeta.querySelector("h3").textContent.toLowerCase();
            let estadoCultivo = tarjeta.querySelector("p:nth-child(3)").textContent.toLowerCase().trim();

            let coincideNombre = nombre.includes(texto);
            let coincideEstado = estado === "todos" || estadoCultivo === "estado: " + estado;

            if (coincideNombre && coincideEstado) {
              tarjeta.style.display = "block";
            } else {
              tarjeta.style.display = "none";
            }
          });
        }

        document.getElementById("buscador").addEventListener("input", filtrar);
        document.getElementById("filtroEstado").addEventListener("change", filtrar);
    </script>
</body>
</html>