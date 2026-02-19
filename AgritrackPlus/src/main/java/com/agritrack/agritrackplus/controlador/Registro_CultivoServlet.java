package com.agritrack.agritrackplus.controlador;

import com.agritrack.agritrackplus.DAO.Registro_CultivoDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "RegistroCultivoServlet", urlPatterns = {"/RegistroCultivoServlet"})
public class Registro_CultivoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nombre = request.getParameter("nombre");
        String fechaSiembra = request.getParameter("fecha_siembra");
        String fechaCosecha = request.getParameter("fecha_cosecha");
        String ciclo = request.getParameter("ciclo");
        String estado = request.getParameter("estado");

        Registro_CultivoDAO dao = new Registro_CultivoDAO();
        boolean exito = dao.registrar(nombre, fechaSiembra, fechaCosecha, ciclo, estado);

        if (exito) {
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Cultivos_Registrados.jsp?registro=exitoso");
        } else {
            response.sendRedirect(request.getContextPath() + "/public/Administrador/Registro_Cultivos.jsp?error=true");
        }
    }
}