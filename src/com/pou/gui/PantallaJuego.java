package com.pou.gui;

import com.pou.entities.Nube;
import com.pou.entities.Pou;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class PantallaJuego {

    public void dibujar(Graphics2D g, int ancho, int alto,
                        Pou pou, List<Nube> nubes,
                        double cameraY, int puntos, int record) {

        dibujarFondo(g, ancho, alto, cameraY);

        for (Nube n : nubes) n.dibujar(g, cameraY);

        pou.dibujar(g, cameraY);

        dibujarHUD(g, ancho, puntos, record);
    }

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

    private void dibujarHUD(Graphics2D g, int ancho, int puntos, int record) {
        // Score panel
        g.setColor(new Color(0, 0, 0, 110));
        g.fillRoundRect(8, 8, 155, 62, 14, 14);

        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.setColor(Color.WHITE);
        g.drawString("Altura:  " + puntos + " m", 18, 30);

        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(new Color(255, 215, 50));
        g.drawString("Record:  " + record + " m", 18, 55);
    }

    private static Color blend(Color a, Color b, float t) {
        int r = (int)(a.getRed()   + t * (b.getRed()   - a.getRed()));
        int gr= (int)(a.getGreen() + t * (b.getGreen() - a.getGreen()));
        int bl= (int)(a.getBlue()  + t * (b.getBlue()  - a.getBlue()));
        return new Color(r, gr, bl);
    }
}
