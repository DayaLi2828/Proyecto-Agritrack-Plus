<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
    <%-- ===== FIN SECCIÓN PAGO SUPERVISOR ===== --%>
        </div>

    </main>
    
<script>
    // Variable global para tareas del supervisor
    let tareasSupervisor = [];

    async function buscarActividad() {
        const criterio = document.getElementById('busquedaTrabajador').value;
        const listaResultados = document.getElementById('listaResultados');

        if (!criterio) {
            Swal.fire({
                icon: 'warning',
                title: 'Campo requerido',
                text: 'Por favor, ingresa el nombre o documento.'
            });
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
            Swal.fire({
                icon: 'error',
                title: 'Error de conexión',
                text: 'Error al conectar con el servidor.'
            });
    }   }

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

        doc.setFillColor(55, 65, 81);
        doc.rect(20, yPos, 170, 8, 'F');
        doc.setTextColor(255, 255, 255);
        doc.setFontSize(10);
        doc.text("Cultivo - Tarea", 25, yPos + 5);
        doc.text("Monto", 165, yPos + 5);
        yPos += 15;

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

        doc.setFillColor(4, 120, 87);
        doc.rect(130, yPos, 60, 15, 'F');
        doc.setTextColor(255, 255, 255);
        doc.text("TOTAL A PAGAR: $" + totalPagar.toLocaleString(), 135, yPos + 10);

        guardarEnBaseDeDatos(trabajador, "SN", totalPagar);
        doc.save("Factura_" + trabajador.replace(/\s+/g, '_') + ".pdf");
    }

    async function guardarEnBaseDeDatos(nombre, documento, total) {
        const criterio = document.getElementById('busquedaTrabajador').value;
        try {
            const url = "guardarPago.jsp?nombre=" + encodeURIComponent(nombre)
                      + "&documento=" + encodeURIComponent(documento)
                      + "&total=" + total
                      + "&criterio=" + encodeURIComponent(criterio);
            await fetch(url);
                Swal.fire({
                    icon: 'success',
                    title: 'Proceso exitoso',
                    text: 'Pago registrado y tareas marcadas como pagadas.'
                });            document.getElementById('listaResultados').innerHTML = '<div class="info-edicion">Pago procesado con éxito.</div>';
        } catch (e) {
            console.error("Error al guardar:", e);
        }
    }

    async function buscarSupervisor() {
        const criterio = document.getElementById('busquedaSupervisor').value;
        const infoDiv = document.getElementById('infoSupervisor');
            if (!criterio) { 
                Swal.fire({
                    icon: 'warning',
                    title: 'Campo requerido',
                    text: 'Por favor, ingresa el nombre o documento del supervisor.'
                });
                return; 
            }try {
            const url = "buscarSupervisor.jsp?criterio=" + encodeURIComponent(criterio);
            const response = await fetch(url);
            const data = await response.json();
            if (!data || d
            ata.error) {
                infoDiv.innerHTML = '<div class="info-edicion">No se encontro ningun supervisor con ese criterio.</div>';
                tareasSupervisor = [];
                return;
            }

            tareasSupervisor = data.tareas || [];

            infoDiv.innerHTML =
                '<div class="fila__stock">' +
                    '<div class="campo">' +
                        '<p class="nombre__trabajador"><strong>Supervisor: </strong>' + data.nombre + '</p>' +
                        '<p class="ayuda-texto">Cultivos supervisados: ' + data.totalCultivos + '</p>' +
                    '</div>' +
                '</div>';
        } catch (error) {
            console.error("Error:", error);
            Swal.fire({
                icon: 'error',
                title: 'Error de conexión',
                text: 'Error al conectar con el servidor.'
            });
        }
    }

    function generarFacturaSupervisorPDF() {
        const { jsPDF } = window.jspdf;
        const doc = new jsPDF();
        const supervisor = document.getElementById('busquedaSupervisor').value;
        const salario = parseFloat(document.getElementById('salarioSupervisor').value) || 0;
        const infoDiv = document.getElementById('infoSupervisor');

        // Validaciones previas
        if (!supervisor || infoDiv.querySelector('.info-edicion')) { 
            Swal.fire({ icon: 'warning', title: 'Atención', text: 'Busca y confirma un supervisor primero.' });
            return; 
        }
        if (salario <= 0) {
            Swal.fire({ icon: 'warning', title: 'Dato inválido', text: 'Ingresa el salario semanal.' });
            return; 
        }

        // --- DISEÑO DEL ENCABEZADO ---
        doc.setFillColor(4, 120, 87); // Color verde Agritrack
        doc.rect(0, 0, 210, 40, 'F');
        doc.setTextColor(255, 255, 255);
        doc.setFontSize(22);
        doc.text("AGRITRACK PLUS", 20, 25);
        doc.setFontSize(10);
        doc.text("Comprobante de Pago - Rol Supervisor", 20, 33);

        // Datos del documento
        doc.setTextColor(55, 65, 81);
        doc.setFontSize(11);
        doc.text("Supervisor: " + supervisor, 20, 55);
        doc.text("Fecha: " + new Date().toLocaleDateString(), 20, 62);

        let yPos = 75;

        // --- SECCIÓN DE CULTIVOS SUPERVISADOS ---
        doc.setFillColor(55, 65, 81);
        doc.rect(20, yPos, 170, 8, 'F');
        doc.setTextColor(255, 255, 255);
        doc.text("Lista de Cultivos Supervisados", 25, yPos + 5);
        yPos += 15;

        doc.setTextColor(31, 41, 55);

        if (tareasSupervisor && tareasSupervisor.length > 0) {
            // Filtrar cultivos únicos para que no se repitan
            const cultivosUnicos = [...new Set(tareasSupervisor.map(t => t.cultivo))];

            cultivosUnicos.forEach((nombreCultivo) => {
                doc.text("• " + (nombreCultivo || "Cultivo no especificado"), 25, yPos);
                yPos += 10;

                // Control de salto de página
                if (yPos > 270) { doc.addPage(); yPos = 20; }
            });
        } else {
            doc.setFontSize(10);
            doc.setTextColor(100, 100, 100);
            doc.text("No se encontraron registros de cultivos específicos.", 25, yPos);
            yPos += 10;
        }

        // --- SECCIÓN DE PAGO ---
        yPos += 10;
        doc.setDrawColor(200, 200, 200);
        doc.line(20, yPos, 190, yPos); // Línea divisoria
        yPos += 10;

        doc.setFontSize(11);
        doc.setTextColor(31, 41, 55);
        doc.text("Concepto: Salario de supervisión semanal", 25, yPos);
        doc.text("$" + salario.toLocaleString(), 160, yPos);

        yPos += 15;
        doc.setFillColor(4, 120, 87);
        doc.rect(130, yPos, 60, 12, 'F');
        doc.setTextColor(255, 255, 255);
        doc.text("TOTAL: $" + salario.toLocaleString(), 135, yPos + 8);

        // Guardar en BD y descargar
        guardarPagoSupervisor(supervisor, salario);
        doc.save("Factura_Supervisor_" + supervisor.replace(/\s+/g, '_') + ".pdf");
    }

    async function guardarPagoSupervisor(nombre, total) {
        try {
            const url = "guardarPago.jsp?nombre=" + encodeURIComponent(nombre)
                      + "&documento=SN"
                      + "&total=" + total
                      + "&criterio=" + encodeURIComponent(nombre);
            await fetch(url);
            Swal.fire({
                    icon: 'success',
                    title: 'Pago registrado',
                    text: 'Pago del supervisor registrado correctamente.'
                });            
            document.getElementById('infoSupervisor').innerHTML = '<div class="info-edicion">Pago procesado con exito.</div>';
            document.getElementById('salarioSupervisor').value = "";
            document.getElementById('busquedaSupervisor').value = "";
        } catch (e) { console.error("Error al guardar:", e); }
    }

    window.onload = function() {
        document.getElementById('valorMedio').value = "";
        document.getElementById('valorCompleto').value = "";
        document.getElementById('busquedaTrabajador').value = "";
        document.getElementById('salarioSupervisor').value = "";
        document.getElementById('busquedaSupervisor').value = "";
    };
</script>
</body>
</html>