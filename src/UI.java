import javax.swing.*;

public class UI {

    // Create window
    public JFrame window;

    public UI() {
        // Initialise window
        window = new JFrame();
        window.setSize(500, 600);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLayout(null);
        window.setResizable(false);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

}
