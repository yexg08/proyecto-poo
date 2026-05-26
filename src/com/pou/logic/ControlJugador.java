package com.pou.logic;

import com.pou.entities.Pou;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * Controlador de entrada de teclado del jugador.
 * <p>
 * Extiende {@link KeyAdapter} para capturar eventos de teclado y traducirlos
 * en comandos de movimiento para {@link Pou}. Las teclas ENTER y ESC se
 * tratan como eventos de un solo uso que deben ser consumidos explícitamente
 * por el game loop mediante {@link #consumirEnter()} y {@link #consumirEsc()}.
 * </p>
 */
public class ControlJugador extends KeyAdapter {

    private Pou pou;
    private final Set<Integer> teclasPres = new HashSet<>();
    private boolean enterPresionado;
    private boolean escPresionado;

    /**
     * Construye el controlador asociado a la instancia de Pou indicada.
     *
     * @param pou entidad jugador que recibirá los comandos de movimiento
     */
    public ControlJugador(Pou pou) {
        this.pou = pou;
    }

    /**
     * Registra la tecla presionada, activa los flags de ENTER/ESC si corresponde
     * y actualiza el movimiento de Pou.
     *
     * @param e evento de tecla presionada
     */
    @Override
    public void keyPressed(KeyEvent e) {
        teclasPres.add(e.getKeyCode());
        if (e.getKeyCode() == KeyEvent.VK_ENTER) enterPresionado = true;
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) escPresionado  = true;
        actualizarMovimiento();
    }

    /**
     * Elimina la tecla soltada del conjunto de teclas activas y actualiza
     * el movimiento de Pou.
     *
     * @param e evento de tecla soltada
     */
    @Override
    public void keyReleased(KeyEvent e) {
        teclasPres.remove(e.getKeyCode());
        actualizarMovimiento();
    }

    /**
     * Lee el estado actual del conjunto de teclas y aplica la dirección de movimiento
     * a Pou. Soporta las flechas ← → y las teclas A / D.
     */
    private void actualizarMovimiento() {
        boolean izq = teclasPres.contains(KeyEvent.VK_LEFT)  || teclasPres.contains(KeyEvent.VK_A);
        boolean der = teclasPres.contains(KeyEvent.VK_RIGHT) || teclasPres.contains(KeyEvent.VK_D);
        pou.setMoviendoIzquierda(izq);
        pou.setMoviendoDerecha(der);
    }

    /**
     * Retorna {@code true} una sola vez por pulsación de ENTER (evento consumido).
     *
     * @return {@code true} si ENTER fue presionado desde la última llamada
     */
    public boolean consumirEnter() {
        boolean v = enterPresionado;
        enterPresionado = false;
        return v;
    }

    /**
     * Retorna {@code true} una sola vez por pulsación de ESC (evento consumido).
     *
     * @return {@code true} si ESC fue presionado desde la última llamada
     */
    public boolean consumirEsc() {
        boolean v = escPresionado;
        escPresionado = false;
        return v;
    }

    /**
     * Reemplaza la instancia de Pou controlada, limpia las teclas retenidas
     * y reinicia el movimiento para evitar movimiento fantasma al reiniciar.
     *
     * @param pou nueva instancia de Pou a controlar
     */
    public void setPou(Pou pou) {
        this.pou = pou;
        // Clear held keys on re-init to avoid ghost movement
        teclasPres.clear();
        actualizarMovimiento();
    }
}
