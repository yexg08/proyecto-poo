package com.pou.entities;

import com.pou.util.Recursos;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Nube {

    public enum Tipo { NORMAL, MOVIL, FRAGIL }

    public static final int ANCHO = 85;
    public static final int ALTO  = 16;

    private double x, y;
    private final Tipo tipo;
    private double velocidadX;
    private int limiteIzq, limiteDer;
    private boolean rota;

    // Colors per type
    private static final Color[] COLORES_BASE = {
        new Color(210, 240, 210),   // NORMAL  – green-white
        new Color(180, 220, 255),   // MOVIL   – sky blue
        new Color(255, 185, 185)    // FRAGIL  – rose
    };

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

    public void actualizar() {
        if (tipo == Tipo.MOVIL) {
            x += velocidadX;
            if (x <= limiteIzq || x >= limiteDer) velocidadX = -velocidadX;
        }
    }

    public void romper() {
        if (tipo == Tipo.FRAGIL) rota = true;
    }

    private static final int SPR_ALTO = 38;

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

    public boolean estaRota() { return rota; }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, ANCHO, ALTO);
    }

    public double getX()    { return x; }
    public double getY()    { return y; }
    public Tipo   getTipo() { return tipo; }
}
