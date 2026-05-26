package com.pou.logic;

/**
 * Gestiona la puntuación del jugador expresada como altura máxima alcanzada en metros.
 * <p>
 * La puntuación se incrementa cuando Pou supera su altura máxima previa;
 * nunca disminuye dentro de una misma partida. El récord histórico se conserva
 * entre partidas mientras la aplicación esté abierta.
 * </p>
 */
public class Puntaje {

    private int puntos;
    private int record;
    private double yInicial;

    /**
     * Construye el sistema de puntuación con la posición Y inicial de Pou.
     *
     * @param yInicial coordenada Y del mundo en la que Pou comienza la partida;
     *                 sirve como referencia para calcular la altura ascendida
     */
    public Puntaje(double yInicial) {
        this.yInicial = yInicial;
        this.puntos   = 0;
        this.record   = 0;
    }

    /**
     * Actualiza la puntuación en cada fotograma con la posición actual de Pou.
     * La altura se calcula como {@code (yInicial - yActual) / 8}, lo que convierte
     * píxeles del mundo en "metros" de juego. Solo actualiza si la altura es mayor
     * que la máxima alcanzada en esta partida.
     *
     * @param yActual coordenada Y del mundo donde se encuentra Pou en este fotograma
     */
    public void actualizar(double yActual) {
        int altura = (int) ((yInicial - yActual) / 8.0);
        if (altura > puntos) {
            puntos = altura;
            if (puntos > record) record = puntos;
        }
    }

    /**
     * Reinicia la puntuación de la partida actual a cero y actualiza la referencia
     * de posición inicial. El récord histórico no se modifica.
     *
     * @param yInicial nueva coordenada Y inicial de Pou para la partida que comienza
     */
    public void reiniciar(double yInicial) {
        this.yInicial = yInicial;
        this.puntos   = 0;
    }

    /**
     * Devuelve la puntuación de la partida actual en metros.
     *
     * @return altura máxima alcanzada en la partida actual
     */
    public int getPuntos() { return puntos; }

    /**
     * Devuelve el récord histórico de la sesión en metros.
     *
     * @return mayor puntuación obtenida desde que se inició la aplicación
     */
    public int getRecord() { return record; }
}
