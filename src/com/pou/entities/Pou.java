package com.pou.entities;

import com.pou.util.Recursos;

import java.awt.*;

/**
 * Entidad del jugador controlada por el usuario.
 * <p>
 * Pou aplica gravedad constante en cada fotograma y responde a los comandos
 * de movimiento horizontal. Al rebotar en una nube su velocidad vertical se
 * reinicia al valor de salto. Si el sprite PNG está disponible en {@link Recursos}
 * se usa; de lo contrario se dibuja una representación geométrica con ojos
 * animados según la dirección de movimiento vertical.
 * </p>
 */
public class Pou {

    /** Ancho del sprite/hitbox de Pou en píxeles. */
    public static final int ANCHO = 42;

    /** Alto del sprite/hitbox de Pou en píxeles. */
    public static final int ALTO  = 50;

    private static final double GRAVEDAD            = 0.40;
    private static final double VEL_SALTO           = -14.5;
    private static final double VEL_SUPER_SALTO     = -22.0;
    private static final double VEL_MOVIMIENTO      = 5.5;
    private static final double VEL_MAX_CAIDA       = 16.0;
    private static final int    DURACION_SUPER_SALTO = 300; // 5 s a 60 fps

    private double  x, y;
    private double  velocidadX, velocidadY;
    private boolean moviendoIzquierda, moviendoDerecha;
    private boolean superSaltoActivo;
    private int     ticksSuperSalto;

    /**
     * Crea a Pou en la posición indicada con velocidad vertical de salto inicial.
     *
     * @param x coordenada X del mundo
     * @param y coordenada Y del mundo
     */
    public Pou(double x, double y) {
        this.x = x;
        this.y = y;
        this.velocidadY = VEL_SALTO;
    }

    /**
     * Actualiza la física de Pou en un fotograma: aplica gravedad, limita la
     * velocidad de caída, aplica el movimiento horizontal y envuelve la posición
     * horizontal al salir del panel.
     *
     * @param anchoPanel ancho del panel en píxeles; se usa para el wrapping horizontal
     */
    public void actualizar(int anchoPanel) {
        if (superSaltoActivo && --ticksSuperSalto <= 0) superSaltoActivo = false;

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

    /**
     * Aplica el impulso de salto reiniciando la velocidad vertical. Si el super salto
     * está activo usa {@code VEL_SUPER_SALTO}; en caso contrario usa {@code VEL_SALTO}.
     * Lo invoca {@link com.pou.logic.Colision} al detectar un aterrizaje.
     */
    public void saltar() {
        velocidadY = superSaltoActivo ? VEL_SUPER_SALTO : VEL_SALTO;
    }

    /**
     * Activa el super salto por {@value #DURACION_SUPER_SALTO} fotogramas.
     * Mientras está activo {@link #saltar()} aplica una velocidad vertical mayor.
     */
    public void activarSuperSalto() {
        superSaltoActivo = true;
        ticksSuperSalto  = DURACION_SUPER_SALTO;
    }

    /**
     * Indica si el power-up de super salto está activo en este momento.
     *
     * @return {@code true} si el super salto sigue vigente
     */
    public boolean isSuperSaltoActivo() { return superSaltoActivo; }

    /**
     * Dibuja a Pou en el contexto gráfico aplicando el desplazamiento de cámara.
     * Usa el sprite PNG si está cargado; en caso contrario dibuja formas geométricas
     * con ojos que cambian de expresión según la velocidad vertical.
     *
     * @param g2d     contexto gráfico de Swing
     * @param cameraY desplazamiento vertical de la cámara en coordenadas del mundo
     */
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

    /**
     * Devuelve el rectángulo de colisión de Pou en coordenadas del mundo.
     *
     * @return bounds del hitbox
     */
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, ANCHO, ALTO);
    }

    /**
     * Devuelve la coordenada X del mundo.
     *
     * @return posición horizontal de Pou
     */
    public double getX()          { return x; }

    /**
     * Devuelve la coordenada Y del mundo.
     *
     * @return posición vertical de Pou
     */
    public double getY()          { return y; }

    /**
     * Devuelve la velocidad vertical actual. Positiva = cayendo; negativa = subiendo.
     *
     * @return velocidad en píxeles por fotograma en el eje Y
     */
    public double getVelocidadY() { return velocidadY; }

    /**
     * Establece si Pou debe moverse hacia la izquierda en el próximo {@link #actualizar}.
     *
     * @param v {@code true} para activar el movimiento a la izquierda
     */
    public void setMoviendoIzquierda(boolean v) { moviendoIzquierda = v; }

    /**
     * Establece si Pou debe moverse hacia la derecha en el próximo {@link #actualizar}.
     *
     * @param v {@code true} para activar el movimiento a la derecha
     */
    public void setMoviendoDerecha(boolean v)   { moviendoDerecha   = v; }
}
