<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Pagos AgritrackPlus</title>
    <link rel="stylesheet" href="../../asset/Administrador/style_Gestion_Pagos.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js"></script>
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
            <h1 class="titulo">Gestión de Pagos</h1>
        </div>
    </header>

    <main class="main">
        <div class="contendor">
            <div class="titulo__seccion">
                <img src="../../asset/imagenes/usuario(2).png" alt="trabajador">
                Pago a Trabajadores
            </div>
            <div class="contendor__cajas">
                <div class="contendor__subtitulo">
                    <div class="caja__logo">
                        <img class="logo" src="../../asset/imagenes/supervisor.png" alt="buscar">
                    </div>
                    <h2 class="subtitulo">Buscar Trabajador</h2>
                </div>
                <div class="formulario__registrarcultivo">
                    <div class="campo">
                        <label class="nombre__trabajador">Nombre o Documento del Usuario</label>
                        <input type="text" id="busquedaTrabajador" placeholder="Ej: Juan Pérez o 1098..." required>
                    </div>
                    <button type="button" class="boton__enviar" onclick="buscarActividad()" style="margin-top: 10px; background-color: #047857;">
                        Buscar Tareas Completadas
                    </button>
                </div>
            </div>

            <div class="contendor__cajas">
                <div class="contendor__subtitulo">
                    <div class="caja__logo">
                        <img class="logo" src="../../asset/imagenes/stock.png" alt="valores">
                    </div>
                    <h2 class="subtitulo">Configuración de Jornal</h2>
                </div>
                <div class="fechas">
                    <div class="campo">
                        <label class="nombre__trabajador">Valor Medio Día</label>
                        <input type="number" id="valorMedio" placeholder="$ Valor medio" value="25000">
                    </div>
                    <div class="campo">
                        <label class="nombre__trabajador">Valor Día Completo</label>
                        <input type="number" id="valorCompleto" placeholder="$ Valor completo" value="50000">
                    </div>
                </div>
            </div>

            <div class="contendor__cajas">
                <div class="contendor__subtitulo">
                    <div class="caja__logo">
                        <img class="logo" src="../../asset/imagenes/planta (2).png" alt="tareas">
                    </div>
                    <h2 class="subtitulo">Tareas Realizadas</h2>
                </div>
                
                <div id="contenedorTareas">
                    <div id="listaResultados">
                        <div class="info-edicion">
                            Ingrese un nombre para buscar tareas liquidadas.
                        </div>
                    </div>
                </div>

                <button type="button" class="boton__enviar" onclick="generarFacturaPDF()" style="margin-top: 20px;">
                    Generar Factura PDF
                </button>
            </div>
            <%-- ===== SECCIÓN PAGO SUPERVISOR ===== --%>
        <div class="titulo__seccion">
            <img src="../../asset/imagenes/supervisor.png" alt="supervisor">
            Pago a Supervisores
        </div>
                        
    <div class="contendor__cajas">
        <div class="contendor__subtitulo">
            <div class="caja__logo">
                <img class="logo" src="../../asset/imagenes/supervisor.png" alt="supervisor">
            </div>
            <h2 class="subtitulo">Pago a Supervisor</h2>
        </div>
        <div class="formulario__registrarcultivo">
            <div class="campo">
                <label class="nombre__trabajador">Nombre o Documento del Supervisor</label>
                <input type="text" id="busquedaSupervisor" placeholder="Ej: Pedro Gil o 1077...">
            </div>
            <button type="button" class="boton__enviar" onclick="buscarSupervisor()" style="margin-top: 10px; background-color: #047857;">
                Buscar Supervisor
            </button>
        </div>
    </div>

    <div class="contendor__cajas">
        <div class="contendor__subtitulo">
            <div class="caja__logo">
                <img class="logo" src="../../asset/imagenes/stock.png" alt="salario">
            </div>
            <h2 class="subtitulo">Salario Semanal Supervisor</h2>
        </div>
        <div class="fechas">
            <div class="campo">
                <label class="nombre__trabajador">Salario semanal</label>
                <input type="number" id="salarioSupervisor" placeholder="$ Salario semanal">
            </div>
        </div>
    </div>

    <div class="contendor__cajas">
        <div class="contendor__subtitulo">
            <div class="caja__logo">
                <img class="logo" src="../../asset/imagenes/planta (2).png" alt="info">
            </div>
            <h2 class="subtitulo">Información del Supervisor</h2>
        </div>
        <div id="infoSupervisor">
            <div class="info-edicion">
                Ingrese el nombre del supervisor para continuar.
            </div>
        </div>
        <button type="button" class="boton__enviar" onclick="generarFacturaSupervisorPDF()" style="margin-top: 20px;">
            Generar Factura PDF Supervisor
        </button>
    </div>

        </div>

    </main>
    
