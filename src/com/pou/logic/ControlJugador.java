package com.pou.logic;

import com.pou.entities.Pou;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public class ControlJugador extends KeyAdapter {

    private Pou pou;
    private final Set<Integer> teclasPres = new HashSet<>();
    private boolean enterPresionado;
    private boolean escPresionado;

    public ControlJugador(Pou pou) {
        this.pou = pou;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        teclasPres.add(e.getKeyCode());
        if (e.getKeyCode() == KeyEvent.VK_ENTER) enterPresionado = true;
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) escPresionado  = true;
        actualizarMovimiento();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        teclasPres.remove(e.getKeyCode());
        actualizarMovimiento();
    }

    private void actualizarMovimiento() {
        boolean izq = teclasPres.contains(KeyEvent.VK_LEFT)  || teclasPres.contains(KeyEvent.VK_A);
        boolean der = teclasPres.contains(KeyEvent.VK_RIGHT) || teclasPres.contains(KeyEvent.VK_D);
        pou.setMoviendoIzquierda(izq);
        pou.setMoviendoDerecha(der);
    }

    /** Returns true once per ENTER press (consumed). */
    public boolean consumirEnter() {
        boolean v = enterPresionado;
        enterPresionado = false;
        return v;
    }

    /** Returns true once per ESC press (consumed). */
    public boolean consumirEsc() {
        boolean v = escPresionado;
        escPresionado = false;
        return v;
    }

    public void setPou(Pou pou) {
        this.pou = pou;
        // Clear held keys on re-init to avoid ghost movement
        teclasPres.clear();
        actualizarMovimiento();
    }
}
