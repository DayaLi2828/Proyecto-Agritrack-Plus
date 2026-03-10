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
                    <button type="button" class="boton__enviar" onclick="buscarActividad()" style="margin-top: 10px; background-color: #047857;">Buscar Tareas Completadas</button>
                </div>
            </div>

            <div class="contendor__cajas">
                <div class="contendor__subtitulo">
                    <div class="caja__logo">
                        <img class="logo" src="../../asset/imagenes/stock.png" alt="valores">
                    </div>
                    <h2 class="subtitulo">Configuración de Jornal Semanal</h2>
                </div>
                <div class="fechas">
                    <div class="campo">
                        <label class="nombre__trabajador">Valor Medio Día (6am - 12pm)</label>
                        <input type="number" id="valorMedio" placeholder="$ Valor medio jornal">
                    </div>
                    <div class="campo">
                        <label class="nombre__trabajador">Valor Día Completo (6am - 6pm)</label>
                        <input type="number" id="valorCompleto" placeholder="$ Valor día completo">
                    </div>
                </div>
            </div>

            <div class="contendor__cajas">
                <div class="contendor__subtitulo">
                    <div class="caja__logo">
                        <img class="logo" src="../../asset/imagenes/planta (2).png" alt="tareas">
                    </div>
                    <h2 class="subtitulo">Tareas Realizadas (Solo Completadas)</h2>
                </div>
                
                <div id="contenedorTareas">
                    <div class="info-edicion">
                        Las tareas con estado "Pendiente" o "En Proceso" no se muestran en esta lista.
                    </div>
                    
                    <div id="listaResultados">
                        </div>
                </div>

                <button type="button" class="boton__enviar" onclick="generarFacturaPDF()">
                    Generar Factura PDF
                </button>
            </div>
        </div>
    </main>

