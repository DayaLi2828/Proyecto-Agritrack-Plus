<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Pagos - AgritrackPlus</title>
    <link rel="stylesheet" href="../../asset/Administrador/style_Gestion_Pagos.css">
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
        </div>
    </main>

<script>
    /**
     * BUSCA LAS TAREAS
     */
    async function buscarActividad() {
        const criterio = document.getElementById('busquedaTrabajador').value;
        const listaResultados = document.getElementById('listaResultados');

        if (!criterio) {
            alert("Por favor, ingresa el nombre o documento.");
            return;
        }

        try {
            const url = "getTareas.jsp?criterio=" + encodeURIComponent(criterio);
            const response = await fetch(url);
            const tareas = await response.json();

            listaResultados.innerHTML = ""; 

            if (tareas.length === 0) {
                listaResultados.innerHTML = `
                    <div class="info-edicion">
                        No se encontraron tareas para este usuario.
                    </div>`;
                return;
            }

            tareas.forEach(t => {
                const jornadaDB = (t.jornada || "").toLowerCase();
                const esCompleto = jornadaDB.includes('completo') || jornadaDB.includes('entero');
                
                const tipoJornada = esCompleto ? "completo" : "medio";

                const card = document.createElement('div');
                card.className = 'fila__stock';
                card.innerHTML = `
                    <div class="campo">
                        <p class="nombre__trabajador"><strong>Tarea: </strong><span class="txt-tarea">\${t.tarea}</span></p>
                        <p class="ayuda-texto">Estado: \${t.estado}</p>
                    </div>
                    <div class="campo">
                        <span class="etiqueta-jornada" data-tipo="\${tipoJornada}">
                            \${t.jornada}
                        </span>
                    </div>
                `;
                listaResultados.appendChild(card);
            });

        } catch (error) {
            console.error("Error:", error);
            alert("Error al conectar con el servidor.");
        }
    }

    /**
     * GENERA EL PDF Y GUARDA EN BD
     */
    function generarFacturaPDF() {
        const { jsPDF } = window.jspdf;
        const doc = new jsPDF();

        const trabajador = document.getElementById('busquedaTrabajador').value;
        const vMedio = parseFloat(document.getElementById('valorMedio').value) || 0;
        const vCompleto = parseFloat(document.getElementById('valorCompleto').value) || 0;
        const filas = document.querySelectorAll('.fila__stock');
        
        if (vMedio <= 0 || vCompleto <= 0 || filas.length === 0) {
            alert("Verifique los valores y que existan tareas en la lista.");
            return;
        }

        // Diseño PDF (Este diseño es interno del archivo generado, no afecta tu web)
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
        doc.text("Tarea", 25, yPos + 5);
        doc.text("Monto", 165, yPos + 5);
        yPos += 15;

        filas.forEach((fila) => {
            const nombre = fila.querySelector('.txt-tarea').textContent;
            const tipo = fila.querySelector('.etiqueta-jornada').getAttribute('data-tipo');
            const subtotal = (tipo === 'completo') ? vCompleto : vMedio;
            totalPagar += subtotal;

            doc.setTextColor(31, 41, 55);
            doc.text(nombre, 25, yPos);
            doc.text("$" + subtotal.toLocaleString(), 165, yPos);
            yPos += 10;
        });

        doc.setFillColor(4, 120, 87);
        doc.rect(130, yPos, 60, 15, 'F');
        doc.setTextColor(255, 255, 255);
        doc.text("TOTAL A PAGAR: $" + totalPagar.toLocaleString(), 135, yPos + 10);

        guardarEnBaseDeDatos(trabajador, "SN", totalPagar); 
        doc.save("Factura_" + trabajador.replace(/\s+/g, '_') + ".pdf");
    }

    async function guardarEnBaseDeDatos(nombre, documento, total) {
        try {
            await fetch(`guardarPago.jsp?nombre=\${encodeURIComponent(nombre)}&documento=\${documento}&total=\${total}`);
            console.log("Registro guardado.");
        } catch (e) { console.error(e); }
    }

    // Limpiar campos al cargar la página
    window.onload = function() {
        document.getElementById('valorMedio').value = "";
        document.getElementById('valorCompleto').value = "";
        document.getElementById('busquedaTrabajador').value = "";
    };
</script>
</body>
</html>