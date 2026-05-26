import com.pou.logic.GameLoop;

import javax.swing.*;

/**
 * Punto de entrada de la aplicación PouJump.
 * Inicializa la ventana Swing y arranca el ciclo de juego en el hilo de despacho de eventos.
 */
public class Main {

    /**
     * Crea el {@link JFrame} principal, agrega el panel de juego y lanza el hilo del game loop.
     *
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Pou Jump!");
            GameLoop gameLoop = new GameLoop();

            frame.add(gameLoop);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            gameLoop.requestFocusInWindow();
            gameLoop.iniciarHilo();
        });
    }
}
