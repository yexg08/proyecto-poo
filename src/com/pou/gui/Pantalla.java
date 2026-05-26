package com.pou.gui;

import java.awt.Graphics2D;

/**
 * Contrato común para todas las pantallas del juego.
 * <p>
 * Cada estado del juego (menú, partida activa, game over) tiene su propia
 * implementación concreta. {@link com.pou.logic.GameLoop} mantiene una referencia
 * de tipo {@code Pantalla} y llama a {@link #dibujar} de forma <em>polimórfica</em>,
 * sin necesidad de conocer el tipo concreto en tiempo de compilación.
 * </p>
 *
 * @see PantallaMenu
 * @see PantallaJuego
 * @see PantallaGameOver
 */
public interface Pantalla {

    /**
     * Renderiza esta pantalla en el contexto gráfico indicado.
     *
     * @param g     contexto gráfico de Swing (con antialiasing ya configurado por el game loop)
     * @param ancho ancho del panel en píxeles
     * @param alto  alto del panel en píxeles
     */
    void dibujar(Graphics2D g, int ancho, int alto);
}
