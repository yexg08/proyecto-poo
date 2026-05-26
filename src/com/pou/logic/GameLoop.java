package com.pou.logic;

import com.pou.entities.Pou;
import com.pou.gui.Pantalla;
import com.pou.gui.PantallaGameOver;
import com.pou.gui.PantallaJuego;
import com.pou.gui.PantallaMenu;
import com.pou.util.GestorSonido;

import javax.swing.*;
import java.awt.*;

/**
 * Panel principal del juego que actúa como controlador central.
 * <p>
 * Implementa el ciclo de juego a {@value #FPS} FPS usando un hilo dedicado y
 * delta-time con {@link System#nanoTime()}, gestiona la máquina de estados
 * ({@code MENU → JUGANDO → GAME_OVER}) y delega el renderizado de forma
 * <em>polimórfica</em> a través de la interfaz {@link Pantalla}.
 * </p>
 * <p>
 * Extiende {@link JPanel} para integrarse directamente en el {@link JFrame}
 * e implementa {@link Runnable} para ejecutar el ciclo en un hilo separado.
 * </p>
 */
public class GameLoop extends JPanel implements Runnable {

    /** Ancho del panel de juego en píxeles. */
    public static final int ANCHO = 400;

    /** Alto del panel de juego en píxeles. */
    public static final int ALTO  = 620;

    private static final int FPS = 60;

    /** Fracción del alto de pantalla desde la parte superior donde se mantiene a Pou. */
    private static final double CAM_THRESHOLD = 0.42;

    private Thread hilo;
    private volatile GameState estado;

    private Pou             pou;
    private GeneradorNubes  generadorNubes;
    private ControlJugador  control;
    private Puntaje         puntaje;

    private double cameraY;
    private double cameraYMin;
    private double yInicialPou;

    private final PantallaMenu     pantallaMenu     = new PantallaMenu();
    private final PantallaJuego    pantallaJuego    = new PantallaJuego();
    private final PantallaGameOver pantallaGameOver = new PantallaGameOver();

    /**
     * Referencia polimórfica a la pantalla primaria activa.
     * Al cambiar de estado se reasigna a la implementación correspondiente
     * de {@link Pantalla} y {@link #paintComponent} la dibuja sin conocer el tipo concreto.
     */
    private Pantalla    pantallaPrimaria;
    private Cronometro  cronometro;

    /**
     * Construye el panel de juego, establece su tamaño preferido y llama a
     * {@link #inicializar()} para preparar todos los subsistemas.
     */
    public GameLoop() {
        setPreferredSize(new Dimension(ANCHO, ALTO));
        setBackground(Color.BLACK);
        setFocusable(true);
        inicializar();
    }

    // ------------------------------------------------------------------
    //  Init / reset
    // ------------------------------------------------------------------

    /**
     * Configura el estado inicial: coloca la máquina de estados en {@code MENU},
     * asigna la pantalla primaria y registra el listener de teclado.
     */
    private void inicializar() {
        estado           = GameState.MENU;
        pantallaPrimaria = pantallaMenu;
        iniciarPartida();
        control = new ControlJugador(pou);
        addKeyListener(control);
    }

    /**
     * Reinicia todas las variables de partida: posición de Pou, generador de nubes,
     * cámara y puntuación. Se invoca tanto al inicio del juego como al reiniciar
     * desde la pantalla de Game Over.
     */
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

        if (cronometro == null) {
            cronometro = new Cronometro();
        } else {
            cronometro.reiniciar();
        }

        if (control != null) control.setPou(pou);
    }

    // ------------------------------------------------------------------
    //  Game loop
    // ------------------------------------------------------------------

    /**
     * Crea e inicia el hilo daemon que ejecuta el ciclo de juego.
     * Debe llamarse una sola vez, después de que la ventana sea visible.
     */
    public void iniciarHilo() {
        hilo = new Thread(this);
        hilo.setDaemon(true);
        hilo.start();
    }

    /**
     * Cuerpo del hilo de juego. Ejecuta un ciclo de tiempo fijo (delta-time)
     * a {@value #FPS} FPS usando {@link System#nanoTime()} para medir el tiempo
     * transcurrido. Llama a {@link #actualizar()} y {@link #repaint()} en cada tick.
     */
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

    /**
     * Despacha la actualización al estado activo de la máquina de estados.
     * En {@code MENU} y {@code GAME_OVER} gestiona las transiciones por teclado
     * y reasigna {@link #pantallaPrimaria}; en {@code JUGANDO} delega a
     * {@link #actualizarJuego()}.
     */
    private void actualizar() {
        switch (estado) {
            case MENU:
                if (control.consumirEnter()) {
                    estado           = GameState.JUGANDO;
                    pantallaPrimaria = pantallaJuego;
                    cronometro.reiniciar();
                    cronometro.iniciar();
                    GestorSonido.getInstancia().iniciarMusicaFondo();
                }
                break;

            case JUGANDO:
                actualizarJuego();
                break;

            case GAME_OVER:
                if (control.consumirEnter()) {
                    iniciarPartida();
                    estado           = GameState.JUGANDO;
                    pantallaPrimaria = pantallaJuego;
                    cronometro.iniciar();
                    GestorSonido.getInstancia().iniciarMusicaFondo();
                }
                if (control.consumirEsc()) {
                    iniciarPartida();
                    estado           = GameState.MENU;
                    pantallaPrimaria = pantallaMenu;
                }
                break;
        }
    }

    /**
     * Actualiza la lógica de la partida en cada fotograma: mueve a Pou,
     * desplaza la cámara hacia arriba (nunca hacia abajo), genera y elimina nubes,
     * detecta colisiones y actualiza el puntaje. Si Pou cae fuera del área visible
     * transiciona a {@code GAME_OVER}.
     */
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
            cronometro.detener();
            GestorSonido.getInstancia().detenerMusicaFondo();
            GestorSonido.getInstancia().reproducirGameOver();
        }
    }

    // ------------------------------------------------------------------
    //  Rendering
    // ------------------------------------------------------------------

    /**
     * Renderiza el fotograma actual mediante la referencia polimórfica {@link #pantallaPrimaria}.
     * Llama a {@link Pantalla#dibujar} sin conocer el tipo concreto, lo que demuestra
     * el polimorfismo en tiempo de ejecución. En el estado {@code GAME_OVER} superpone
     * adicionalmente la pantalla de Game Over sobre el estado congelado del juego.
     *
     * @param g contexto gráfico proporcionado por Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,        RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,   RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,           RenderingHints.VALUE_RENDER_QUALITY);

        // Load context data before drawing
        if (estado == GameState.JUGANDO || estado == GameState.GAME_OVER) {
            pantallaJuego.cargar(pou, generadorNubes.getNubes(), cameraY,
                    puntaje.getPuntos(), puntaje.getRecord(), cronometro.getSegundos());
        }

        // Polymorphic call — GameLoop doesn't know which Pantalla is active
        pantallaPrimaria.dibujar(g2d, ANCHO, ALTO);

        // Game Over overlay is drawn on top of the frozen game view
        if (estado == GameState.GAME_OVER) {
            pantallaGameOver.cargar(puntaje.getPuntos(), puntaje.getRecord(), cronometro.getSegundos());
            pantallaGameOver.dibujar(g2d, ANCHO, ALTO);
        }
    }
}