<script>
    let tareasSupervisor = [];

    async function buscarActividad() {
        const criterio = document.getElementById('busquedaTrabajador').value;
        const listaResultados = document.getElementById('listaResultados');

        if (!criterio) {
            Swal.fire({ icon: 'warning', title: 'Campo requerido', text: 'Por favor, ingresa el nombre o documento.' });
            return;
        }

        try {
            const url = "getTareas.jsp?criterio=" + encodeURIComponent(criterio);
            const response = await fetch(url);
            const tareas = await response.json();

            listaResultados.innerHTML = "";

            if (tareas.length === 0) {
                listaResultados.innerHTML = '<div class="info-edicion">No se encontraron tareas para este usuario.</div>';
                return;
            }

            tareas.forEach(t => {
                const jornadaDB = (t.jornada || "").toLowerCase();
                const estadoDB = (t.estado || "").toLowerCase();
                const esCompleto = jornadaDB.includes('completo') || jornadaDB.includes('entero');
                const tipoJornada = esCompleto ? "completo" : "medio";

                const card = document.createElement('div');
                card.setAttribute('data-estado', estadoDB);
                card.className = 'fila__stock';
                card.innerHTML =
                    '<div class="campo">' +
                        '<p class="nombre__trabajador"><strong>Tarea: </strong><span class="txt-tarea">' + t.tarea + '</span></p>' +
                        '<p class="ayuda-texto">Cultivo: <span class="txt-cultivo">' + (t.cultivo || '') + '</span></p>' +
                        '<p class="ayuda-texto">Estado: <span class="txt-estado">' + t.estado + '</span></p>' +
                    '</div>' +
                    '<div class="campo">' +
                        '<span class="etiqueta-jornada" data-tipo="' + tipoJornada + '">' + t.jornada + '</span>' +
                    '</div>';
                listaResultados.appendChild(card);
            });

        } catch (error) {
            console.error("Error:", error);
            Swal.fire({ icon: 'error', title: 'Error de conexión', text: 'Error al conectar con el servidor.' });
        }
    }
