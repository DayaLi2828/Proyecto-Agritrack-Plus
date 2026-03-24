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
                    <h2 class="subtitulo">Filtrar por Usuario</h2>
                </div>
                <div class="formulario__registrarcultivo">
                    <div class="campo">
                        <label class="nombre__trabajador">Nombre o documento del usuario</label>
                        <input type="text" id="inputUsuario" placeholder="Escriba nombre o documento...">
                    </div>
                    <button type="button" class="boton__enviar" onclick="cargarFacturas()" style="margin-top: 10px; background-color: #047857;">
                        Buscar Historial
                    </button>
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

    <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js"></script>
    <script>
        async function cargarFacturas() {
            const criterio = document.getElementById('inputUsuario').value;
            const contenedor = document.getElementById('contenedorHistorial');

            if (!criterio || criterio.trim() === '') {
                Swal.fire({
                    icon: 'warning',
                    title: 'Campo requerido',
                    text: 'Por favor, ingresa el nombre o documento del usuario.'
                });
                return;
            }

            contenedor.innerHTML = '<div class="info-edicion">Buscando...</div>';

            try {
                const url = "getHistorial.jsp?usuario=" + encodeURIComponent(criterio);
                const response = await fetch(url);
                const facturas = await response.json();

                contenedor.innerHTML = "";

                if (facturas.length === 0) {
                    contenedor.innerHTML = '<div class="info-edicion" style="color: #b91c1c;">No hay registros de pagos para este usuario.</div>';
                    return;
                }

                facturas.forEach(f => {
                const card = document.createElement('div');
                card.className = 'fila__stock';

                const esSupervisor = f.rol && f.rol.toLowerCase() === 'supervisor';
                const colorRol = esSupervisor ? '#1d4ed8' : '#047857';
                const textoRol = esSupervisor ? 'SUPERVISOR' : 'TRABAJADOR';

                card.innerHTML =
                    '<div class="campo" style="flex: 2;">' +
                        '<p class="nombre__trabajador"><strong>Factura #' + f.id + '</strong>' +
                            ' <span style="background:' + colorRol + '; color:white; padding:2px 8px; border-radius:10px; font-size:11px;">' + textoRol + '</span>' +
                        '</p>' +
                        '<p class="ayuda-texto" style="background: #e5e7eb; color: #374151;">Fecha: ' + f.fecha + '</p>' +
                        '<p style="margin-top:5px; font-weight:bold; color:#047857;">' +
                            'Total: $ ' + parseFloat(f.total).toLocaleString() + ' - PAGADA' +
                        '</p>' +
                    '</div>' +
                    '<div class="campo" style="flex: 1; display: flex; justify-content: flex-end; align-items: center;">' +
                        '<button class="btn-ver-pdf" onclick="reimprimirFactura(\'' + f.id + '\', \'' + f.total + '\', \'' + f.fecha + '\')">' +
                            'Reimprimir PDF' +
                        '</button>' +
                    '</div>';
                contenedor.appendChild(card);
            });
            } catch (error) {
                console.error("Error cargando historial:", error);
                contenedor.innerHTML = '<div class="info-edicion" style="color: #b91c1c;">Error al conectar con el servidor.</div>';
            }
        }

        function reimprimirFactura(id, total, fecha) {
            try {
                const { jsPDF } = window.jspdf;
                const doc = new jsPDF();
                const trabajador = document.getElementById('inputUsuario').value;

                doc.setFillColor(4, 120, 87);
                doc.rect(0, 0, 210, 45, 'F');
                doc.setTextColor(255, 255, 255);
                doc.setFontSize(22);
                doc.text("AGRITRACK PLUS", 20, 25);
                doc.setFontSize(10);
                doc.text("COPIA DE COMPROBANTE DE PAGO", 20, 35);

                doc.setTextColor(55, 65, 81);
                doc.setFontSize(12);
                doc.text("Informacion del Registro", 20, 60);
                doc.setFontSize(10);
                doc.text("Factura N: " + id, 20, 70);
                doc.text("Usuario: " + trabajador, 20, 77);
                doc.text("Fecha Original de Pago: " + fecha, 20, 84);
                doc.text("Estado: PAGADA", 20, 91);

                doc.setLineWidth(0.5);
                doc.setDrawColor(200, 200, 200);
                doc.line(20, 100, 190, 100);

                doc.setFontSize(14);
                doc.setTextColor(4, 120, 87);
                doc.text("TOTAL PAGADO: $" + parseFloat(total).toLocaleString(), 20, 115);

                doc.setFontSize(9);
                doc.setTextColor(100, 100, 100);
                doc.text("Este documento es una copia fiel generada desde el historial de pagos del sistema.", 20, 140);
                doc.text("Agritrack Plus - Gestion Agricola Eficiente", 20, 145);

                doc.save("Factura_Reimpresa_" + id + ".pdf");

            } catch (error) {
                console.error("Error al generar PDF:", error);
                Swal.fire({
                        icon: 'error',
                        title: 'Error',
                        text: 'No se pudo generar el PDF.'
                    });            
                }
        }
    </script>
</body>
</html>