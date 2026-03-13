<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.agritrack.agritrackplus.DAO.ProductoDAO" %>
<%@ page import="java.util.Map" %>

<%
    // Lógica para detectar si es edición
    String idEditar = request.getParameter("id");
    Map<String, String> p = null;
    boolean esEdicion = (idEditar != null && !idEditar.isEmpty());

    if (esEdicion) {
        ProductoDAO dao = new ProductoDAO();
        p = dao.buscarProductoPorId(Integer.parseInt(idEditar));
    }
%>

<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><%= esEdicion ? "Editar Producto" : "Añadir Producto" %></title>
  <link rel="stylesheet" href="../../asset/Administrador/style_añadir_producto.css">
</head>
<body onload="mostrarFechaVencimiento()"> 
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
          <h1 class="titulo"><%= esEdicion ? "Editar Producto" : "Añadir Nuevo Producto" %></h1>
        </div>
  </header>
  <main>
    <div class="contenedor__ingreso">
      <div class="contendor__subtitulo">
        <div class="caja__logo">
          <img class="logo" src="../../asset/imagenes/stock.png" alt="logo planta">
        </div>
        <h2 class="subtitulo"><%= esEdicion ? "Modificar Datos" : "Añadir producto" %></h2>
      </div>

      <% if ("true".equals(request.getParameter("error"))) { %>
        <p style="color:red;">Hubo un error al procesar el producto. Intenta de nuevo.</p>
      <% } %>

        <form class="form__producto" method="post" action="${pageContext.request.contextPath}/AgregarProductoServlet">
            
            <% if(esEdicion) { %>
                <input type="hidden" name="id" value="<%= idEditar %>">
            <% } %>

            <div class="campo">
              <label>Nombre del producto</label>
              <input type="text" name="nombre" value="<%= esEdicion ? p.get("nombre") : "" %>" required>
            </div>

            <div class="campo">
                <label>Tipo de producto</label>
                    <select name="tipo_producto_id" id="tipo_producto" required onchange="mostrarFechaVencimiento()">
                        <option value="">Selecciona...</option>
                        <option value="1" <%= esEdicion && "1".equals(p.get("tipo_producto_id")) ? "selected" : "" %>>Fertilizante</option>
                        <option value="2" <%= esEdicion && "2".equals(p.get("tipo_producto_id")) ? "selected" : "" %>>Herramienta</option>
                        <option value="3" <%= esEdicion && "3".equals(p.get("tipo_producto_id")) ? "selected" : "" %>>Semilla</option>
                        <option value="4" <%= esEdicion && "4".equals(p.get("tipo_producto_id")) ? "selected" : "" %>>Pesticida</option>
                  </select>
            </div>

            <div class="campo">
              <label>Unidad de medida</label>
              <select name="unidad_medida" required>
                <option value="Kilogramo" <%= esEdicion && "Kilogramo".equals(p.get("unidad_medida")) ? "selected" : "" %>>Kilogramo (Kg)</option>
                <option value="Litro" <%= esEdicion && "Litro".equals(p.get("unidad_medida")) ? "selected" : "" %>>Litro (Lt)</option>
                <option value="Unidad" <%= esEdicion && "Unidad".equals(p.get("unidad_medida")) ? "selected" : "" %>>Unidad (Un)</option>
              </select>
            </div>

            <div class="campo">
                <label>Precio</label>
                <input type="number" name="precio" id="precio" step="0.01" min="0" 
                value="<%= esEdicion ? p.get("precio") : "" %>" required>
            </div>

            <div class="contenedor__fechas">
              <div class="campo input__fechas">
                <label>Fecha de compra</label>
                <input type="date" name="fecha_compra" id="fecha_compra" value="<%= esEdicion ? p.get("fecha_compra") : "" %>" required>
              </div>
              <div class="campo input__fechas" id="campo__fecha_vencimiento" style="display:none;">
                <label>Fecha de Vencimiento</label>
               <input type="date" name="fecha_vencimiento" id="fecha_vencimiento" value="<%= esEdicion ? p.get("fecha_vencimiento") : "" %>">
              </div>
            </div>

            <div class="campo">
                <label>Cantidad</label>
                <input type="number" name="cantidad" min="1" value="<%= esEdicion ? p.get("cantidad") : "" %>" required>
            </div>

            <button type="submit"><%= esEdicion ? "Guardar Cambios" : "Agregar Producto" %></button>
      </form>
    </div>
  </main>
      <script>
           // Se ejecuta cuando carga la página
    window.onload = function() {
        configurarRestriccionesFechas();
        mostrarFechaVencimiento(); // Ejecuta tu función original
    };

    function configurarRestriccionesFechas() {
        const inputCompra = document.getElementById("fecha_compra");
        const inputVencimiento = document.getElementById("fecha_vencimiento");
        const form = document.querySelector(".form__producto");

        // 1. No permitir que la fecha de compra sea mayor a HOY
        const hoy = new Date().toISOString().split("T")[0];
        inputCompra.setAttribute("max", hoy);

        // 2. Cuando cambie la fecha de compra, actualizar el mínimo de vencimiento
        inputCompra.addEventListener("change", function() {
            if (this.value) {
                // La fecha de vencimiento debe ser al menos el día siguiente a la compra
                let fechaSeleccionada = new Date(this.value);
                fechaSeleccionada.setDate(fechaSeleccionada.getDate() + 1);
                
                let mañanaDeCompra = fechaSeleccionada.toISOString().split("T")[0];
                inputVencimiento.setAttribute("min", mañanaDeCompra);
                
                // Si la fecha de vencimiento ya puesta es menor a la nueva fecha permitida, se borra
                if (inputVencimiento.value && inputVencimiento.value < mañanaDeCompra) {
                    inputVencimiento.value = "";
                }
            }
        });

        // 3. Validación final antes de enviar el formulario
        form.onsubmit = function(e) {
            const fechaCompra = new Date(inputCompra.value);
            const fechaVencimiento = new Date(inputVencimiento.value);
            const hoyDate = new Date();
            hoyDate.setHours(23, 59, 59, 999); // Ajuste para comparar solo fechas

            // Validar que compra no sea futuro
            if (fechaCompra > hoyDate) {
                alert("La fecha de compra no puede ser una fecha futura.");
                e.preventDefault();
                return false;
            }

            // Validar vencimiento (solo si el campo es visible/requerido)
            if (inputVencimiento.required || document.getElementById("campo__fecha_vencimiento").style.display !== "none") {
                if (inputVencimiento.value && fechaVencimiento <= fechaCompra) {
                    alert("La fecha de vencimiento debe ser al menos un día después de la fecha de compra.");
                    e.preventDefault();
                    return false;
                }
            }
            return true;
        };
    }
        function mostrarFechaVencimiento() {
          let tipo = document.getElementById("tipo_producto").value;
          let campofecha = document.getElementById("campo__fecha_vencimiento");
          let inputFecha = document.getElementById("fecha_vencimiento");

          if (tipo === "1" || tipo === "4") {
            campofecha.style.display = "block";
            inputFecha.required = true;
          } else {
            campofecha.style.display = "none";
            inputFecha.required = false;
            // No borramos el valor si es edición para no perder el dato si el usuario cambia de opción por error
            if(!document.getElementsByName("id")[0]) { 
                inputFecha.value = ""; 
            }
          }
        }
     </script>
</body>
</html>