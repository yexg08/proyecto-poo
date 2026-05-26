package com.pou.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Cargador centralizado de recursos gráficos del juego.
 * <p>
 * Carga los sprites PNG desde el classpath al iniciar la aplicación y los expone
 * como constantes estáticas. Si un archivo no se encuentra o falla la lectura,
 * el campo correspondiente queda en {@code null} y las clases que lo usan
 * recurren al dibujo geométrico de respaldo.
 * </p>
 */
public class Recursos {

    /** Sprite de Pou (personaje principal). */
    public static final BufferedImage POU         = cargar("/resources/sprites/spritesPou/pouSprite-removebg-preview.png");

    /** Sprite de nube normal (plataforma estática). */
    public static final BufferedImage NUBE_NORMAL = cargar("/resources/sprites/spritesNubes/Nube_normal-removebg-preview.png");

    /** Sprite de nube móvil (se desplaza horizontalmente). */
    public static final BufferedImage NUBE_MOVIL  = cargar("/resources/sprites/spritesNubes/Nube_en_movimiento-removebg-preview.png");

    /** Sprite de nube frágil (se rompe al ser pisada). */
    public static final BufferedImage NUBE_FRAGIL  = cargar("/resources/sprites/spritesNubes/Nube_fragil-removebg-preview.png");

    /** Sprite del power-up de vida extra (cruz verde). */
    public static final BufferedImage POWERUP_VIDA  = cargar("/resources/powerups/sprites/sprite_vida_extra-removebg-preview.png");

    /** Sprite del power-up de super salto (rayo amarillo). */
    public static final BufferedImage POWERUP_SALTO = cargar("/resources/powerups/sprites/sprite_super_salto-removebg-preview.png");

    /**
     * Carga una imagen PNG desde el classpath.
     *
     * @param ruta ruta absoluta del recurso dentro del classpath (p. ej. {@code /resources/sprites/...})
     * @return imagen cargada, o {@code null} si el recurso no existe o no se puede leer
     */
    private static BufferedImage cargar(String ruta) {
        try (InputStream is = Recursos.class.getResourceAsStream(ruta)) {
            if (is != null) return ImageIO.read(is);
        } catch (IOException ignored) {}
        return null;
    }

    private Recursos() {}
}
