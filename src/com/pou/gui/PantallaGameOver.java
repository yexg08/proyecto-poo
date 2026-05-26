package com.pou.gui;

import java.awt.*;

public class PantallaGameOver {

    public void dibujar(Graphics2D g, int ancho, int alto, int puntos, int record) {
        // Dim background
        g.setColor(new Color(0, 0, 0, 170));
        g.fillRect(0, 0, ancho, alto);

        // Card
        int cx = ancho / 2 - 160, cy = alto / 2 - 155;
        g.setColor(new Color(250, 220, 170));
        g.fillRoundRect(cx, cy, 320, 310, 30, 30);
        g.setColor(new Color(160, 90, 30));
        g.setStroke(new BasicStroke(4f));
        g.drawRoundRect(cx, cy, 320, 310, 30, 30);
        g.setStroke(new BasicStroke(1f));

        // "GAME OVER" title
        g.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 42));
        FontMetrics fm = g.getFontMetrics();
        String titulo = "GAME OVER";
        g.setColor(new Color(190, 40, 40));
        g.drawString(titulo, (ancho - fm.stringWidth(titulo)) / 2, cy + 58);

        // Sad Pou face (simple geometric)
        int px = ancho / 2 - 22, py = cy + 75;
        g.setColor(new Color(180, 120, 60));
        g.fillRoundRect(px, py, 44, 50, 18, 18);
        g.setColor(Color.WHITE);
        g.fillOval(px + 7, py + 12, 12, 13);
        g.fillOval(px + 25, py + 12, 12, 13);
        g.setColor(Color.BLACK);
        g.fillOval(px + 10, py + 16, 6, 6);
        g.fillOval(px + 28, py + 16, 6, 6);
        g.setColor(new Color(100, 50, 10));
        g.setStroke(new BasicStroke(2f));
        g.drawArc(px + 11, py + 34, 22, 10, 0, 180); 
        g.setStroke(new BasicStroke(1f));

        // Score
        g.setFont(new Font("Arial", Font.BOLD, 22));
        fm = g.getFontMetrics();
        String score = "Altura alcanzada:  " + puntos + " m";
        g.setColor(Color.DARK_GRAY);
        g.drawString(score, (ancho - fm.stringWidth(score)) / 2, cy + 178);

        // Record
        g.setFont(new Font("Arial", Font.BOLD, 19));
        fm = g.getFontMetrics();
        String rec = "Mejor marca:  " + record + " m";
        g.setColor(new Color(190, 120, 0));
        g.drawString(rec, (ancho - fm.stringWidth(rec)) / 2, cy + 210);

        // Divider
        g.setColor(new Color(160, 90, 30, 100));
        g.fillRect(cx + 20, cy + 225, 280, 2);

        // ENTER hint
        g.setFont(new Font("Arial", Font.BOLD, 17));
        fm = g.getFontMetrics();
        String restart = "ENTER  —  Jugar de nuevo";
        g.setColor(new Color(40, 140, 40));
        g.drawString(restart, (ancho - fm.stringWidth(restart)) / 2, cy + 260);

        // ESC hint
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        fm = g.getFontMetrics();
        String menu = "ESC  —  Volver al menú";
        g.setColor(new Color(80, 80, 200));
        g.drawString(menu, (ancho - fm.stringWidth(menu)) / 2, cy + 288);
    }
}
