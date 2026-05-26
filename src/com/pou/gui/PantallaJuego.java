package com.pou.gui;

import com.pou.entities.Nube;
import com.pou.entities.Pou;

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

    private Pou         pou;
    private List<Nube>  nubes;
    private double      cameraY;
    private int         puntos;
    private int         record;
    private int         tiempo;

    /**
     * Carga el contexto de datos que serán usados en el próximo fotograma.
     * Debe llamarse antes de {@link #dibujar} cuando el juego está en estado {@code JUGANDO}
     * o {@code GAME_OVER}.
     *
     * @param pou     entidad del jugador
     * @param nubes   lista de nubes activas
     * @param cameraY desplazamiento de cámara en coordenadas del mundo
     * @param puntos  puntuación actual en metros
     * @param record  récord de la sesión en metros
     */
    public void cargar(Pou pou, List<Nube> nubes, double cameraY, int puntos, int record, int tiempo) {
        this.pou     = pou;
        this.nubes   = nubes;
        this.cameraY = cameraY;
        this.puntos  = puntos;
        this.record  = record;
        this.tiempo  = tiempo;
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
        for (Nube n : nubes) n.dibujar(g, cameraY);
        pou.dibujar(g, cameraY);
        dibujarHUD(g, ancho, puntos, record);
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
     * Dibuja el HUD en la esquina superior izquierda con la altura actual y el récord.
     *
     * @param g      contexto gráfico de Swing
     * @param ancho  ancho del panel en píxeles
     * @param puntos puntuación actual en metros
     * @param record récord de la sesión en metros
     */
    private void dibujarHUD(Graphics2D g, int ancho, int puntos, int record) {
        g.setColor(new Color(0, 0, 0, 110));
        g.fillRoundRect(8, 8, 155, 84, 14, 14);

        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.setColor(Color.WHITE);
        g.drawString("Altura:  " + puntos + " m", 18, 30);

        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(new Color(255, 215, 50));
        g.drawString("Record:  " + record + " m", 18, 52);

        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(new Color(160, 220, 255));
        g.drawString("Tiempo:  " + tiempo + " s", 18, 74);
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