<script>
    /**
     * Busca las tareas del trabajador usando el JSP de servicio
     */
    async function buscarActividad() {
        const criterio = document.getElementById('busquedaTrabajador').value;
        const listaResultados = document.getElementById('listaResultados');

        if (!criterio) {
            alert("Por favor, ingresa el nombre o documento del trabajador.");
            return;
        }

        try {
            const response = await fetch(`getTareas.jsp?criterio=\${encodeURIComponent(criterio)}`);
            const tareas = await response.json();

            listaResultados.innerHTML = ""; 

            if (tareas.length === 0) {
                listaResultados.innerHTML = `<div class="info-edicion" style="background: #fee2e2; color: #b91c1c;">No se encontraron tareas completadas para este usuario.</div>`;
                return;
            }

            tareas.forEach(t => {
                const fila = document.createElement('div');
                fila.className = 'fila__stock';
                
                // Determinamos el tipo de jornada para el cálculo y el color
                const esCompleto = t.jornada.toLowerCase().includes('completo');
                const tipoJornada = esCompleto ? 'completo' : 'medio';
                const colorFondo = esCompleto ? '#8b5cf6' : '#3b82f6'; // Morado para completo, Azul para medio

                fila.innerHTML = `
                    <div class="campo" style="flex: 2;">
                        <p class="nombre__trabajador"><strong>Tarea:</strong> <span class="txt-tarea">\${t.tarea}</span></p>
                        <p class="ayuda-texto" style="background: rgba(16,185,129,0.1); color: #047857 !important;">
                            Estado: \${t.estado}
                        </p>
                    </div>
                    <div class="campo" style="flex: 1; display: flex; justify-content: flex-end; align-items: center;">
                        <span class="etiqueta-jornada" 
                              data-tipo="\${tipoJornada}" 
                              style="background-color: \${colorFondo}; color: white; padding: 6px 14px; border-radius: 20px; font-size: 0.8rem; font-weight: bold; text-transform: uppercase;">
                            \${t.jornada}
                        </span>
                    </div>
                `;
                listaResultados.appendChild(fila);
            });

        } catch (error) {
            console.error("Error al buscar tareas:", error);
            alert("Error de conexión al obtener tareas.");
        }
    }

  /**
     * Genera el PDF con diseño de "cajitas", colores y encabezado profesional
     */
  function generarFacturaPDF() {
    const { jsPDF } = window.jspdf;
    const doc = new jsPDF();

    const trabajador = document.getElementById('busquedaTrabajador').value;
    const vMedio = parseFloat(document.getElementById('valorMedio').value) || 0;
    const vCompleto = parseFloat(document.getElementById('valorCompleto').value) || 0;
    const filas = document.querySelectorAll('.fila__stock');
    
    if (filas.length === 0 || !trabajador) {
        alert("No hay tareas cargadas.");
        return;
    }

    // --- ENCABEZADO ---
    doc.setFillColor(4, 120, 87); 
    doc.rect(0, 0, 210, 40, 'F');
    doc.setTextColor(255, 255, 255);
    doc.setFontSize(22);
    doc.setFont("helvetica", "bold");
    doc.text("AGRITRACK PLUS", 20, 25);
    
    // --- DATOS RECEPTOR ---
    doc.setTextColor(0, 0, 0);
    doc.setFontSize(11);
    doc.setFillColor(243, 244, 246);
    doc.rect(20, 50, 170, 20, 'F');
    doc.text(`Trabajador: \${trabajador}`, 25, 58);
    doc.text(`Fecha: \${new Date().toLocaleDateString()}`, 25, 65);

    // --- TABLA DE TAREAS ---
    let yPos = 85; 
    doc.setFont("helvetica", "bold");
    doc.text("DESCRIPCIÓN DE TAREAS", 20, 80);

    // Encabezado
    doc.setFillColor(55, 65, 81);
    doc.rect(20, yPos, 170, 12, 'F'); 
    doc.setTextColor(255, 255, 255);
    doc.text("Tarea (Estado)", 25, yPos + 8); // Cambié el título para aclarar el estado
    doc.text("Jornada", 110, yPos + 8);
    doc.text("Subtotal", 165, yPos + 8);

    let totalPagar = 0;
    doc.setTextColor(0, 0, 0);
    doc.setFont("helvetica", "normal");
    
    yPos += 12;

    filas.forEach((fila, index) => {
        const tareaNombre = fila.querySelector('.txt-tarea').textContent;
        const estadoElemento = fila.querySelector('.ayuda-texto').textContent;
        const etiqueta = fila.querySelector('.etiqueta-jornada');
        
        const tipo = etiqueta.getAttribute('data-tipo');
        const textoJornada = etiqueta.textContent.trim();
        
        // 1. Determinar base (35.000 o lo que pongas en el input)
        let subtotal = (tipo === 'completo') ? vCompleto : vMedio;

        // 2. Lógica de Pago Proporcional: Si está "En Proceso", se paga el 50%
        let detalleEstado = "";
        if (estadoElemento.includes("En Proceso")) {
            subtotal = subtotal / 2;
            detalleEstado = " (Proceso 50%)";
        }

        totalPagar += subtotal;

        // Dibujar fila
        doc.setDrawColor(200, 200, 200);
        doc.rect(20, yPos, 170, 12);
        
        // Escribimos la tarea y si tiene descuento por estar en proceso
        const textoTareaFinal = `\${index + 1}. \${tareaNombre.substring(0, 25)}\${detalleEstado}`;
        doc.text(textoTareaFinal, 25, yPos + 8);
        doc.text(textoJornada, 110, yPos + 8);
        doc.text(`$\${subtotal.toLocaleString()}`, 165, yPos + 8);

        yPos += 12;
    });

    // --- TOTAL FINAL ---
    yPos += 15;
    doc.setFillColor(4, 120, 87);
    doc.rect(120, yPos, 70, 15, 'F');
    doc.setTextColor(255, 255, 255);
    doc.setFontSize(14);
    doc.setFont("helvetica", "bold");
    doc.text("TOTAL:", 125, yPos + 10);
    doc.text(`$\${totalPagar.toLocaleString()}`, 152, yPos + 10);

    doc.save(`Factura_\${trabajador.replace(/\s+/g, '_')}.pdf`);
}
</script>
</body>
</html>