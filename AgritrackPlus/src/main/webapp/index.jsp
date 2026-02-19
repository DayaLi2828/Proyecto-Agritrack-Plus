<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>AgriTrack Plus</title>
  <link rel="stylesheet" href="style_iniciarSesion.css">
</head>
<body>
  <div class="contenedor">
    <header>
      <div class="contenedor__login">
        <img class="logo" src="asset/imagenes/hoja (2).png" alt="hoja del logo">
        <h1 class="contenedor__titulo">AGRITRACK PLUS</h1>
      </div>
    </header>
    <main>
      <h2 class="contenedor__subtitulo">Inicia sesión con tus credenciales</h2>

      <% if ("true".equals(request.getParameter("error"))) { %>
        <p style="color:red;">Correo o contraseña incorrectos. Intenta de nuevo.</p>
      <% } %>
      <% if ("exitoso".equals(request.getParameter("registro"))) { %>
        <p style="color:green;">Registro exitoso. Ya puedes iniciar sesión.</p>
      <% } %>

      <form class="formulario__login" method="post" action="LoginServlet">
        <label for="email" class="email">Correo electrónico</label>
        <input type="email" id="email" name="email" placeholder="Ingresa tu correo" required />

        <label for="password" class="password">Contraseña</label>
        <input type="password" id="password" name="password" placeholder="Ingresa tu contraseña" required />

        <div class="boton__login">
          <button type="submit">Iniciar sesión</button>
        </div>
      </form>
    </main>
  </div>
</body>
</html>