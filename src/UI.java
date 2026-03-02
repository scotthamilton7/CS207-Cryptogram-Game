import javax.swing.*;

public class UI {

    private int i = 0;
    private JLabel labelGuesses;
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

        // Buttons
        JButton button = new JButton("Play");
        button.setBounds(150, 400, 200, 50);
        window.add(button);
        button.addActionListener(e -> {counter();});

        // Text
        labelGuesses = new JLabel("Current Guesses: 0");
        labelGuesses.setBounds(20, 20, 200, 30); // x, y, width, height
        window.add(labelGuesses);




    }

    public void counter() {
        i++;
        labelGuesses.setText("Current Guesses: " + i);
    }


}
