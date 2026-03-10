package com.agritrack.agritrackplus.modelo;

public class Tarea {
    private int id;
    private int cultivoId;
    private String nombreCultivo;      // Para mostrar "Tomate Cherry"
    private String nombreTarea;        // Para mostrar "Riego"
    private String descripcion;
    private String jornada;            // Usado para "Mañana/Tarde"
    private String nombreTrabajador;   // Para mostrar quién la hace
    private String estado;
    public Tarea() {
    }

    // GETTERS Y SETTERS (Esenciales para que el JSP no marque error)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCultivoId() { return cultivoId; }
    public void setCultivoId(int cultivoId) { this.cultivoId = cultivoId; }

    public String getNombreCultivo() { return nombreCultivo; }
    public void setNombreCultivo(String nombreCultivo) { this.nombreCultivo = nombreCultivo; }

    public String getNombreTarea() { return nombreTarea; }
    public void setNombreTarea(String nombreTarea) { this.nombreTarea = nombreTarea; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getJornada() { return jornada; }
    public void setJornada(String jornada) { this.jornada = jornada; }

    public String getNombreTrabajador() { return nombreTrabajador; }
    public void setNombreTrabajador(String nombreTrabajador) { this.nombreTrabajador = nombreTrabajador; }

    public String getEstado() {return estado;}

    public void setEstado(String estado) {this.estado = estado;}

}