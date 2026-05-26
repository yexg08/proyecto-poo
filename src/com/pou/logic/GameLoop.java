package com.pou.logic;

import com.pou.entities.Pou;
import com.pou.gui.PantallaGameOver;
import com.pou.gui.PantallaJuego;
import com.pou.gui.PantallaMenu;
import com.pou.util.GestorSonido;

import javax.swing.*;
import java.awt.*;

public class GameLoop extends JPanel implements Runnable {

    public static final int ANCHO = 400;
    public static final int ALTO  = 620;

    private static final int FPS = 60;

    // Camera threshold: Pou is kept above this fraction from the top
    private static final double CAM_THRESHOLD = 0.42;

    private Thread hilo;
    private volatile GameState estado;

    private Pou             pou;
    private GeneradorNubes  generadorNubes;
    private ControlJugador  control;
    private Puntaje         puntaje;

    private double cameraY;
    private double cameraYMin; // lowest Y value the camera has reached (= highest position)
    private double yInicialPou;

    private final PantallaMenu     pantallaMenu     = new PantallaMenu();
    private final PantallaJuego    pantallaJuego    = new PantallaJuego();
    private final PantallaGameOver pantallaGameOver = new PantallaGameOver();

    public GameLoop() {
        setPreferredSize(new Dimension(ANCHO, ALTO));
        setBackground(Color.BLACK);
        setFocusable(true);
        inicializar();
    }

    // ------------------------------------------------------------------
    //  Init / reset
    // ------------------------------------------------------------------

    private void inicializar() {
        estado = GameState.MENU;
        iniciarPartida();
        control = new ControlJugador(pou);
        addKeyListener(control);
    }

    private void iniciarPartida() {
        yInicialPou    = ALTO - 150.0;
        pou            = new Pou(ANCHO / 2.0 - Pou.ANCHO / 2.0, yInicialPou);
        generadorNubes = new GeneradorNubes(ANCHO, ALTO);
        cameraY        = 0.0;
        cameraYMin     = 0.0;

        if (puntaje == null) {
            puntaje = new Puntaje(yInicialPou);
        } else {
            puntaje.reiniciar(yInicialPou);
        }

        if (control != null) control.setPou(pou);
    }

    // ------------------------------------------------------------------
    //  Game loop
    // ------------------------------------------------------------------

    public void iniciarHilo() {
        hilo = new Thread(this);
        hilo.setDaemon(true);
        hilo.start();
    }

    @Override
    public void run() {
        final double ns = 1_000_000_000.0 / FPS;
        long anterior   = System.nanoTime();
        double delta    = 0.0;

        while (true) {
            long actual = System.nanoTime();
            delta += (actual - anterior) / ns;
            anterior = actual;

            if (delta >= 1.0) {
                actualizar();
                repaint();
                delta -= 1.0;
            }

            // Yield to avoid 100 % CPU on fast machines
            try { Thread.sleep(1); } catch (InterruptedException ignored) {}
        }
    }

    // ------------------------------------------------------------------
    //  State machine
    // ------------------------------------------------------------------

    private void actualizar() {
        switch (estado) {
            case MENU:
                if (control.consumirEnter()) {
                    estado = GameState.JUGANDO;
                    GestorSonido.getInstancia().iniciarMusicaFondo();
                }
                break;

            case JUGANDO:
                actualizarJuego();
                break;

            case GAME_OVER:
                if (control.consumirEnter()) {
                    iniciarPartida();
                    estado = GameState.JUGANDO;
                    GestorSonido.getInstancia().iniciarMusicaFondo();
                }
                if (control.consumirEsc()) {
                    iniciarPartida();
                    estado = GameState.MENU;
                }
                break;
        }
    }

    private void actualizarJuego() {
        pou.actualizar(ANCHO);

        // Camera follows Pou upward only (never drops)
        double target = pou.getY() - ALTO * CAM_THRESHOLD;
        if (target < cameraYMin) {
            cameraYMin = target;
            cameraY    = target;
        }

        generadorNubes.actualizar(cameraY, ALTO);
        Colision.verificar(pou, generadorNubes.getNubes());
        puntaje.actualizar(pou.getY());

        // Game over when Pou falls below the bottom of the visible area
        if (pou.getY() > cameraY + ALTO + 60) {
            estado = GameState.GAME_OVER;
            GestorSonido.getInstancia().detenerMusicaFondo();
            GestorSonido.getInstancia().reproducirGameOver();
        }
    }

    // ------------------------------------------------------------------
    //  Rendering
    // ------------------------------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,        RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,   RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,           RenderingHints.VALUE_RENDER_QUALITY);

        switch (estado) {
            case MENU:
                pantallaMenu.dibujar(g2d, ANCHO, ALTO);
                break;

            case JUGANDO:
                pantallaJuego.dibujar(g2d, ANCHO, ALTO, pou,
                        generadorNubes.getNubes(), cameraY,
                        puntaje.getPuntos(), puntaje.getRecord());
                break;

            case GAME_OVER:
                pantallaJuego.dibujar(g2d, ANCHO, ALTO, pou,
                        generadorNubes.getNubes(), cameraY,
                        puntaje.getPuntos(), puntaje.getRecord());
                pantallaGameOver.dibujar(g2d, ANCHO, ALTO,
                        puntaje.getPuntos(), puntaje.getRecord());
                break;
        }
    }
}
