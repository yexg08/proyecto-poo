package com.pou.logic;

public class Puntaje {

    private int puntos;
    private int record;
    private double yInicial;

    public Puntaje(double yInicial) {
        this.yInicial = yInicial;
        this.puntos   = 0;
        this.record   = 0;
    }

    /** Call every frame with Pou's current world Y. Score = max height in "metres". */
    public void actualizar(double yActual) {
        int altura = (int) ((yInicial - yActual) / 8.0);
        if (altura > puntos) {
            puntos = altura;
            if (puntos > record) record = puntos;
        }
    }

    public void reiniciar(double yInicial) {
        this.yInicial = yInicial;
        this.puntos   = 0;
    }

    public int getPuntos() { return puntos; }
    public int getRecord() { return record; }
}
