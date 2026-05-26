package com.pou.logic;

/**
 * Estados posibles de la máquina de estados del juego.
 * El flujo normal es {@code MENU → JUGANDO → GAME_OVER → JUGANDO / MENU}.
 */
public enum GameState {

    /** Pantalla de inicio; el juego espera que el jugador presione ENTER. */
    MENU,

    /** Partida activa; se actualizan físicas, cámara y colisiones cada fotograma. */
    JUGANDO,

    /** El jugador cayó fuera de la pantalla; se muestra la tarjeta de Game Over. */
    GAME_OVER
}
