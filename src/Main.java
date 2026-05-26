import com.pou.logic.GameLoop;

import javax.swing.*;

public class Main {
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
