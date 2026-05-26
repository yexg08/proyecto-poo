package com.pou.logic;

import com.pou.entities.Nube;
import com.pou.entities.Pou;
import com.pou.util.GestorSonido;

import java.util.List;

public class Colision {

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
