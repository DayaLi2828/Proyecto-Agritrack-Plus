<%@page import="com.agritrack.agritrackplus.DAO.UsuarioDAO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.agritrack.agritrackplus.DAO.Registro_CultivoDAO" %>
<%@ page import="com.agritrack.agritrackplus.DAO.ProductoDAO" %>
<%@ page import="com.agritrack.agritrackplus.DAO.CultivoDAO" %>
<%@ page import="java.util.List" %>
<%
    // 1. VALIDACIÓN DE SEGURIDAD
    HttpSession sesion = request.getSession(false);
    String nombreUsuario = (sesion != null) ? (String) sesion.getAttribute("usuario_nombre") : null;
    String rol = (sesion != null) ? (String) sesion.getAttribute("rol") : null;
    Integer idUsuario = (sesion != null) ? (Integer) sesion.getAttribute("usuario_id") : null;

    if (nombreUsuario == null || (!"administrador".equalsIgnoreCase(rol) && !"supervisor".equalsIgnoreCase(rol))) {
        response.sendRedirect("../../index.jsp?error=acceso_denegado");
        return;
    }

    // 2. OBTENCIÓN DE DATOS DINÁMICOS
    CultivoDAO cultivoDAO = new CultivoDAO(); 
    ProductoDAO productoDAO = new ProductoDAO();
    UsuarioDAO usuarioDAO = new UsuarioDAO();
    int totalCultivos = 0;
    int totalProductos = 0;
    int totalUsuarios = 0;

    try {
        // Lógica de conteo basada en el ROL
        totalCultivos = cultivoDAO.contarCultivosPorRol(idUsuario, rol);        
        List listaP = productoDAO.listarProductos();
        if(listaP != null) totalProductos = listaP.size();

        // CORRECCIÓN: Uso del método directo contarUsuarios para evitar cargar listas pesadas
        if ("administrador".equalsIgnoreCase(rol)) {
            totalUsuarios = usuarioDAO.contarUsuarios(); 
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    String inicial = (nombreUsuario != null && !nombreUsuario.isEmpty()) ? nombreUsuario.substring(0, 1).toUpperCase() : "?";
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Admin | AgriTrack Plus</title>
  <link rel="stylesheet" href="../../asset/Administrador/style_Admin.css">
</head>
<body>

  <header>
    <div class="contenedor__titulo">
      <img class="logo" src="../../asset/imagenes/hoja.png" alt="logo">
      <h1 class="titulo">AGRITRACK<br> PLUS</h1>
    </div>
  </header>

  <aside class="sidebar__barra">
    <nav>
      <a href="Admin.jsp">Inicio</a>
      <a href="../Calendario.jsp">Calendario</a>
      <% if ("administrador".equalsIgnoreCase(rol)) { %>
        <a href="Usuarios.jsp">Usuarios</a>
      <% } %>
      <a href="Productos.jsp">Inventario</a>
      <a href="Gestion_Pagos.jsp">Pagos</a>
      <a href="Hisrotial_Pagos.jsp">Historial de pagos</a>
    </nav>
  </aside>

  <main class="main">
    <div class="main__cajatexto">
      <h1 class="main__titulo">Bienvenido, <%= nombreUsuario %></h1>
      <p class="main__texto">Gestiona tus cultivos de manera eficiente!</p>
      
      <div class="contenedor__perfil">
        <div class="perfil__texto">
          <h2 class="nombre__usuario"><%= nombreUsuario %></h2>
          <p class="descripcion__usuario"><%= rol %></p>
        </div>
        
        <div class="cirulo__perfil" onclick="togglePerfil()">
          <h3 class="inicial__usuario"><%= inicial %></h3>
        </div>

        <div class="cerrar__sesion" onclick="location.href='${pageContext.request.contextPath}/LogoutServlet'">
          <img src="../../asset/imagenes/cerrar-sesion.png" alt="cerrar"/>
        </div>
      </div>
    </div>

    <div id="cardDatos" class="perfil__card">
        <h3>Mi Perfil</h3>
        <p><strong>Usuario:</strong> <%= nombreUsuario %></p>
        <p><strong>Cargo:</strong> <%= rol %></p>
        <p><strong>Estado:</strong> Activo</p>
        <button class="btn-cerrar-card" onclick="togglePerfil()">Cerrar</button>
    </div>

    <div class="main__contenedores">
      <div class="main__boxs" onclick="location.href='Registro_Cultivos.jsp'">
        <div class="main__contimagen"><img src="../../asset/imagenes/te-verde.png"></div>
        <div class="caja__texto">
          <h3>Registrar cultivo</h3>
          <p>Añade nuevos cultivos al sistema.</p>
        </div>
      </div>

      <div class="main__boxs" onclick="location.href='Cultivos_Registrados.jsp'">
        <div class="main__contimagen--secundario"><img src="../../asset/imagenes/bloc.png"></div>
        <div class="caja__texto">
          <h3>Cultivos Registrados</h3>
          <p>Visualiza tus cultivos actuales.</p>
        </div>
      </div>
    </div>

    <div class="main__contetarjetas">
      <div class="main__tarjetas">
        <h4><%= "supervisor".equalsIgnoreCase(rol) ? "Mis Cultivos" : "Cultivos Totales" %></h4>
        <h3 class="main__numero"><%= totalCultivos %></h3>
      </div>
      <div class="main__tarjetas">
        <h4>Productos</h4>
        <h3 class="main__numero"><%= totalProductos %></h3>
      </div>
      
      <% if ("administrador".equalsIgnoreCase(rol)) { %>
      <div class="main__tarjetas">
        <h4>Usuarios</h4>
        <h3 class="main__numero"><%= totalUsuarios %></h3>
      </div>
      <% } else { %>
      <div class="main__tarjetas">
          <h4>Rol Acceso</h4>
          <h3 class="main__numero" style="font-size: 1.2rem;"><%= rol %></h3>
      </div>
      <% } %>
    </div>
  </main>

  <footer class="footer">
    <p>© 2026 Agritrack Plus</p>
  </footer>

  <script>
    function togglePerfil() {
        var card = document.getElementById("cardDatos");
        card.classList.toggle("activo");
    }
  </script>
</body>

</html>