async function buscarSupervisor() {
    const criterio = document.getElementById('busquedaSupervisor').value;
    const infoDiv = document.getElementById('infoSupervisor');

    if (!criterio) { 
        Swal.fire({ icon: 'warning', title: 'Campo requerido', text: 'Por favor, ingresa el nombre.' });
        return; 
    }

    try {
        const url = "buscarSupervisor.jsp?criterio=" + encodeURIComponent(criterio);
        const response = await fetch(url);
        const data = await response.json();

        if (!data || data.error) {
            infoDiv.innerHTML = '<div class="info-edicion">No se encontró supervisor.</div>';
            tareasSupervisor = []; // Limpiamos si no hay nada
            return;
        }

        // IMPORTANTE: Aquí guardamos los cultivos para que el PDF los vea
        tareasSupervisor = data.tareas || []; 
        
        infoDiv.innerHTML =
            '<div class="fila__stock">' +
                '<div class="campo">' +
                    '<p class="nombre__trabajador"><strong>Supervisor: </strong>' + data.nombre + '</p>' +
                    '<p class="ayuda-texto">Cultivos supervisados: ' + (tareasSupervisor.length) + '</p>' +
                '</div>' +
            '</div>';
    } catch (error) {
        console.error("Error:", error);
    }
}
    // --- FUNCIONES DE PDF (IGUALES A TU LÓGICA ORIGINAL) ---

    function generarFacturaPDF() {
        const { jsPDF } = window.jspdf;
        const doc = new jsPDF();

        const trabajador = document.getElementById('busquedaTrabajador').value;
        const vMedio = parseFloat(document.getElementById('valorMedio').value) || 0;
        const vCompleto = parseFloat(document.getElementById('valorCompleto').value) || 0;
        const filas = document.querySelectorAll('.fila__stock');

        if (vMedio <= 0 || vCompleto <= 0 || filas.length === 0) {
            Swal.fire({
                icon: 'warning',
                title: 'Datos inválidos',
                text: 'Verifique los valores y que existan tareas en la lista.'
            });
            return;
        }

        // --- DISEÑO RESTAURADO (COMO EN LA IMAGEN ANTERIOR) ---
        // Rectángulo verde de encabezado (más grande)
        doc.setFillColor(4, 120, 87);
        doc.rect(0, 0, 210, 45, 'F');
        doc.setTextColor(255, 255, 255);
        doc.setFontSize(22);
        doc.text("AGRITRACK PLUS", 20, 25);
        doc.setFontSize(10);
        doc.text("Comprobante de Pago de Jornales", 20, 35);
        doc.setTextColor(55, 65, 81);
        doc.setFontSize(11);
        doc.text("Trabajador: " + trabajador, 20, 60);
        doc.text("Fecha: " + new Date().toLocaleDateString(), 20, 67);

        let yPos = 85;
        let totalPagar = 0;

        // Encabezado de la tabla de tareas
        doc.setFillColor(55, 65, 81);
        doc.rect(20, yPos, 170, 8, 'F');
        doc.setTextColor(255, 255, 255);
        doc.setFontSize(10);
        doc.text("Cultivo - Tarea", 25, yPos + 5);
        doc.text("Monto", 165, yPos + 5);
        yPos += 15;

        // Detalle de las tareas y cálculo
        filas.forEach((fila) => {
            const nombreEl = fila.querySelector('.txt-tarea');
            if (!nombreEl) return;
            const nombre = nombreEl.textContent;
            const cultivo = fila.querySelector('.txt-cultivo') ? fila.querySelector('.txt-cultivo').textContent : '';
            const estado = fila.getAttribute('data-estado');
            const tipoEl = fila.querySelector('.etiqueta-jornada');
            const tipo = tipoEl ? tipoEl.getAttribute('data-tipo') : 'medio';

            let subtotal = (tipo === 'completo') ? vCompleto : vMedio;
            let notaEstado = "";

            // Validación de pago al 50% si está en proceso
            if (estado && estado.includes("proceso")) {
                subtotal = subtotal * 0.5;
                notaEstado = " (50%)";
            }
            totalPagar += subtotal;

            doc.setFontSize(11);
            doc.setTextColor(31, 41, 55);
            doc.text(cultivo + " - " + nombre + notaEstado, 25, yPos);
            doc.text("$" + subtotal.toLocaleString(), 165, yPos);
            yPos += 12;
        });

        // Total a pagar
        doc.setFillColor(4, 120, 87);
        doc.rect(130, yPos, 60, 15, 'F');
        doc.setTextColor(255, 255, 255);
        doc.text("TOTAL A PAGAR: $" + totalPagar.toLocaleString(), 135, yPos + 10);

        // --- GUARDADO DE DATOS (CON CONCATENACIÓN CLÁSICA) ---
        // Para evitar el error de EL de JSP, usamos la concatenación antigua
        const url = "guardarPago.jsp?nombre=" + encodeURIComponent(trabajador) + 
                    "&documento=SN" + 
                    "&total=" + totalPagar + 
                    "&criterio=" + encodeURIComponent(trabajador);

        // Llamada asíncrona para guardar el pago
        fetch(url).then(() => {
            Swal.fire({
                icon: 'success',
                title: 'Proceso exitoso',
                text: 'Pago registrado y tareas marcadas como pagadas.'
            });
            document.getElementById('listaResultados').innerHTML = '<div class="info-edicion">Pago procesado con éxito.</div>';
        }).catch(error => {
            console.error("Error al guardar:", error);
        });

        // Descargar el PDF
        doc.save("Factura_" + trabajador.replace(/\s+/g, '_') + ".pdf");
    }

    async function guardarEnBaseDeDatos(nombre, documento, total) {
        const criterio = document.getElementById('busquedaTrabajador').value;
        try {
            const url = `guardarPago.jsp?nombre=\${encodeURIComponent(nombre)}&documento=\${encodeURIComponent(documento)}&total=\${total}&criterio=\${encodeURIComponent(criterio)}`;
            await fetch(url);
            Swal.fire({ icon: 'success', title: 'Éxito', text: 'Pago registrado.' });
            document.getElementById('listaResultados').innerHTML = '<div class="info-edicion">Pago procesado.</div>';
        } catch (e) { console.error(e); }
    }

