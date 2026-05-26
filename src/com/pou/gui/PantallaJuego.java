package com.pou.gui;

import com.pou.entities.Nube;
import com.pou.entities.Pou;
import com.pou.entities.PowerUp;
import com.pou.util.Recursos;

import java.awt.*;
import java.util.List;
import java.util.Random;

/**
 * Vista de la pantalla de juego activo. Implementa {@link Pantalla}.
 * <p>
 * Antes de cada llamada a {@link #dibujar} el game loop debe invocar
 * {@link #cargar} para actualizar el contexto de datos del fotograma.
 * Compone el fotograma completo dibujando en orden: fondo de cielo dinámico
 * (cambia de color y agrega estrellas a gran altitud), nubes, el personaje Pou
 * y el HUD con la puntuación y el récord.
 * </p>
 */
public class PantallaJuego implements Pantalla {

    private Pou          pou;
    private List<Nube>   nubes;
    private List<PowerUp> powerUps;
    private double       cameraY;
    private int          puntos;
    private int          record;
    private int          tiempo;
    private int          vidas;
    private boolean      superSaltoActivo;

    /**
     * Carga el contexto de datos que serán usados en el próximo fotograma.
     * Debe llamarse antes de {@link #dibujar} cuando el juego está en estado {@code JUGANDO}
     * o {@code GAME_OVER}.
     *
     * @param pou             entidad del jugador
     * @param nubes           lista de nubes activas
     * @param powerUps        lista de power-ups activos en el mundo
     * @param cameraY         desplazamiento de cámara en coordenadas del mundo
     * @param puntos          puntuación actual en metros
     * @param record          récord de la sesión en metros
     * @param tiempo          segundos de la partida actual
     * @param vidas           número de vidas restantes del jugador
     * @param superSaltoActivo {@code true} si el power-up de super salto está vigente
     */
    public void cargar(Pou pou, List<Nube> nubes, List<PowerUp> powerUps,
                       double cameraY, int puntos, int record, int tiempo,
                       int vidas, boolean superSaltoActivo) {
        this.pou             = pou;
        this.nubes           = nubes;
        this.powerUps        = powerUps;
        this.cameraY         = cameraY;
        this.puntos          = puntos;
        this.record          = record;
        this.tiempo          = tiempo;
        this.vidas           = vidas;
        this.superSaltoActivo = superSaltoActivo;
    }

    /**
     * Renderiza el fotograma de juego completo usando los datos cargados con {@link #cargar}.
     *
     * @param g     contexto gráfico de Swing
     * @param ancho ancho del panel en píxeles
     * @param alto  alto del panel en píxeles
     */
    @Override
    public void dibujar(Graphics2D g, int ancho, int alto) {
        dibujarFondo(g, ancho, alto, cameraY);
        for (Nube n : nubes)     n.dibujar(g, cameraY);
        for (PowerUp p : powerUps) p.dibujar(g, cameraY);
        pou.dibujar(g, cameraY);
        dibujarHUD(g, ancho, puntos, record, vidas, superSaltoActivo);
    }

    /**
     * Dibuja el fondo de cielo dinámico que transiciona de azul de día a azul profundo
     * y luego a casi negro conforme la cámara sube. A partir de cierta altitud agrega
     * un campo de estrellas determinista basado en la banda de cámara.
     *
     * @param g       contexto gráfico de Swing
     * @param ancho   ancho del panel en píxeles
     * @param alto    alto del panel en píxeles
     * @param cameraY posición de la cámara en el mundo; negativa al subir
     */
    private void dibujarFondo(Graphics2D g, int ancho, int alto, double cameraY) {
        // Sky shifts from day-blue → deep-blue → near-space as altitude grows
        double altura = -cameraY / 100.0;

        Color top, bot;
        if (altura < 40) {
            top = new Color(80,  170, 255);
            bot = new Color(180, 220, 255);
        } else if (altura < 120) {
            float t = (float)((altura - 40) / 80.0);
            top = blend(new Color(80, 170, 255), new Color(20, 60, 160), t);
            bot = blend(new Color(180, 220, 255), new Color(60, 110, 210), t);
        } else {
            top = new Color(10,  20,  80);
            bot = new Color(30,  60, 150);
        }

        g.setPaint(new GradientPaint(0, 0, top, 0, alto, bot));
        g.fillRect(0, 0, ancho, alto);

        // Stars appear at high altitude
        if (altura > 80) {
            float alpha = (float) Math.min(1.0, (altura - 80) / 60.0);
            g.setColor(new Color(1f, 1f, 1f, alpha * 0.7f));
            // Deterministic star field based on camera band
            long band = (long)(cameraY / 300);
            Random rng = new Random(band * 1337L);
            for (int i = 0; i < 40; i++) {
                int sx = rng.nextInt(ancho);
                int sy = rng.nextInt(alto);
                int r  = rng.nextInt(2) + 1;
                g.fillOval(sx, sy, r, r);
            }
        }
    }

