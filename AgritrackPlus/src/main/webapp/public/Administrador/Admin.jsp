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
    com.agritrack.agritrackplus.modelo.Usuario userFull = null; // Para los datos personales
    int totalCultivos = 0;
    int totalProductos = 0;
    int totalUsuarios = 0;

    try {
        // Datos del perfil completo
        userFull = usuarioDAO.listarId(idUsuario);
        
        // Lógica de conteo basada en el ROL
        totalCultivos = cultivoDAO.contarCultivosPorRol(idUsuario, rol);        
        List listaP = productoDAO.listarProductos();
        if(listaP != null) totalProductos = listaP.size();

        if ("administrador".equalsIgnoreCase(rol)) {
            totalUsuarios = usuarioDAO.contarUsuarios(); 
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    // Variables de apoyo para los datos personales
    String correo = (userFull != null && userFull.getCorreo() != null) ? userFull.getCorreo() : "";
    String telefono = (userFull != null && userFull.getTelefono() != null) ? userFull.getTelefono() : "";
    String direccion = (userFull != null && userFull.getDireccion() != null) ? userFull.getDireccion() : "";
    String fotoNombre = (userFull != null) ? userFull.getFoto() : null; 
    
    String inicial = (nombreUsuario != null && !nombreUsuario.isEmpty()) ? nombreUsuario.substring(0, 1).toUpperCase() : "?";
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Admin | AgriTrack Plus</title>
  <link rel="stylesheet" href="../../asset/Administrador/style_Admin.css">
  <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
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
      <a href="Historial_Facturas.jsp">Historial de pagos</a>
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
        
        <div class="cirulo__perfil" onclick="togglePerfil()" style="cursor:pointer; overflow:hidden; display:flex; align-items:center; justify-content:center; background:#e2e8f0; border-radius:50%; width:50px; height:50px; border: 2px solid #10b981;">
            <% if (fotoNombre != null && !fotoNombre.isEmpty()) { %>
                <img src="../../asset/imagenes/<%= fotoNombre %>" style="width:100%; height:100%; object-fit:cover;">
            <% } else { %>
                <h3 class="inicial__usuario"><%= inicial %></h3>
            <% } %>
        </div>

        <div class="cerrar__sesion" onclick="location.href='${pageContext.request.contextPath}/LogoutServlet'">
          <img src="../../asset/imagenes/cerrar-sesion.png" alt="cerrar"/>
        </div>
      </div>
    </div>

    <%-- OVERLAY Y TARJETA DINÁMICA ACTUALIZADA --%>
    <div id="overlayPerfil" class="perfil__overlay" onclick="togglePerfil()"></div>
    
    <div id="cardDatos" class="perfil__card">
        <div class="card__edit-header">
            <h3>Mis Datos Personales</h3>
            <p>Gestiona tu perfil de <%= rol %>.</p>
        </div>

        <form action="${pageContext.request.contextPath}/ActualizarPerfilServlet" 
              method="POST" 
              enctype="multipart/form-data" 
              class="form__edicion">
            
            <input type="hidden" name="idUsuario" value="<%= idUsuario %>">
            <input type="hidden" name="txtDocumento" value="<%= (userFull != null) ? userFull.getDocumento() : "" %>">
            
            <div class="seccion__foto-perfil">
                <div id="contenedorPreview">
                    <% if (fotoNombre != null && !fotoNombre.isEmpty()) { %>
                        <img src="../../asset/imagenes/<%= fotoNombre %>" class="foto__actual" id="previewFoto">
                    <% } else { %>
                        <div class="foto__vacia" id="previewFoto"><%= inicial %></div>
                    <% } %>
                </div>
                <label for="inputFoto" class="btn-subir-foto">
                    <i class="fas fa-camera"></i> Cambiar Foto
                </label>
                <input type="file" id="inputFoto" name="fotoPerfil" accept="image/*" style="display:none" onchange="previsualizar(this)">
            </div>

            <div class="grid__campos">
                <div class="campo__edicion">
                    <label>Nombre Completo</label>
                    <input type="text" name="txtNombre" value="<%= (userFull != null) ? userFull.getNombre() : nombreUsuario %>" required>
                </div>
                <div class="campo__edicion">
                    <label>Correo Electrónico</label>
                    <input type="email" name="txtCorreo" value="<%= correo %>" required>
                </div>
                <div class="campo__edicion">
                    <label>Teléfono</label>
                    <input type="text" name="txtTelefono" value="<%= telefono %>">
                </div>
                <div class="campo__edicion">
                    <label>Dirección</label>
                    <input type="text" name="txtDireccion" value="<%= direccion %>">
                </div>
                <div class="campo__edicion" style="grid-column: span 2;">
                    <label>Nueva Contraseña</label>
                    <input type="password" name="txtPassword" placeholder="Dejar en blanco para mantener la actual">
                </div>
            </div>

            <div class="botones__edicion">
                <button type="button" class="btn-cancelar" onclick="togglePerfil()">Cancelar</button>
                <button type="submit" class="btn-guardar">Guardar Cambios</button>
            </div>
        </form>
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
        document.getElementById("cardDatos").classList.toggle("activo");
        // Agregamos el overlay si no existe en el CSS original, para que se vea igual que el supervisor
        const overlay = document.getElementById("overlayPerfil");
        if(overlay) overlay.classList.toggle("activo");
    }

    function previsualizar(input) {
        if (input.files && input.files[0]) {
            var reader = new FileReader();
            reader.onload = function(e) {
                const contenedor = document.getElementById('contenedorPreview');
                contenedor.innerHTML = `<img src="${e.target.result}" class="foto__actual" id="previewFoto">`;
            }
            reader.readAsDataURL(input.files[0]);
        }
    }
    /*MENSAJE DE CONFIRMACION DE DATOS PERSONALES*/

    // Esperar a que cargue el DOM
    document.addEventListener("DOMContentLoaded", function() {
        const urlParams = new URLSearchParams(window.location.search);
        
        // Si la URL tiene ?update=success
        if (urlParams.get('update') === 'success') {
            Swal.fire({
                icon: 'success',
                title: '¡Perfil Actualizado!',
                text: 'Tus datos se han guardado correctamente.',
                confirmButtonColor: '#10b981',
                timer: 3000 // Se cierra sola en 3 segundos
            });
            // Limpiar la URL para que no repita la alerta al recargar
            window.history.replaceState({}, document.title, window.location.pathname);
        }

        // Si la URL tiene ?update=error
        if (urlParams.get('update') === 'error') {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'No se pudieron actualizar los datos. Intenta de nuevo.',
                confirmButtonColor: '#ef4444'
            });
            window.history.replaceState({}, document.title, window.location.pathname);
        }
    });

  </script>
</body>
</html>