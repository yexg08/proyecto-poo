package com.pou.entities;

import com.pou.util.Recursos;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Entidad de poder (power-up) que Pou puede recoger durante la partida.
 * <p>
 * Existen dos tipos: {@link Tipo#VIDA_EXTRA} otorga una vida adicional y
 * {@link Tipo#SUPER_SALTO} activa temporalmente un impulso de salto mayor.
 * Una vez recogido el power-up se marca como tal y deja de dibujarse y colisionar.
 * </p>
 */
public class PowerUp {

    /**
     * Tipo de efecto que aplica el power-up al ser recogido.
     */
    public enum Tipo {
        /** Incrementa en uno las vidas restantes del jugador. */
        VIDA_EXTRA,
        /** Activa temporalmente una velocidad de salto superior. */
        SUPER_SALTO
    }

    /** Ancho del sprite/hitbox del power-up en píxeles. */
    public static final int ANCHO = 32;

    /** Alto del sprite/hitbox del power-up en píxeles. */
    public static final int ALTO  = 32;

    private final double x;
    private final double y;
    private final Tipo   tipo;
    private boolean      recogido;

    /**
     * Crea un power-up en la posición indicada con el tipo especificado.
     *
     * @param x    coordenada X del mundo
     * @param y    coordenada Y del mundo
     * @param tipo efecto que aplica al ser recogido
     */
    public PowerUp(double x, double y, Tipo tipo) {
        this.x    = x;
        this.y    = y;
        this.tipo = tipo;
    }

    /**
     * Dibuja el power-up aplicando el desplazamiento de cámara.
     * Usa el sprite PNG correspondiente si está cargado; en caso contrario
     * dibuja un rectángulo de color de respaldo.
     *
     * @param g       contexto gráfico de Swing
     * @param cameraY desplazamiento vertical de la cámara en coordenadas del mundo
     */
    public void dibujar(Graphics2D g, double cameraY) {
        if (recogido) return;
        int sx = (int) x;
        int sy = (int) (y - cameraY);

        BufferedImage img = tipo == Tipo.VIDA_EXTRA ? Recursos.POWERUP_VIDA : Recursos.POWERUP_SALTO;
        if (img != null) {
            g.drawImage(img, sx, sy, ANCHO, ALTO, null);
        } else {
            g.setColor(tipo == Tipo.VIDA_EXTRA ? new Color(60, 200, 60) : new Color(255, 200, 0));
            g.fillRoundRect(sx, sy, ANCHO, ALTO, 10, 10);
        }
    }

    /**
     * Marca el power-up como recogido. Después de llamar a este método
     * {@link #dibujar} no renderiza nada y el generador lo elimina de la lista.
     */
    public void recoger() { recogido = true; }

    /**
     * Indica si el power-up ya fue recogido por el jugador.
     *
     * @return {@code true} si ya fue recogido
     */
    public boolean isRecogido() { return recogido; }

    /**
     * Devuelve la coordenada X del mundo.
     *
     * @return posición horizontal del power-up
     */
    public double getX() { return x; }

    /**
     * Devuelve la coordenada Y del mundo.
     *
     * @return posición vertical del power-up
     */
    public double getY() { return y; }

    /**
     * Devuelve el tipo de efecto del power-up.
     *
     * @return {@link Tipo#VIDA_EXTRA} o {@link Tipo#SUPER_SALTO}
     */
    public Tipo getTipo() { return tipo; }

    /**
     * Devuelve el rectángulo de colisión del power-up en coordenadas del mundo.
     *
     * @return bounds del hitbox
     */
    public Rectangle getBounds() { return new Rectangle((int) x, (int) y, ANCHO, ALTO); }
}