    /**
     * Dibuja el HUD en la esquina superior izquierda con la altura, el récord, el
     * tiempo y (si está activo) el indicador de super salto; y los iconos de vidas
     * restantes en la esquina superior derecha.
     *
     * @param g               contexto gráfico de Swing
     * @param ancho           ancho del panel en píxeles
     * @param puntos          puntuación actual en metros
     * @param record          récord de la sesión en metros
     * @param vidas           número de vidas restantes del jugador
     * @param superSaltoActivo {@code true} si el indicador de super salto debe mostrarse
     */
    private void dibujarHUD(Graphics2D g, int ancho, int puntos, int record, int vidas, boolean superSaltoActivo) {
        int hudAlto = superSaltoActivo ? 102 : 84;
        g.setColor(new Color(0, 0, 0, 110));
        g.fillRoundRect(8, 8, 155, hudAlto, 14, 14);

        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.setColor(Color.WHITE);
        g.drawString("Altura:  " + puntos + " m", 18, 30);

        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(new Color(255, 215, 50));
        g.drawString("Record:  " + record + " m", 18, 52);

        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(new Color(160, 220, 255));
        g.drawString("Tiempo:  " + tiempo + " s", 18, 74);

        if (superSaltoActivo) {
            g.setFont(new Font("Arial", Font.BOLD, 13));
            g.setColor(new Color(255, 230, 0));
            g.drawString("* SUPER SALTO", 18, 96);
        }

        // Life icons — top-right corner
        if (vidas <= 0) return;
        int icoW = 22, icoH = 26, gap = 6, margen = 8;
        int totalW = vidas * icoW + (vidas - 1) * gap;
        int ix = ancho - margen - totalW;
        int iy = margen;

        g.setColor(new Color(0, 0, 0, 110));
        g.fillRoundRect(ix - 6, iy, totalW + 12, icoH + 8, 14, 14);

        for (int i = 0; i < vidas; i++) {
            int lx = ix + i * (icoW + gap);
            int ly = iy + 4;
            if (Recursos.POU != null) {
                g.drawImage(Recursos.POU, lx, ly, icoW, icoH, null);
            } else {
                dibujarMiniPou(g, lx, ly, icoW, icoH);
            }
        }
    }

    /**
     * Dibuja una representación geométrica reducida de Pou usada como icono de vida.
     *
     * @param g contexto gráfico de Swing
     * @param x coordenada X de la esquina superior izquierda del icono
     * @param y coordenada Y de la esquina superior izquierda del icono
     * @param w ancho del icono en píxeles
     * @param h alto del icono en píxeles
     */
    private void dibujarMiniPou(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(new Color(180, 120, 60));
        g.fillRoundRect(x, y, w, h, 8, 8);
        g.setColor(Color.WHITE);
        g.fillOval(x + 3, y + 5, 5, 6);
        g.fillOval(x + 13, y + 5, 5, 6);
        g.setColor(new Color(30, 20, 10));
        g.fillOval(x + 4, y + 7, 3, 3);
        g.fillOval(x + 14, y + 7, 3, 3);
        g.setColor(new Color(100, 50, 10));
        g.setStroke(new BasicStroke(1.5f));
        g.drawArc(x + 6, y + 16, 10, 5, 0, 180);
        g.setStroke(new BasicStroke(1f));
    }

    /**
     * Interpola linealmente entre dos colores.
     *
     * @param a color origen
     * @param b color destino
     * @param t factor de interpolación en [0.0, 1.0]
     * @return color interpolado
     */
    private static Color blend(Color a, Color b, float t) {
        int r = (int)(a.getRed()   + t * (b.getRed()   - a.getRed()));
        int gr= (int)(a.getGreen() + t * (b.getGreen() - a.getGreen()));
        int bl= (int)(a.getBlue()  + t * (b.getBlue()  - a.getBlue()));
        return new Color(r, gr, bl);
    }
}
