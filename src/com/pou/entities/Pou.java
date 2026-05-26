package com.pou.entities;

import com.pou.util.Recursos;

import java.awt.*;

public class Pou {
    public static final int ANCHO = 42;
    public static final int ALTO  = 50;

    private static final double GRAVEDAD       = 0.40;
    private static final double VEL_SALTO      = -14.5;
    private static final double VEL_MOVIMIENTO = 5.5;
    private static final double VEL_MAX_CAIDA  = 16.0;

    private double x, y;
    private double velocidadX, velocidadY;
    private boolean moviendoIzquierda, moviendoDerecha;

    public Pou(double x, double y) {
        this.x = x;
        this.y = y;
        this.velocidadY = VEL_SALTO;
    }

    public void actualizar(int anchoPanel) {
        velocidadY += GRAVEDAD;
        if (velocidadY > VEL_MAX_CAIDA) velocidadY = VEL_MAX_CAIDA;

        velocidadX = 0;
        if (moviendoIzquierda)  velocidadX = -VEL_MOVIMIENTO;
        if (moviendoDerecha)    velocidadX =  VEL_MOVIMIENTO;

        x += velocidadX;
        y += velocidadY;

        // Wrap horizontally
        if (x + ANCHO < 0)   x = anchoPanel;
        if (x > anchoPanel)  x = -ANCHO;
    }

    public void saltar() {
        velocidadY = VEL_SALTO;
    }

    public void dibujar(Graphics2D g2d, double cameraY) {
        int sx = (int) x;
        int sy = (int) (y - cameraY);

        if (Recursos.POU != null) {
            g2d.drawImage(Recursos.POU, sx, sy, ANCHO, ALTO, null);
            return;
        }

        // Fallback: dibujo geométrico
        g2d.setColor(new Color(180, 120, 60));
        g2d.fillRoundRect(sx, sy, ANCHO, ALTO, 20, 20);

        g2d.setColor(new Color(210, 160, 100));
        g2d.fillOval(sx + 8, sy + 18, ANCHO - 16, 18);

        g2d.setColor(Color.WHITE);
        g2d.fillOval(sx + 7,  sy + 10, 12, 14);
        g2d.fillOval(sx + 23, sy + 10, 12, 14);

        int pupilOffset = velocidadY < 0 ? -2 : 2;
        g2d.setColor(new Color(30, 20, 10));
        g2d.fillOval(sx + 10, sy + 13 + pupilOffset, 6, 7);
        g2d.fillOval(sx + 26, sy + 13 + pupilOffset, 6, 7);

        g2d.setColor(new Color(100, 50, 10));
        g2d.setStroke(new BasicStroke(2f));
        if (velocidadY < 0) {
            g2d.drawArc(sx + 12, sy + 29, 18, 9, 0, -180);
        } else {
            g2d.drawArc(sx + 12, sy + 33, 18, 9, 0,  180);
        }
        g2d.setStroke(new BasicStroke(1f));

        g2d.setColor(new Color(140, 80, 30));
        g2d.fillRoundRect(sx + 4,  sy + ALTO - 6, 13, 10, 6, 6);
        g2d.fillRoundRect(sx + ANCHO - 17, sy + ALTO - 6, 13, 10, 6, 6);
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, ANCHO, ALTO);
    }

    public double getX()          { return x; }
    public double getY()          { return y; }
    public double getVelocidadY() { return velocidadY; }

    public void setMoviendoIzquierda(boolean v) { moviendoIzquierda = v; }
    public void setMoviendoDerecha(boolean v)   { moviendoDerecha   = v; }
}
