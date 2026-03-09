import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Map;

public class UI extends JFrame {
    private Game game; // current game being played

    private JTextField[] guessFields; // Boxes to display guesses

    private String encrypted; // the encrypted string for the current game
    private String[] encryptedTokens;

    private JPanel gamePanel; // Combined Panel for the whole game
    private JPanel guessRow; // Panel for guess boxes
    private JPanel encryptedRow; // Panel for encrypted characters

    private String type = "Letter";

    public UI() {
        this.game = new Game(new Player(), type); // Hard coded for now
        this.encrypted = game.getCurrentCryptogram().getEncryptedPhrase();
        this.encryptedTokens = new String[encrypted.length()];

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
                    encryptedTokens[i] = x;
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

                    if (type.equals("Number")) {
                        String token = encryptedTokens[index];
                        int encryptedNum = Integer.parseInt(token);

                        if (text.isEmpty()) {
                            game.undoLetter(encryptedNum);
                        } else {
                            char guess = text.charAt(0);
                            game.enterLetter(encryptedNum, guess);
                        }
                    }

                    else if (type.equals("Letter")) {
                        char encryptedChar = encrypted.charAt(index);
                        // if event is triggered and box is empty, something must have been removed, else, something must have been added
                        if (text.isEmpty()) {
                            //game.getPlayergameMapping().remove(encryptedChar);
                            game.undoLetter(encryptedChar);
                        } else {
                            char guess = text.charAt(0);
                            game.enterLetter(encryptedChar, guess);
                        }
                    }

                    refreshBoard();
                }
            });
        }
    }

    // reloads the boxes with the updated game mapping
    public void refreshBoard() {
        Map<Object, Character> guesses = game.getPlayergameMapping();

        if (type.equals("Number")) {
            int i = 0;
            for (String x : encrypted.split(" ")) {
                if (x.equals("") || x.equals(" ")) {
                    continue;
                }
                if (guessFields[i] == null) {
                    i++;
                    continue;
                }
                Character guess = guesses.get(Integer.valueOf(x));

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

                i++;
            }
        }
        else if  (type.equals("Letter")) {
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
        checkWin();
    }

    // Method to compare guesses to actual answer to check if player won
    // might have problems with number cryptogram
    private void checkWin() {
        // Check if the player won, not sure what to do after win?
        String SolutionPhrase = game.getCurrentCryptogram().getPhrase(); // Stores unencrypted phrase
        String EncryptedPhrase = game.getCurrentCryptogram().getEncryptedPhrase(); // Stores encrypted phrase
        Map<Object, Character> guesses = game.getPlayergameMapping();

        // Sets guesses to answer and goes until player gets correct answer
        for (int i = 0; i < SolutionPhrase.length(); i++){
            char answerChar = SolutionPhrase.charAt(i);
            char encryptChar = EncryptedPhrase.charAt(i);
            Character guess = guesses.get(encryptChar);
            if (guess == null || guess != answerChar) {
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "You win!"); // displays win message
    }
}
