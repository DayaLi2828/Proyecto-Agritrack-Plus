<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.agritrack.agritrackplus.DAO.Registro_CultivoDAO" %>
<%@ page import="java.util.List, java.util.Map" %>
<%@ page import="java.util.List, java.util.Map" %>
<%
  String id = request.getParameter("id");
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
        <img src="../../asset/imagenes/devolver.png" id="icono de devolver">
      </div>
    </a>
    <div class="contenedor__titulo">
      <div class="contenedor__logo">
        <img class="logo" src="../../asset/imagenes/hoja (3).png" alt="hoja del logo"/>
      </div>
      <h1 class="titulo">Detalle del Cultivo</h1>
      <a href="Editar_Cultivo.jsp?id=<%= id %>" class="boton">Editar Cultivo</a>
    </div>
  </header>

  <main>
    <div class="main__contenedor">

      <!-- Información del cultivo -->
      <section class="contendor__informacion contenedor__cultivo">
        <div class="caja__titulo">
          <img class="logo" src="../../asset/imagenes/planta (2).png" alt="icono cultivo"/>
          <h2 class="titulo">Información del Cultivo</h2>
        </div>
        <div class="contenedor__registros">
          <div class="fila__cultivo">
            <div class="dato__cultivo"><p><strong>Nombre:</strong> <%= cultivo.get("nombre") %></p></div>
            <div class="dato__cultivo"><p><strong>Fecha de Siembra:</strong> <%= cultivo.get("fecha_siembra") %></p></div>
          </div>
          <div class="fila__cultivo">
            <div class="dato__cultivo">
              <p><strong>Fecha de Cosecha:</strong> 
                <%= cultivo.get("fecha_cosecha") != null && !cultivo.get("fecha_cosecha").equals("null") ? cultivo.get("fecha_cosecha") : "No definida aún" %>
              </p>
            </div>
            <div class="dato__cultivo"><p><strong>Ciclo Actual:</strong> <%= cultivo.get("ciclo") %></p></div>
          </div>
          <div class="fila__cultivo">
            <div class="dato__cultivo"><p><strong>Estado:</strong> <%= cultivo.get("estado") %></p></div>
          </div>
        </div>
      </section>

      <!-- Stock -->
        <section class="contendor__informacion contenedor__stock">
          <div class="caja__titulo">
            <img class="logo" src="../../asset/imagenes/stock.png" alt="icono stock"/>
            <h2 class="titulo">Stock de Productos</h2>
          </div>
          <% if (productos.isEmpty()) { %>
            <div class="fila__stock">
              <div class="item__stock"><p>No hay productos asignados aún.</p></div>
            </div>
          <% } else {
              for (Map<String, String> producto : productos) {
                String unidad = producto.get("unidad_medida");
                String unidadTexto = "1".equals(unidad) || "1.0".equals(unidad) ? "Kg" :
                                     "2".equals(unidad) || "2.0".equals(unidad) ? "Litros" : "Unidades";
          %>
            <div class="fila__stock">
              <div class="item__stock"><p><strong>Producto:</strong> <%= producto.get("nombre") %></p></div>
              <div class="cantidad__stock">
                <strong><%= producto.get("cantidad") %> <%= unidadTexto %></strong>
              </div>
            </div>
          <% } } %>
        </section>

        <!-- Supervisor -->
        <section class="contendor__informacion contenedor__admin">
          <div class="caja__titulo">
            <img class="logo" src="../../asset/imagenes/supervisor.png" alt="icono supervisor"/>
            <h2 class="titulo">Supervisor de Campo</h2>
          </div>
          <img class="logo__admin" src="../../asset/imagenes/admin.png" alt="imagen de usuario"/>
          <div class="info__admin">
            <% if (supervisor.isEmpty()) { %>
              <p>No hay supervisor asignado aún.</p>
            <% } else { %>
              <h3 class="titulo__admin"><%= supervisor.get("nombre") %></h3>
              <p>Supervisor asignado</p>
            <% } %>
          </div>
        </section>

      <!-- Trabajadores -->
        <section class="contendor__informacion contenedor__trab">
            <div class="caja__titulo">
              <img class="logo" src="../../asset/imagenes/obrero.png" alt="icono trabajadores"/>
              <h2 class="titulo">Trabajadores</h2>
            </div>
            <% if (trabajadores.isEmpty()) { %>
              <p>No hay trabajadores asignados aún.</p>
            <% } else { %>
              <div class="fila__trab">
                <% for (Map<String, String> trabajador : trabajadores) { %>
                  <div class="bloque__trab">
                    <%
                      String foto = trabajador.get("foto");
                      String imgSrc = (foto != null && !foto.equals("null") && !foto.isEmpty())
                        ? "../../asset/imagenes/trabajadores/" + foto
                        : "../../asset/imagenes/obrero.png";
                    %>
                    <img class="logo__trab" src="<%= imgSrc %>" alt="imagen trabajador"/>
                    <div class="info__trabajador">
                      <h3 class="titulo__trabajador"><%= trabajador.get("nombre") %></h3>
                    </div>
                  </div>
                <% } %>
              </div>
            <% } %>
        </section>

    </div>
  </main>
</body>
</html>