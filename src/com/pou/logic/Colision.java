package com.pou.logic;

import com.pou.entities.Nube;
import com.pou.entities.Pou;
import com.pou.entities.PowerUp;
import com.pou.util.GestorSonido;

import java.awt.*;
import java.util.List;

/**
 * Detecta y resuelve las colisiones entre Pou y las nubes.
 * <p>
 * La detección se realiza únicamente mientras Pou cae (velocidad Y positiva).
 * Se comprueba que los pies de Pou toquen la franja superior de una nube y que
 * haya superposición horizontal. Al detectar colisión se ejecuta el salto,
 * se reproduce el sonido correspondiente y se rompe la nube si es frágil.
 * </p>
 */
public class Colision {

    /**
     * Verifica si los pies de Pou colisionan con alguna nube de la lista y,
     * en caso afirmativo, aplica el rebote y los efectos de sonido.
     * <p>
     * Solo se procesa la primera nube impactada por fotograma ({@code break} tras
     * el primer choque) para evitar saltos dobles.
     * </p>
     *
     * @param pou   entidad jugador cuya posición y velocidad se evalúan
     * @param nubes lista de nubes activas en el mundo
     */
    /**
     * Verifica si Pou toca algún power-up de la lista y, en caso afirmativo,
     * lo marca como recogido y devuelve su tipo para que el game loop aplique
     * el efecto correspondiente.
     *
     * @param pou      entidad jugador
     * @param powerUps lista de power-ups activos en el mundo
     * @return tipo del power-up recogido, o {@code null} si no hubo colisión
     */
    public static PowerUp.Tipo verificarPowerUps(Pou pou, List<PowerUp> powerUps) {
        Rectangle pouBounds = pou.getBounds();
        for (PowerUp p : powerUps) {
            if (!p.isRecogido() && pouBounds.intersects(p.getBounds())) {
                p.recoger();
                return p.getTipo();
            }
        }
        return null;
    }

    public static void verificar(Pou pou, List<Nube> nubes) {
        // Only check collision while Pou is falling
        if (pou.getVelocidadY() <= 0) return;

        // Foot strip: bottom center of Pou
        int pFoot  = (int) (pou.getY() + Pou.ALTO);
        int pLeft  = (int) (pou.getX() + 6);
        int pRight = (int) (pou.getX() + Pou.ANCHO - 6);

        for (Nube nube : nubes) {
            int nTop   = (int) nube.getY();
            int nLeft  = (int) nube.getX();
            int nRight = (int) (nube.getX() + Nube.ANCHO);

            // Feet must land within 22 px of the cloud top and overlap horizontally
            boolean enRango    = pFoot >= nTop && pFoot <= nTop + 22;
            boolean superpone  = pRight > nLeft && pLeft < nRight;

            if (enRango && superpone) {
                pou.saltar();
                GestorSonido.getInstancia().reproducirSalto();
                if (nube.getTipo() == Nube.Tipo.FRAGIL) {
                    GestorSonido.getInstancia().reproducirNubeRota();
                }
                nube.romper();
                break;
            }
        }
    }
}
