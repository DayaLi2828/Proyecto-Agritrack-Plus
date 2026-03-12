<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Historial de Facturas - AgritrackPlus</title>
    <link rel="stylesheet" href="../../asset/Administrador/style_Gestion_Pagos.css">

</head>
<body>
    <header>
        <a href="Admin.jsp">
            <div class="icono__devolver">
                <img src="../../asset/imagenes/devolver.png" alt="volver">
            </div>
        </a>
        <div class="contenedor__titulo">
            <div class="contenedor__logo">
                <img class="logo" src="../../asset/imagenes/hoja (3).png" alt="logo"/>
            </div>
            <h1 class="titulo">Historial de Pagos</h1>
        </div>
    </header>

    <main class="main">
        <div class="contendor">
            <div class="contendor__cajas filtro-seccion">
                <div class="contendor__subtitulo">
                    <div class="caja__logo">
                        <img class="logo" src="../../asset/imagenes/supervisor.png" alt="buscar">
                    </div>
                    <h2 class="subtitulo">Filtrar por Trabajador</h2>
                </div>
                <div class="formulario__registrarcultivo">
                    <div class="campo">
                        <label class="nombre__trabajador">Seleccione o busque al usuario</label>
                        <input type="text" id="inputUsuario" list="listaUsuarios" placeholder="Escriba nombre o documento..." onchange="cargarFacturas()">
                        <datalist id="listaUsuarios">
                            <option value="1098765432 - Juan Pérez">
                            <option value="1122334455 - Maria Lopez">
                        </datalist>
                    </div>
                </div>
            </div>

            <div class="contendor__cajas">
                <div class="contendor__subtitulo">
                    <div class="caja__logo">
                        <img class="logo" src="../../asset/imagenes/stock.png" alt="facturas">
                    </div>
                    <h2 class="subtitulo">Facturas Encontradas</h2>
                </div>
                
                <div id="contenedorHistorial">
                    <div class="info-edicion">Ingrese un usuario para ver su historial de pagos realizados.</div>
                    </div>
            </div>
        </div>
    </main>

    <script>
        async function cargarFacturas() {
            const criterio = document.getElementById('inputUsuario').value;
            const contenedor = document.getElementById('contenedorHistorial');

            if (!criterio) return;

            try {
                // Supongamos que tienes un servicio getHistorial.jsp
                const url = "getHistorial.jsp?usuario=" + encodeURIComponent(criterio);
                const response = await fetch(url);
                const facturas = await response.json();

                contenedor.innerHTML = ""; // Limpiar

                if (facturas.length === 0) {
                    contenedor.innerHTML = '<div class="info-edicion" style="color: #b91c1c;">No hay registros de pagos para este usuario.</div>';
                    return;
                }

                facturas.forEach(f => {
                    const card = document.createElement('div');
                    card.className = 'fila__stock'; // Reutiliza tu CSS de cajitas
                    
                    card.innerHTML = `
                        <div class="campo" style="flex: 2;">
                            <p class="nombre__trabajador"><strong>Factura #\${f.id}</strong></p>
                            <p class="ayuda-texto" style="background: #e5e7eb; color: #374151;">Fecha: \${f.fecha}</p>
                            <p style="margin-top:5px; font-weight:bold; color:#047857;">Total Pagado: $ \${f.total.toLocaleString()}</p>
                        </div>
                        <div class="campo" style="flex: 1; display: flex; justify-content: flex-end; align-items: center;">
                            <button class="btn-ver-pdf" onclick="reimprimirFactura(\${f.id})">Reimprimir PDF</button>
                        </div>
                    `;
                    contenedor.appendChild(card);
                });

            } catch (error) {
                console.error("Error cargando historial:", error);
            }
        }

        function reimprimirFactura(id) {
            alert("Llamando a la función de generación de PDF para la factura #" + id);
            // Aquí llamarías a la lógica de jsPDF con los datos guardados de esa factura
        }
    </script>
</body>
</html>