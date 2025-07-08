package com.example.prueba_01.modelo;

public class Opcion {
    private String Descripcion;
    private Boolean Seleccionada;

    public Opcion(String descripcion) {
        Descripcion = descripcion;
        Seleccionada = false;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public Boolean getSeleccionada() {
        return Seleccionada;
    }

    public void setSeleccionada(Boolean seleccionada) {
        Seleccionada = seleccionada;
    }
}
