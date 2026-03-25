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
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
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
                Registro_CultivoDAO dao = new Registro_CultivoDAO();
                List<Map<String, String>> cultivos = dao.listarCultivos();

                if (cultivos == null || cultivos.isEmpty()) {
            %>
                <div class="mensaje-vacio">
                    <p>No hay cultivos registrados actualmente.</p>
                </div>
            <%
                } else {
                    for (Map<String, String> cultivo : cultivos) {
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

                    <div class="acciones-contenedor">
                        <a href="Detalles_Cultivo.jsp?id=<%= id %>" class="boton__ver">Ver cultivo</a>
                        <a href="Editar_Cultivo.jsp?id=<%= id %>" class="boton__editar">Editar</a>                        
                        
                        <button type="button" class="boton__eliminar" onclick="confirmarEliminacion('<%= id %>', '<%= nombre %>')">
                            Eliminar
                        </button>
                    </div>
                </div>
            <%
                    }
                }
            %>
        </div>
    </main>
<script>
    window.onload = function() {
        const urlParams = new URLSearchParams(window.location.search);
        const mensaje = urlParams.get('mensaje');
        const agotado = urlParams.get('agotado');

        let config = {
            timer: 3500,
            timerProgressBar: true,
            showConfirmButton: false
        };

        if (mensaje === 'registrado') {
            if (agotado) {
                // Registro exitoso PERO hay productos agotados
                Swal.fire({
                    icon: 'warning',
                    title: '✅ Cultivo Registrado',
                    html: 'El cultivo se guardó, pero los siguientes productos <b>se agotaron</b>:<br><br><b>' +
                          decodeURIComponent(agotado).replace(/,/g, '<br>') + '</b>',
                    confirmButtonColor: '#28a745',
                    confirmButtonText: 'Entendido'
                });
            } else {
                // Registro exitoso sin novedades
                Swal.fire({
                    ...config,
                    icon: 'success',
                    title: '¡Cultivo Registrado!',
                    text: 'El nuevo cultivo se ha guardado exitosamente en el sistema.'
                });
            }

        } else if (mensaje === 'actualizado') {
            if (agotado) {
                // Edición exitosa PERO hay productos agotados
                Swal.fire({
                    icon: 'warning',
                    title: '✅ Cultivo Actualizado',
                    html: 'Los cambios se guardaron, pero estos productos <b>se agotaron</b>:<br><br><b>' +
                          decodeURIComponent(agotado).replace(/,/g, '<br>') + '</b>',
                    confirmButtonColor: '#28a745',
                    confirmButtonText: 'Entendido'
                });
            } else {
                // Edición exitosa sin novedades
                Swal.fire({
                    ...config,
                    icon: 'success',
                    title: '¡Cultivo Actualizado!',
                    text: 'Los cambios han sido guardados correctamente.'
                });
            }

        } else if (mensaje === 'eliminado') {
            Swal.fire({ ...config, icon: 'success', title: '¡Cultivo Eliminado!' });
        }

        // Limpiar URL para que las alertas no se repitan al refrescar
        window.history.replaceState({}, document.title, window.location.pathname);
    };

    // 2. Función para confirmar la eliminación
    function confirmarEliminacion(id, nombreCultivo) {
        Swal.fire({
            title: '¿Estás seguro?',
            text: "Vas a eliminar el cultivo: " + nombreCultivo + ". Esta acción no se puede deshacer.",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#28a745',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Sí, eliminar',
            cancelButtonText: 'Cancelar'
        }).then((result) => {
            if (result.isConfirmed) {
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '<%=request.getContextPath()%>/EliminarCultivoServlet';

                const inputId = document.createElement('input');
                inputId.type = 'hidden';
                inputId.name = 'id';
                inputId.value = id;

                form.appendChild(inputId);
                document.body.appendChild(form);
                form.submit();
            }
        });
    }

    // 3. Lógica del buscador y filtro
    function filtrar() {
        let texto = document.getElementById("buscador").value.toLowerCase();
        let estadoFiltro = document.getElementById("filtroEstado").value.toLowerCase();
        let tarjetas = document.querySelectorAll(".tarjeta__cultivo");

        tarjetas.forEach(function(tarjeta) {
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