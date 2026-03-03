/**
 * Admin_Logic.js
 * Maneja la interactividad y seguridad visual de AgriTrack Plus
 */

document.addEventListener("DOMContentLoaded", function() {
    // 1. Aplicar restricciones de seguridad visual según el Rol
    aplicarFiltroSeguridad();
    
    console.log("AgriTrack Plus: Lógica de interfaz cargada.");
});

/**
 * Oculta elementos que el Supervisor no tiene permiso de ver
 */
function aplicarFiltroSeguridad() {
    // Verificamos si la variable ROL_USUARIO existe
    if (typeof ROL_USUARIO === 'undefined') {
        console.warn("No se detectó la variable ROL_USUARIO.");
        return;
    }

    if (ROL_USUARIO === "supervisor") {
        // Buscar todos los elementos con la clase 'solo-admin' y ocultarlos
        const elementosPrivados = document.querySelectorAll('.solo-admin');
        elementosPrivados.forEach(function(el) {
            el.style.display = 'none';
        });

        // Cambiar dinámicamente el texto de la bienvenida
        const subtexto = document.querySelector('.main__texto');
        if (subtexto) {
            subtexto.innerText = "Panel de Supervisión de Cultivos y Personal.";
        }
        
        console.log("Acceso restringido: Modo Supervisor activo.");
    } else {
        console.log("Acceso total: Modo Administrador activo.");
    }
}

/**
 * Muestra u oculta la tarjeta de perfil lateral
 */
function togglePerfil() {
    const card = document.getElementById("cardDatos");
    if (card) {
        card.classList.toggle("activo");
    }
}

/**
 * Cierra la tarjeta de perfil si se hace clic fuera de ella
 */
window.onclick = function(event) {
    const card = document.getElementById("cardDatos");
    const circulo = document.querySelector('.cirulo__perfil');
    
    // El "?" verifica si el elemento existe antes de preguntar por sus clases
    if (card && card.classList.contains('activo')) {
        // Verificamos que el círculo exista antes de usar .contains
        if (!card.contains(event.target) && (circulo && !circulo.contains(event.target))) {
            card.classList.remove('activo');
        }
    }
};