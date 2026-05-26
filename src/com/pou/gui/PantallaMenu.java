package com.pou.gui;

import java.awt.*;

/**
 * Vista de la pantalla de menú principal.
 * <p>
 * Dibuja el fondo degradado, el título del juego, las instrucciones de controles,
 * una leyenda de los tipos de nubes y el aviso para comenzar. Las nubes decorativas
 * del fondo se dibujan con formas geométricas semi-transparentes.
 * </p>
 */
public class PantallaMenu implements Pantalla {

    /**
     * Renderiza la pantalla completa del menú principal en el contexto gráfico dado.
     *
     * @param g    contexto gráfico de Swing
     * @param ancho ancho del panel en píxeles
     * @param alto  alto del panel en píxeles
     */
    public void dibujar(Graphics2D g, int ancho, int alto) {
        // Sky gradient
        GradientPaint cielo = new GradientPaint(0, 0, new Color(100, 180, 255), 0, alto, new Color(200, 235, 255));
        g.setPaint(cielo);
        g.fillRect(0, 0, ancho, alto);

        // Decorative clouds (background)
        dibujarNubeDecorativa(g, 30, 80,  90);
        dibujarNubeDecorativa(g, 260, 140, 70);
        dibujarNubeDecorativa(g, 80, 220, 60);
        dibujarNubeDecorativa(g, 300, 310, 80);

        // Title shadow
        g.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 58));
        FontMetrics fm = g.getFontMetrics();
        String titulo = "POU JUMP!";
        int tx = (ancho - fm.stringWidth(titulo)) / 2;
        g.setColor(new Color(0, 0, 0, 60));
        g.drawString(titulo, tx + 3, alto / 4 + 3);

        // Title
        g.setColor(new Color(60, 130, 60));
        g.drawString(titulo, tx, alto / 4);

        // Subtitle
        g.setFont(new Font("Arial", Font.ITALIC, 17));
        fm = g.getFontMetrics();
        String sub = "Salta de nube en nube hasta el infinito";
        g.setColor(new Color(50, 80, 150));
        g.drawString(sub, (ancho - fm.stringWidth(sub)) / 2, alto / 4 + 42);

        // Controls box
        int bx = ancho / 2 - 140, by = alto / 2 - 60;
        g.setColor(new Color(255, 255, 255, 160));
        g.fillRoundRect(bx, by, 280, 150, 20, 20);
        g.setColor(new Color(100, 150, 100));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(bx, by, 280, 150, 20, 20);
        g.setStroke(new BasicStroke(1f));

        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("CONTROLES:", bx + 15, by + 25);

        g.setFont(new Font("Arial", Font.PLAIN, 14));
        String[] lineas = {
            "  ← → / A D  —  Mover a los lados",
            "  Aterriza en las nubes para saltar"
        };
        int ly = by + 48;
        for (String l : lineas) { g.drawString(l, bx + 5, ly); ly += 22; }

        g.setFont(new Font("Arial", Font.BOLD, 13));
        g.drawString("TIPOS DE NUBES:", bx + 15, ly + 8);
        ly += 26;
        g.setColor(new Color(80, 160, 80));
        g.fillRoundRect(bx + 15, ly - 10, 14, 14, 4, 4);
        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 13));
        g.drawString("Normal    ", bx + 35, ly);

        g.setColor(new Color(80, 160, 255));
        g.fillRoundRect(bx + 115, ly - 10, 14, 14, 4, 4);
        g.setColor(Color.DARK_GRAY);
        g.drawString("Se mueve", bx + 135, ly);

        ly += 20;
        g.setColor(new Color(255, 120, 120));
        g.fillRoundRect(bx + 15, ly - 10, 14, 14, 4, 4);
        g.setColor(Color.DARK_GRAY);
        g.drawString("Se rompe al pisar", bx + 35, ly);

        // ENTER prompt
        g.setFont(new Font("Arial", Font.BOLD, 22));
        fm = g.getFontMetrics();
        String enter = "Presiona  ENTER  para jugar";
        g.setColor(new Color(220, 140, 0));
        g.drawString(enter, (ancho - fm.stringWidth(enter)) / 2, alto - 55);
    }

    /**
     * Dibuja una nube decorativa geométrica semi-transparente en la posición indicada.
     *
     * @param g el contexto gráfico
     * @param x coordenada X del borde izquierdo de la nube
     * @param y coordenada Y de la base de la nube
     * @param w ancho aproximado de la nube en píxeles
     */
    private void dibujarNubeDecorativa(Graphics2D g, int x, int y, int w) {
        g.setColor(new Color(255, 255, 255, 180));
        g.fillOval(x,      y - 10, (int)(w * 0.45), (int)(w * 0.4));
        g.fillOval(x + w/4, y - 16, (int)(w * 0.5),  (int)(w * 0.45));
        g.fillOval(x + w/2, y - 8,  (int)(w * 0.42), (int)(w * 0.38));
        g.fillRoundRect(x, y, w, (int)(w * 0.28), 10, 10);
    }
}
