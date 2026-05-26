package com.pou.logic;

import com.pou.entities.Nube;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Genera y administra el conjunto de nubes activas en el mundo del juego.
 * <p>
 * Crea una columna inicial de nubes al arrancar la partida y, en cada fotograma,
 * produce nuevas nubes por encima de la cámara y elimina las que quedan muy
 * por debajo o ya fueron rotas.
 * </p>
 */
public class GeneradorNubes {

    /** Separación mínima (en píxeles del mundo) entre nubes consecutivas. */
    private static final int SEP_MIN = 65;

    /** Separación máxima (en píxeles del mundo) entre nubes consecutivas. */
    private static final int SEP_MAX = 115;

    private final List<Nube> nubes;
    private final Random random;
    private final int anchoPanel;

    /** Coordenada Y del mundo de la nube más alta generada hasta el momento. */
    private double yGenerada;

    /**
     * Construye el generador y llena la pantalla inicial con nubes.
     *
     * @param anchoPanel ancho del panel en píxeles, usado para distribuir las nubes horizontalmente
     * @param altoPanel  alto del panel en píxeles, usado para calcular la posición inicial
     */
    public GeneradorNubes(int anchoPanel, int altoPanel) {
        this.anchoPanel = anchoPanel;
        this.nubes      = new ArrayList<>();
        this.random     = new Random();
        generarIniciales(altoPanel);
    }

    /**
     * Genera la nube inicial justo debajo de Pou y llena dos pantallas hacia arriba
     * para que el jugador siempre tenga plataformas disponibles desde el inicio.
     *
     * @param altoPanel alto del panel en píxeles
     */
    private void generarIniciales(int altoPanel) {
        // Primera nube justo debajo del punto de inicio de Pou
        double y = altoPanel - 110.0;
        nubes.add(new Nube(anchoPanel / 2.0 - Nube.ANCHO / 2.0, y, Nube.Tipo.NORMAL, anchoPanel));
        yGenerada = y;

        // Rellenar dos pantallas hacia arriba
        while (yGenerada > -altoPanel * 2) {
            yGenerada -= SEP_MIN + random.nextInt(SEP_MAX - SEP_MIN);
            agregarAleatoria(yGenerada);
        }
    }

    /**
     * Actualiza el pool de nubes en cada fotograma: genera nuevas nubes por encima de la
     * cámara si es necesario, actualiza el movimiento de las nubes móviles y elimina las
     * que quedaron muy por debajo de la cámara o fueron rotas.
     *
     * @param cameraY coordenada Y del mundo que corresponde al borde superior de la pantalla
     * @param altoPanel alto del panel en píxeles
     */
    public void actualizar(double cameraY, int altoPanel) {
        // Generar una pantalla completa por encima de la cámara
        while (yGenerada > cameraY - altoPanel) {
            yGenerada -= SEP_MIN + random.nextInt(SEP_MAX - SEP_MIN);
            agregarAleatoria(yGenerada);
        }

        // Actualizar nubes móviles
        for (Nube n : nubes) n.actualizar();

        // Eliminar nubes que cayeron muy por debajo o ya se rompieron
        nubes.removeIf(n -> n.getY() > cameraY + altoPanel + 120 || n.estaRota());
    }

    /**
     * Crea una nube de tipo aleatorio en la coordenada Y indicada y en una
     * posición X aleatoria dentro del panel.
     *
     * @param y coordenada Y del mundo donde se crea la nube
     */
    private void agregarAleatoria(double y) {
        double x   = random.nextInt(anchoPanel - Nube.ANCHO - 10) + 5;
        Nube.Tipo t = tipoAleatorio();
        nubes.add(new Nube(x, y, t, anchoPanel));
    }

    /**
     * Selecciona un tipo de nube aleatoriamente con las siguientes probabilidades:
     * 60 % Normal, 20 % Móvil, 20 % Frágil.
     *
     * @return tipo de nube seleccionado
     */
    private Nube.Tipo tipoAleatorio() {
        int r = random.nextInt(10);
        if (r < 6) return Nube.Tipo.NORMAL;
        if (r < 8) return Nube.Tipo.MOVIL;
        return Nube.Tipo.FRAGIL;
    }

    /**
     * Devuelve la lista viva de nubes activas.
     *
     * @return lista de nubes; no debe modificarse externamente
     */
    public List<Nube> getNubes() { return nubes; }
}
