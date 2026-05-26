package com.pou.util;

import javazoom.jl.player.Player;

import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * Gestor de audio centralizado implementado como Singleton.
 * <p>
 * Administra la música de fondo (reproducción en bucle mediante un hilo daemon)
 * y los efectos de sonido de un solo disparo (cada efecto corre en su propio
 * hilo daemon para no bloquear el ciclo de juego). Usa la biblioteca JLayer
 * para decodificar y reproducir archivos MP3.
 * </p>
 * <p>
 * La música de fondo solo suena durante el estado {@code JUGANDO}; se detiene
 * automáticamente al pasar a {@code GAME_OVER} y se reinicia al comenzar
 * una nueva partida.
 * </p>
 */
public final class GestorSonido {

    private static final GestorSonido INSTANCIA = new GestorSonido();

    /** Ruta base de los recursos de sonido en el classpath. */
    private static final String CARPETA = "/resources/sonidos/";

    private volatile boolean musicaActiva;
    private volatile Player  reproductorMusica;
    private Thread           hiloMusica;

    private GestorSonido() {}

    /**
     * Devuelve la única instancia del gestor de sonido.
     *
     * @return instancia singleton de {@code GestorSonido}
     */
    public static GestorSonido getInstancia() { return INSTANCIA; }

    // ------------------------------------------------------------------ Música de fondo

    /**
     * Detiene cualquier música en curso e inicia la reproducción de la música
     * de fondo en bucle infinito en un hilo daemon.
     * Llamar a este método cuando el juego pasa al estado {@code JUGANDO}.
     */
    public synchronized void iniciarMusicaFondo() {
        detenerMusicaFondo();
        musicaActiva = true;
        hiloMusica = new Thread(this::reproducirMusicaEnBucle, "musica-fondo");
        hiloMusica.setDaemon(true);
        hiloMusica.start();
    }

    /**
     * Detiene la reproducción de la música de fondo y espera hasta 1 segundo
     * a que el hilo termine. Llamar cuando el juego pasa a {@code GAME_OVER} o al menú.
     */
    public synchronized void detenerMusicaFondo() {
        musicaActiva = false;
        cerrarReproductor();
        if (hiloMusica != null) {
            hiloMusica.interrupt();
            try { hiloMusica.join(1000); } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
            hiloMusica = null;
        }
    }

    /**
     * Bucle interno de reproducción de música de fondo. Abre el archivo MP3,
     * crea un {@link Player} y lo reproduce; al terminar la pista vuelve a
     * empezar si {@code musicaActiva} sigue siendo {@code true}.
     */
    private void reproducirMusicaEnBucle() {
        while (musicaActiva) {
            Player player = null;
            try (InputStream in = abrir("sonido fondo.mp3");
                 BufferedInputStream bis = new BufferedInputStream(in)) {
                player = new Player(bis);
                reproductorMusica = player;
                player.play();
            } catch (Exception ignored) {
            } finally {
                if (reproductorMusica == player) reproductorMusica = null;
                if (player != null) player.close();
            }
            if (!musicaActiva || Thread.currentThread().isInterrupted()) break;
        }
    }

    /**
     * Cierra el reproductor de música de fondo activo de forma segura.
     */
    private void cerrarReproductor() {
        Player p = reproductorMusica;
        reproductorMusica = null;
        if (p != null) p.close();
    }

    // ------------------------------------------------------------------ Efectos de sonido

    /**
     * Reproduce el sonido de salto. Se llama cada vez que Pou aterriza en una nube.
     */
    public void reproducirSalto()     { reproducirEfecto("sonido salto.mp3"); }

    /**
     * Reproduce el sonido de Game Over. Se llama cuando Pou cae fuera de la pantalla.
     */
    public void reproducirGameOver()  { reproducirEfecto("sonido game over.mp3"); }

    /**
     * Reproduce el sonido de nube rota. Se llama cuando Pou pisa una nube frágil.
     */
    public void reproducirNubeRota()  { reproducirEfecto("sonido breaking cloud.mp3"); }

    /**
     * Abre el archivo MP3 indicado y lo reproduce en un hilo daemon independiente
     * para no bloquear el ciclo de juego.
     *
     * @param nombre nombre del archivo de sonido dentro de {@link #CARPETA}
     */
    private void reproducirEfecto(String nombre) {
        Thread hilo = new Thread(() -> {
            try (InputStream in = abrir(nombre);
                 BufferedInputStream bis = new BufferedInputStream(in)) {
                new Player(bis).play();
            } catch (Exception ignored) {}
        }, "sfx-" + nombre);
        hilo.setDaemon(true);
        hilo.start();
    }

    // ------------------------------------------------------------------ Utilidad

    /**
     * Abre un recurso de audio desde el classpath.
     *
     * @param nombre nombre del archivo dentro de {@link #CARPETA}
     * @return {@link InputStream} del recurso
     * @throws IllegalStateException si el archivo no se encuentra en el classpath
     */
    private InputStream abrir(String nombre) {
        InputStream in = GestorSonido.class.getResourceAsStream(CARPETA + nombre);
        if (in == null) throw new IllegalStateException("Sonido no encontrado: " + CARPETA + nombre);
        return in;
    }
}
