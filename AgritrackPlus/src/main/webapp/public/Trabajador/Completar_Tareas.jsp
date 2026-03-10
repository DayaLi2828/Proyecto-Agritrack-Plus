<!DOCTYPE html>
<html lang="es">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Document</title>
    <link rel="stylesheet" href="../../asset/Trabajador/style_completarTareas.css" />
  </head>
  <body>
    <header>
        <a href="Trabajador.jsp">
        <div class="icono__devolver">
          <img src="../../asset/imagenes/devolver.png" id="icono de devolver">
        </div>
      </a>
      <div class="contenedor__titulo">
        <div class="contenedor__logo">
            <img
              class="logo"
              src="../../asset/imagenes/hoja (3).png"
              alt="hoja del logo"
            />
        </div>
        <h1 class="titulo">Mis tareas asignadas</h1>
      </div>
    </header>
    <main>
      <div class="buscardor__tareas">
        <div class="contenedor__buscar">
          <input
            type="text"
            class="input__buscador"
            placeholder="Buscar..."
            aria-label="Buscar"/>
          <img
            class="icono__buscador"
            src="../../asset/imagenes/lupa.png"
            alt="Icono de búsqueda"/>
        </div>

        <select class="select__tareas">
          <option value="todas">Todas las tareas</option>
          <option value="pendientes">Tareas pendientes</option>
          <option value="completadas">Tareas completadas</option>
        </select>
      </div>
      <div class="tarjetas__tareas">
        <div class="tarea completadas">
          <div class="contenedor__nombre">
            <h3 class="nombre__tarea">Riego matutino</h3>
            <h3 class="estado__tarea completada">completada</h3>
          </div>
          <div class="cajas__cultivos">
            <span class="nombre__cultivo">🌱Tomate cherry</span>
            <span class="fecha__cultivo">📅 20/12/2024</span>
            <span class="jornada__cultivo">⏰Mañana</span>
            <img class="icono__flecha" src="../../asset/imagenes/proximo.png" id="imagen de una flecha">
          </div>
          <p class="descripcion__cultivo">
            Realizar riego profundo en cultivo de tomate cherry, asegurando que
            el agua llegue a las raíces
          </p>
        </div>

        <div class="tarea pendientes">
          <div class="contenedor__nombre">
            <h3 class="nombre__tarea">Aplicar fertilizantes APK</h3>
            <h3 class="estado__tarea pendiente">pendiente</h3>
          </div>
          <div class="cajas__cultivos">
            <span class="nombre__cultivo">🌱Lechuga romana</span>
            <span class="fecha__cultivo">📅 21/12/2024</span>
            <span class="jornada__cultivo">⏰Tarde</span>
            <a class="flecha__link" href="index_tareaTrabajador.html">
              <img class="icono__flecha" src="../../asset/imagenes/proximo.png" id="imagen de una flecha">
            </a>

          </div>
          <p class="descripcion__cultivo">
            Aplicación de NPK 10-20-10 en lechuga romana, 50kg por hectárea
          </p>
        </div>

        <div class="tarea completadas">
          <div class="contenedor__nombre">
            <h3 class="nombre__tarea">Limpieza de maleza</h3>
            <h3 class="estado__tarea completada">completada</h3>
          </div>
          <div class="cajas__cultivos">
            <span class="nombre__cultivo">🌱Zanahoria</span>
            <span class="fecha__cultivo">📅 20/12/2024</span>
            <span class="jornada__cultivo">⏰Mañana</span>
            <img class="icono__flecha" src="../../asset/imagenes/proximo.png" id="imagen de una flecha">
          </div>
          <p class="descripcion__cultivo">
            Remover maleza del área de cultivo manualmente
          </p>
        </div>
      </div>
      <div class="tarjetas__tareas">
        <div class="tarea pendientes">
          <div class="contenedor__nombre">
            <h3 class="nombre__tarea">control de plagas</h3>
            <h3 class="estado__tarea pendiente">pendiente</h3>
          </div>
          <div class="cajas__cultivos">
            <span class="nombre__cultivo">🌱Aguacate criollo</span>
            <span class="fecha__cultivo">📅 30/12/2024</span>
            <span class="jornada__cultivo">⏰tarde</span>
            <img class="icono__flecha" src="../../asset/imagenes/proximo.png" id="imagen de una flecha">
          </div>
          <p class="descripcion__cultivo">
            Inspección y control preventivo de pulgones
          </p>
        </div>

        <div class="tarea completadas">
          <div class="contenedor__nombre">
            <h3 class="nombre__tarea">Cosecha</h3>
            <h3 class="estado__tarea completada">completada</h3>
          </div>
          <div class="cajas__cultivos">
            <span class="nombre__cultivo">🌱Lechuga romana</span>
            <span class="fecha__cultivo">📅 3/1/2025</span>
            <span class="jornada__cultivo">⏰Mañana</span>
            <img class="icono__flecha" src="../../asset/imagenes/proximo.png" id="imagen de una flecha">
          </div>
          <p class="descripcion__cultivo">
            Cosecha selectiva de lechugas maduras
          </p>
        </div>
      </div>
    </main>
  </body>
</html>
