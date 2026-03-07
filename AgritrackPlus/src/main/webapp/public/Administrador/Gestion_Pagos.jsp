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
        <a href="Dashboard.jsp">
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
                    <button type="button" class="boton__agregar__producto" onclick="buscarActividad()">Buscar Tareas de la Semana</button>
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
                    <h2 class="subtitulo">Tareas Realizadas (Completadas / En Proceso)</h2>
                </div>
                
                <div id="contenedorTareas">
                    <div class="info-edicion">
                        Las tareas con estado "Pendiente" no se muestran en esta lista para el cálculo de pago.
                    </div>
                    
                    <div id="listaResultados">
                        <div class="fila__stock">
                            <div class="campo" style="flex: 2;">
                                <p class="nombre__trabajador"><strong>Tarea:</strong> Fumigación Lote 1</p>
                                <p class="ayuda-texto" style="background: rgba(16,185,129,0.1); color: #047857 !important;">Estado: Completada</p>
                            </div>
                            <div class="campo" style="flex: 1;">
                                <select class="tipoJornal">
                                    <option value="completo">Día Completo</option>
                                    <option value="medio">Medio Día</option>
                                </select>
                            </div>
                        </div>
                    </div>
                </div>

                <button type="button" class="boton__enviar" onclick="generarFacturaPDF()">
                    Generar Factura PDF
                </button>
            </div>
        </div>
    </main>
</body>
</html>