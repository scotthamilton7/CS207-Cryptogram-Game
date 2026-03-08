import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Map;

/**
 * Current Issues -
 * Needs to skip spaces (Not make a box)
 * Needs to be able to check if the player won (shouldnt be hard)
 * UI should probably look alot better
 * Needs Hint, Show solution buttons etc (also shouldnt be too hard)
 * Also still needs reworked abit for number cryptograms (currently makes too many boxes when given a double digit int)
 */
public class UI extends JFrame {
    private Game game; // current game being played

    private JTextField[] guessFields; // Boxes to display guesses

    private String encrypted; // the encrypted string for the current game

    private JPanel gamePanel; // Combined Panel for the whole game
    private JPanel guessRow; // Panel for guess boxes
    private JPanel encryptedRow; // Panel for encrypted characters

    private String type = "Number";

    public UI() {
        this.game = new Game(new Player(), type); // Hard coded for now
        this.encrypted = game.getCurrentCryptogram().getEncryptedPhrase();

        buildFrame();
        buildEncryptedRow();
        buildGuessRow();
        addListeners();
    }

    private void buildFrame() {
        setTitle("Cryptogram Game");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLayout(new BorderLayout());

        gamePanel = new JPanel();
        // this puts the encryption above the guesses
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS));
        add(gamePanel, BorderLayout.CENTER);
    }

    // Displays encrypted characters 1 by 1
    private void buildEncryptedRow() {
        encryptedRow = new JPanel();
        int length = encrypted.length();

        if (type.equals("Number")) {
            for (String x : encrypted.split(" ")) {
                JLabel label = new JLabel(x);
                encryptedRow.add(label);
            }
        }
        else if  (type.equals("Letter")) {
            for (int i = 0; i < length; i++) {
                char character = encrypted.charAt(i);
                JLabel label = new JLabel(String.valueOf(character));

                encryptedRow.add(label);
            }
        }
        gamePanel.add(encryptedRow);
    }

    // Makes boxes for every character in encrypted phrase
    private void buildGuessRow() {
        guessRow = new JPanel();
        int length = encrypted.length();
        guessFields = new JTextField[length];

        if (type.equals("Number")) {
            int i = 0;
            for (String x : encrypted.split(" ")) {
                if (!x.equals("")) {
                    JTextField field = new JTextField(1);
                    guessRow.add(field);
                    guessFields[i++] = field;
                }
                else {
                    JLabel field = new JLabel(" ");
                    guessRow.add(field);
                }
            }
        }

        else if  (type.equals("Letter")) {
            for (int i = 0; i < length; i++) {
                if (encrypted.charAt(i) != ' ') {
                    JTextField field = new JTextField(1);
                    guessRow.add(field);
                    guessFields[i] = field;
                }
                else {
                    JLabel field = new JLabel("   ");
                    guessRow.add(field);
                }
            }
        }

        gamePanel.add(guessRow);
    }

    // adds listeners
    private void addListeners() {
        for (int i = 0; i < guessFields.length; i++) {

            int index = i; // inner method cant access i normally
            if (guessFields[i] == null) continue;
            // for every box, add a key listener to update map every time a letter is added or removed
            guessFields[i].addKeyListener(new KeyAdapter() {
                public void keyReleased(KeyEvent e) {
                    JTextField field = guessFields[index];
                    String text = field.getText().toUpperCase();
                    char encryptedChar = encrypted.charAt(index);

                    // if event is triggered and box is empty, something must have been removed, else, something must have been added
                    if (text.isEmpty()) {
                        //game.getPlayergameMapping().remove(encryptedChar);
                        game.undoLetter(encryptedChar);
                    } else {
                        char guess = text.charAt(0);
                        game.enterLetter(encryptedChar, guess);
                    }
                    refreshBoard();
                }
            });
        }
    }
    // reloads the boxes with the updated game mapping
    public void refreshBoard() {
        Map<Object, Character> guesses = game.getPlayergameMapping();

        for (int i = 0; i < guessFields.length; i++) {
            if (guessFields[i] == null) continue;

            char encryptedChar = encrypted.charAt(i);
            Character guess = guesses.get(encryptedChar);

            //sets everything to blank that isnt in game mapping
            if (guess == null) {
                if (!guessFields[i].getText().isEmpty()) {
                    guessFields[i].setText("");
                }
            } else {
                String guessStr = String.valueOf(guess);
                if (!guessFields[i].getText().equals(guessStr)) {
                    guessFields[i].setText(guessStr);
                }
            }
        }
    }

    private void checkWin() {
        // Check if the player won, not sure what to do after win?
    }
}
