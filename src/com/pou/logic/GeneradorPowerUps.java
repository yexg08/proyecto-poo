package com.pou.logic;

import com.pou.entities.PowerUp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Genera y administra los power-ups activos en el mundo de juego.
 * <p>
 * El primero aparece entre 4 y 6 segundos (240–360 fotogramas) y los siguientes
 * entre 8 y 12 segundos (480–720 fotogramas), en el tercio superior de la vista.
 * Los power-ups recogidos o que salen de la pantalla por debajo se eliminan
 * automáticamente en cada actualización.
 * </p>
 */
public class GeneradorPowerUps {

    private final List<PowerUp> powerUps = new ArrayList<>();
    private final Random        rng      = new Random();
    private final int           anchoPanel;
    private int                 ticksHastaProximo;

    /**
     * Crea el generador con el ancho del panel de juego.
     * El primer power-up se programa entre 4 y 6 segundos después del inicio.
     *
     * @param anchoPanel ancho del panel en píxeles; limita la posición X de spawn
     */
    public GeneradorPowerUps(int anchoPanel) {
        this.anchoPanel        = anchoPanel;
        this.ticksHastaProximo = 240 + rng.nextInt(120); // 4–6 s
    }

    /**
     * Actualiza el estado del generador en cada fotograma: elimina power-ups
     * obsoletos y spawna uno nuevo cuando el contador llega a cero.
     *
     * @param cameraY   posición de la cámara en el mundo; negativa al subir
     * @param altoPanel alto del panel en píxeles
     */
    public void actualizar(double cameraY, int altoPanel) {
        powerUps.removeIf(p -> p.isRecogido() || p.getY() > cameraY + altoPanel + 50);

        if (--ticksHastaProximo <= 0) {
            spawnPowerUp(cameraY, altoPanel);
            ticksHastaProximo = 480 + rng.nextInt(240); // 8–12 s
        }
    }

    /**
     * Crea un nuevo power-up en posición aleatoria dentro del tercio superior de
     * la vista actual para que Pou lo cruce de forma natural al saltar.
     *
     * @param cameraY   posición de la cámara en el mundo
     * @param altoPanel alto del panel en píxeles
     */
    private void spawnPowerUp(double cameraY, int altoPanel) {
        double x    = 20 + rng.nextInt(anchoPanel - 20 - PowerUp.ANCHO);
        double y    = cameraY + 40 + rng.nextInt(altoPanel / 3);
        PowerUp.Tipo tipo = rng.nextBoolean() ? PowerUp.Tipo.VIDA_EXTRA : PowerUp.Tipo.SUPER_SALTO;
        powerUps.add(new PowerUp(x, y, tipo));
    }

    /**
     * Devuelve la lista de power-ups activos actualmente en el mundo.
     *
     * @return lista mutable de power-ups; los elementos recogidos se eliminan
     *         en la siguiente llamada a {@link #actualizar}
     */
    public List<PowerUp> getPowerUps() { return powerUps; }
}
