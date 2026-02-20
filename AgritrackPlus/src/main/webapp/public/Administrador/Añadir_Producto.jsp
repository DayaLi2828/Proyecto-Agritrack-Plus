<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Añadir Producto</title>
  <link rel="stylesheet" href="../../asset/Administrador/style_añadir_producto.css">
</head>
<body>
  <header>
    <a href="Productos.jsp">
      <div class="icono__devolver">
        <img src="../../asset/imagenes/devolver.png" id="icono de devolver">
      </div>
    </a>
    <div class="contenedor__titulo">
      <div class="contenedor__logo">
        <img class="logo" src="../../asset/imagenes/hoja (3).png" alt="hoja del logo"/>
      </div>
      <h1 class="titulo">Añadir Nuevo Producto</h1>
    </div>
  </header>
  <main>
    <div class="contenedor__ingreso">
      <div class="contendor__subtitulo">
        <div class="caja__logo">
          <img class="logo" src="../../asset/imagenes/stock.png" alt="logo planta">
        </div>
        <h2 class="subtitulo">Añadir producto</h2>
      </div>

      <% if ("true".equals(request.getParameter("error"))) { %>
        <p style="color:red;">Hubo un error al agregar el producto. Intenta de nuevo.</p>
      <% } %>

      <form class="form__producto" method="post" action="../../AgregarProductoServlet">
        <div class="campo">
          <label>Nombre del producto</label>
          <input type="text" name="nombre" required>
        </div>
        <div class="campo">
          <label>Unidad de medida</label>
          <select name="unidad_medida">
            <option value="1">Kg</option>
            <option value="2">Litros</option>
            <option value="3">Unidades</option>
          </select>
        </div>
        <div class="campo">
          <label>Precio</label>
          <input type="number" name="precio" required>
        </div>
        <div class="contenedor__fechas">
          <div class="campo input__fechas">
            <label>Fecha de compra</label>
            <input type="date" name="fecha_compra" required>
          </div>
          <div class="campo input__fechas">
            <label>Fecha de Vencimiento</label>
            <input type="date" name="fecha_vencimiento">
          </div>
        </div>
        <div class="campo">
          <label>Estado</label>
          <select name="estado" required>
            <option value="Disponible">Disponible</option>
            <option value="Agotado">Agotado</option>
            <option value="Vencido">Vencido</option>
          </select>
        </div>
        <div class="campo">
          <label>Tipo de producto</label>
          <select name="tipo_producto_id" required>
            <option value="1">Fertilizante</option>
            <option value="2">Herramienta</option>
            <option value="3">Semilla</option>
            <option value="4">Pesticida</option>
          </select>
        </div>
        <button type="submit">Agregar Producto</button>
      </form>
    </div>
  </main>
</body>
</html>