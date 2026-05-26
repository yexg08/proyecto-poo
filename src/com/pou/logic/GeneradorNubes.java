package com.pou.logic;

import com.pou.entities.Nube;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneradorNubes {

    private static final int SEP_MIN = 65;
    private static final int SEP_MAX = 115;

    private final List<Nube> nubes;
    private final Random random;
    private final int anchoPanel;
    private double yGenerada; // world-Y of highest cloud generated so far

    public GeneradorNubes(int anchoPanel, int altoPanel) {
        this.anchoPanel = anchoPanel;
        this.nubes      = new ArrayList<>();
        this.random     = new Random();
        generarIniciales(altoPanel);
    }

    private void generarIniciales(int altoPanel) {
        // First cloud right below Pou's starting point
        double y = altoPanel - 110.0;
        nubes.add(new Nube(anchoPanel / 2.0 - Nube.ANCHO / 2.0, y, Nube.Tipo.NORMAL, anchoPanel));
        yGenerada = y;

        // Fill two screens upward so Pou always has clouds to land on
        while (yGenerada > -altoPanel * 2) {
            yGenerada -= SEP_MIN + random.nextInt(SEP_MAX - SEP_MIN);
            agregarAleatoria(yGenerada);
        }
    }

    public void actualizar(double cameraY, int altoPanel) {
        // Generate ahead of the camera (one full screen above)
        while (yGenerada > cameraY - altoPanel) {
            yGenerada -= SEP_MIN + random.nextInt(SEP_MAX - SEP_MIN);
            agregarAleatoria(yGenerada);
        }

        // Update moving clouds
        for (Nube n : nubes) n.actualizar();

        // Remove clouds that are too far below the camera or already broken
        nubes.removeIf(n -> n.getY() > cameraY + altoPanel + 120 || n.estaRota());
    }

    private void agregarAleatoria(double y) {
        double x   = random.nextInt(anchoPanel - Nube.ANCHO - 10) + 5;
        Nube.Tipo t = tipoAleatorio();
        nubes.add(new Nube(x, y, t, anchoPanel));
    }

    private Nube.Tipo tipoAleatorio() {
        int r = random.nextInt(10);
        if (r < 6) return Nube.Tipo.NORMAL;
        if (r < 8) return Nube.Tipo.MOVIL;
        return Nube.Tipo.FRAGIL;
    }

    public List<Nube> getNubes() { return nubes; }
}