function generarFacturaSupervisorPDF() {
        const { jsPDF } = window.jspdf;
        const doc = new jsPDF();

        const supervisor = document.getElementById('busquedaSupervisor').value;
        // CORRECCIÓN: Usamos el ID 'salarioSupervisor' que es el que tienes en tu HTML
        const salario = parseFloat(document.getElementById('salarioSupervisor').value) || 0;

        if (!supervisor || salario <= 0) {
            Swal.fire('Atención', 'Verifique el nombre del supervisor y su salario semanal.', 'warning');
            return;
        }

        // --- DISEÑO ORIGINAL (image_d1b346.png) ---
        doc.setFillColor(4, 120, 87);
        doc.rect(0, 0, 210, 45, 'F');
        
        doc.setTextColor(255, 255, 255);
        doc.setFontSize(22);
        doc.text("AGRITRACK PLUS", 20, 25);
        doc.setFontSize(10);
        doc.text("Comprobante de Pago - Rol Supervisor", 20, 35);

        doc.setTextColor(55, 65, 81);
        doc.setFontSize(11);
        doc.text("Supervisor: " + supervisor, 20, 60);
        doc.text("Fecha: " + new Date().toLocaleDateString(), 20, 67);

        let yPos = 85;
        doc.setFillColor(55, 65, 81);
        doc.rect(20, yPos, 170, 8, 'F');
        doc.setTextColor(255, 255, 255);
        doc.text("Lista de Cultivos Supervisados", 25, yPos + 5);
        
        yPos += 15;
        doc.setTextColor(55, 65, 81);
        
        tareasSupervisor.forEach((t) => {
            doc.text("• " + (t.cultivo || "Cultivo registrado"), 25, yPos);
            yPos += 10;
        });

        yPos += 5;
        doc.setDrawColor(200, 200, 200);
        doc.line(20, yPos, 190, yPos);
        yPos += 15;

        doc.text("Concepto: Salario de supervisión semanal", 25, yPos);
        doc.text("$" + salario.toLocaleString(), 165, yPos);

        yPos += 15;
        doc.setFillColor(4, 120, 87);
        doc.rect(130, yPos, 60, 15, 'F');
        doc.setTextColor(255, 255, 255);
        doc.text("TOTAL: $" + salario.toLocaleString(), 135, yPos + 10);

        // Guardado seguro
        const url = "guardarPago.jsp?nombre=" + encodeURIComponent(supervisor) + 
                    "&total=" + salario + "&rol=supervisor";
        fetch(url);

        doc.save("Comprobante_Supervisor_" + supervisor.replace(/\s+/g, '_') + ".pdf");
        
        fetch(url).then(() => {
        Swal.fire('Éxito', 'Factura generada y pago guardado', 'success');
    }).catch(err => console.error("Error:", err));
    }


    async function guardarPagoSupervisor(nombre, total) {
        try {
            const url = `guardarPago.jsp?nombre=\${encodeURIComponent(nombre)}&documento=SN&total=\${total}&criterio=\${encodeURIComponent(nombre)}`;
            await fetch(url);
            Swal.fire({ icon: 'success', title: 'Éxito', text: 'Pago supervisor registrado.' });
            document.getElementById('infoSupervisor').innerHTML = '<div class="info-edicion">Pago procesado.</div>';
        } catch (e) { console.error(e); }
    }

    window.onload = function() {
        // No limpiamos el valor de jornales para que el usuario no tenga que escribirlos siempre
        document.getElementById('busquedaTrabajador').value = "";
        document.getElementById('salarioSupervisor').value = "";
        document.getElementById('busquedaSupervisor').value = "";
    };
</script>
</body>
</html>