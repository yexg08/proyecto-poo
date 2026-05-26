package com.pou.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Central sprite loader.
 * Place images in src/resources/ following the paths below.
 * If a file is absent, the field stays null and geometric drawing is used.
 */
public class Recursos {

    // Pou character
    public static final BufferedImage POU         = cargar("/resources/sprites/spritesPou/pouSprite-removebg-preview.png");

    // Clouds
    public static final BufferedImage NUBE_NORMAL = cargar("/resources/sprites/spritesNubes/Nube_normal-removebg-preview.png");
    public static final BufferedImage NUBE_MOVIL  = cargar("/resources/sprites/spritesNubes/Nube_en_movimiento-removebg-preview.png");
    public static final BufferedImage NUBE_FRAGIL = cargar("/resources/sprites/spritesNubes/Nube_fragil-removebg-preview.png");

    private static BufferedImage cargar(String ruta) {
        try (InputStream is = Recursos.class.getResourceAsStream(ruta)) {
            if (is != null) return ImageIO.read(is);
        } catch (IOException ignored) {}
        return null; // fallback: geometric drawing used
    }

    private Recursos() {}
}
