package com.pou.logic;

import javax.swing.Timer;

/**
 * Cronómetro de partida basado en {@link javax.swing.Timer}.
 * <p>
 * Dispara un evento cada segundo en el Event Dispatch Thread e incrementa
 * un contador interno. Se usa para mostrar el tiempo transcurrido en el HUD
 * durante el estado {@code JUGANDO}.
 * </p>
 */
public class Cronometro {

    private volatile int segundos;
    private final Timer timer;

    /**
     * Construye el cronómetro configurando el {@link Timer} con un intervalo de 1 000 ms.
     */
    public Cronometro() {
        timer = new Timer(1000, e -> segundos++);
    }

    /**
     * Inicia el conteo. Si ya estaba corriendo no tiene efecto adicional.
     */
    public void iniciar() {
        timer.start();
    }

    /**
     * Detiene el conteo sin resetear el tiempo acumulado.
     */
    public void detener() {
        timer.stop();
    }

    /**
     * Reinicia el contador a cero sin detener el timer.
     * Llamar antes de {@link #iniciar()} al comenzar una nueva partida.
     */
    public void reiniciar() {
        segundos = 0;
    }

    /**
     * Devuelve los segundos transcurridos desde el último {@link #reiniciar()}.
     *
     * @return tiempo en segundos
     */
    public int getSegundos() {
        return segundos;
    }
}
