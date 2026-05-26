package com.pou.util;

import javazoom.jl.player.Player;

import java.io.BufferedInputStream;
import java.io.InputStream;

public final class GestorSonido {

    private static final GestorSonido INSTANCIA = new GestorSonido();
    private static final String CARPETA = "/resources/sonidos/";

    private volatile boolean musicaActiva;
    private volatile Player  reproductorMusica;
    private Thread           hiloMusica;

    private GestorSonido() {}

    public static GestorSonido getInstancia() { return INSTANCIA; }

    // ------------------------------------------------------------------ Música de fondo

    public synchronized void iniciarMusicaFondo() {
        detenerMusicaFondo();
        musicaActiva = true;
        hiloMusica = new Thread(this::reproducirMusicaEnBucle, "musica-fondo");
        hiloMusica.setDaemon(true);
        hiloMusica.start();
    }

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

    private void cerrarReproductor() {
        Player p = reproductorMusica;
        reproductorMusica = null;
        if (p != null) p.close();
    }

    // ------------------------------------------------------------------ Efectos de sonido

    public void reproducirSalto()     { reproducirEfecto("sonido salto.mp3"); }
    public void reproducirGameOver()  { reproducirEfecto("sonido game over.mp3"); }
    public void reproducirNubeRota()  { reproducirEfecto("sonido breaking cloud.mp3"); }

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

    private InputStream abrir(String nombre) {
        InputStream in = GestorSonido.class.getResourceAsStream(CARPETA + nombre);
        if (in == null) throw new IllegalStateException("Sonido no encontrado: " + CARPETA + nombre);
        return in;
    }
}
