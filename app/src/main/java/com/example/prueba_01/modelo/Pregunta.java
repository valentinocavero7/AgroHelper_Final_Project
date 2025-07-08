package com.example.prueba_01.modelo;

import java.util.ArrayList;

public class Pregunta {
    private String Texto;
    private ArrayList<Opcion> Opciones;

    public Pregunta(String texto, ArrayList<Opcion> opciones) {
        Texto = texto;
        Opciones = opciones;
    }

    public String getTexto() {
        return Texto;
    }

    public void setTexto(String texto) {
        Texto = texto;
    }

    public ArrayList<Opcion> getOpciones() {
        return Opciones;
    }

    public void setOpciones(ArrayList<Opcion> opciones) {
        Opciones = opciones;
    }

    // para resetear el bool de las pregnta
    public void reiniciarEstado() {
        for(Opcion opcion : Opciones) {
            opcion.setSeleccionada(false);
        }
    }
}
