package com.pou.entities;

import com.pou.util.Recursos;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Entidad que representa una plataforma nube en el mundo del juego.
 * <p>
 * Existen tres tipos de nube definidos por el enum interno {@link Tipo}:
 * las nubes normales son estáticas, las móviles se desplazan horizontalmente
 * rebotando en los bordes del panel, y las frágiles desaparecen al ser pisadas.
 * Cada tipo tiene un sprite PNG asociado; si el sprite no está disponible se
 * dibuja una forma geométrica de colores diferenciados.
 * </p>
 */
public class Nube {

    /**
     * Tipos de nube disponibles en el juego, con comportamientos distintos.
     */
    public enum Tipo {
        /** Plataforma estática; siempre permanece en su posición original. */
        NORMAL,
        /** Plataforma móvil; se desplaza horizontalmente rebotando en los bordes. */
        MOVIL,
        /** Plataforma frágil; se rompe y desaparece al ser pisada por Pou. */
        FRAGIL
    }

    /** Ancho de la nube en píxeles (hitbox y sprite). */
    public static final int ANCHO = 85;

    /** Alto del hitbox de colisión de la nube en píxeles. */
    public static final int ALTO  = 16;

    private double x, y;
    private final Tipo tipo;
    private double velocidadX;
    private int limiteIzq, limiteDer;
    private boolean rota;

    private static final Color[] COLORES_BASE = {
        new Color(210, 240, 210),   // NORMAL  – verde-blanco
        new Color(180, 220, 255),   // MOVIL   – azul cielo
        new Color(255, 185, 185)    // FRAGIL  – rosa
    };

    /**
     * Construye una nube del tipo indicado en la posición especificada.
     * Las nubes móviles reciben velocidad y límites aleatorios al crearse.
     *
     * @param x          coordenada X del mundo
     * @param y          coordenada Y del mundo
     * @param tipo       tipo de nube ({@link Tipo#NORMAL}, {@link Tipo#MOVIL} o {@link Tipo#FRAGIL})
     * @param anchoPanel ancho del panel en píxeles, usado para calcular los límites de rebote
     */
    public Nube(double x, double y, Tipo tipo, int anchoPanel) {
        this.x     = x;
        this.y     = y;
        this.tipo  = tipo;
        this.rota  = false;

        if (tipo == Tipo.MOVIL) {
            Random rng = new Random();
            velocidadX = (rng.nextBoolean() ? 1 : -1) * (1.8 + rng.nextDouble() * 2.0);
            limiteIzq  = 15;
            limiteDer  = anchoPanel - ANCHO - 15;
        }
    }

    /**
     * Actualiza la posición de la nube en un fotograma. Solo tiene efecto en
     * nubes de tipo {@link Tipo#MOVIL}; invierte la velocidad al alcanzar los límites.
     */
    public void actualizar() {
        if (tipo == Tipo.MOVIL) {
            x += velocidadX;
            if (x <= limiteIzq || x >= limiteDer) velocidadX = -velocidadX;
        }
    }

    /**
     * Marca la nube como rota para que sea eliminada del pool en el siguiente
     * fotograma. Solo tiene efecto en nubes de tipo {@link Tipo#FRAGIL}.
     */
    public void romper() {
        if (tipo == Tipo.FRAGIL) rota = true;
    }

    /** Alto del sprite de nube en píxeles (mayor que el hitbox para aspecto visual). */
    private static final int SPR_ALTO = 38;

    /**
     * Dibuja la nube en el contexto gráfico aplicando el desplazamiento de cámara.
     * Usa el sprite PNG correspondiente al tipo si está disponible y la nube no está rota;
     * en caso contrario dibuja una forma geométrica de color diferenciado por tipo.
     *
     * @param g2d     contexto gráfico de Swing
     * @param cameraY desplazamiento vertical de la cámara en coordenadas del mundo
     */
    public void dibujar(Graphics2D g2d, double cameraY) {
        int sx = (int) x;
        int sy = (int) (y - cameraY);

        BufferedImage img = null;
        switch (tipo) {
            case NORMAL: img = Recursos.NUBE_NORMAL; break;
            case MOVIL:  img = Recursos.NUBE_MOVIL;  break;
            case FRAGIL: img = Recursos.NUBE_FRAGIL; break;
        }

        if (img != null && !rota) {
            g2d.drawImage(img, sx, sy - (SPR_ALTO - ALTO), ANCHO, SPR_ALTO, null);
            return;
        }

        // Fallback: dibujo geométrico (también se usa para la nube rota)
        Color base = COLORES_BASE[tipo.ordinal()];
        if (rota) base = new Color(200, 80, 80, 160);

        g2d.setColor(base);
        g2d.fillOval(sx + 6,  sy - 11, 28, 24);
        g2d.fillOval(sx + 28, sy - 16, 34, 26);
        g2d.fillOval(sx + 54, sy - 9,  24, 22);
        g2d.fillRoundRect(sx, sy, ANCHO, ALTO, 12, 12);

        g2d.setColor(base.darker());
        g2d.setStroke(new BasicStroke(1.2f));
        g2d.drawRoundRect(sx, sy, ANCHO, ALTO, 12, 12);
        g2d.setStroke(new BasicStroke(1f));
    }

    /**
     * Indica si la nube fue rota y debe eliminarse del pool.
     *
     * @return {@code true} si la nube ya fue pisada y destruida
     */
    public boolean estaRota() { return rota; }

    /**
     * Devuelve el rectángulo de colisión en coordenadas del mundo.
     *
     * @return bounds del hitbox
     */
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, ANCHO, ALTO);
    }

    /**
     * Devuelve la coordenada X del mundo.
     *
     * @return posición horizontal de la nube
     */
    public double getX()    { return x; }

    /**
     * Devuelve la coordenada Y del mundo.
     *
     * @return posición vertical de la nube
     */
    public double getY()    { return y; }

    /**
     * Devuelve el tipo de nube.
     *
     * @return {@link Tipo} de esta nube
     */
    public Tipo   getTipo() { return tipo; }
}
