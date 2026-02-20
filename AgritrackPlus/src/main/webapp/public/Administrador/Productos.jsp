<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.agritrack.agritrackplus.DAO.ProductoDAO" %>
<%@ page import="java.util.List, java.util.Map" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Gestión de Productos</title>
  <link rel="stylesheet" href="../../asset/Administrador/style_Productos.css">
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
      <h1 class="titulo">Gestión de Inventario de Productos</h1>
      <a href="Añadir_Producto.jsp" class="boton">Añadir Producto</a>
    </div>
  </header>

  <main>
    <div class="buscador__contenedor">
      <input type="text" id="buscador" placeholder=" Buscar Producto..."/>
      <select id="filtroTipo">
        <option value="todos">Todos</option>
        <option value="Semilla">Semilla</option>
        <option value="Herramienta">Herramienta</option>
        <option value="Fertilizante">Fertilizante</option>
        <option value="Pesticida">Pesticida</option>
      </select>
    </div>

    <div class="main__contenedor">
      <%
        ProductoDAO dao = new ProductoDAO();
        List<Map<String, String>> productos = dao.listarProductos();
        if (productos.isEmpty()) {
      %>
        <p>No hay productos registrados.</p>
      <%
        } else {
          for (Map<String, String> producto : productos) {
      %>
        <section class="contendor__informacion contenedor__producto">
          <div class="caja__titulo">
            <img class="logo" src="../../asset/imagenes/stock.png" alt="icono producto"/>
            <h2 class="titulo">Información del Producto</h2>
          </div>
          <div class="contenedor__registros">
            <div class="fila__producto">
              <div class="dato__producto">
                <p><strong>Nombre:</strong> <%= producto.get("nombre") %></p>
              </div>
              <div class="dato__producto">
                <p><strong>Unidad de medida:</strong> <%= producto.get("unidad_medida") %></p>
              </div>
            </div>
            <div class="fila__producto">
              <div class="dato__producto">
                <p><strong>Precio:</strong> $<%= producto.get("precio") %></p>
              </div>
              <div class="dato__producto">
                <p><strong>Estado:</strong> <%= producto.get("estado") %></p>
              </div>
            </div>
            <div class="fila__producto">
              <div class="dato__producto">
                <p><strong>Fecha de compra:</strong> <%= producto.get("fecha_compra") %></p>
              </div>
              <div class="dato__producto">
                <p><strong>Fecha de vencimiento:</strong> <%= producto.get("fecha_vencimiento") %></p>
              </div>
            </div>
            <div class="fila__producto">
              <div class="dato__producto">
                <p><strong>Tipo de producto:</strong> <%= producto.get("tipo") %></p>
              </div>
            </div>
          </div>
        </section>
      <%
          }
        }
      %>
    </div>
  </main>
</body>
</html>