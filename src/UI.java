import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

public class UI extends JFrame {
    private Game game; // current game being played

    private JTextField[] guessFields; // Boxes to display guesses

    private String encrypted; // the encrypted string for the current game
    private String[] encryptedTokens;

    private JPanel gamePanel; // Combined Panel for the whole game
    private JPanel guessRow; // Panel for guess boxes
    private JPanel encryptedRow; // Panel for encrypted characters
    private JPanel freqRow; // Panel for the frequencies of characters

    private String type = "Letter";

    private Players players;
    private Player currentPlayer;

    public UI() {
        /**
        initGame();
        buildFrame();
        buildEncryptedRow();
        buildGuessRow();
        addListeners();

        setVisible(true);
        refreshBoard();
         **/

        players = new Players();
        players.loadPlayers();
    }

    public void startGame(String username) {
        initPlayer(username);
        initGame(username);
        buildFrame();
        buildEncryptedRow();
        buildGuessRow();
        buildFreqRow();
        buildActionsRow();
        addListeners();
        setVisible(true);

        refreshBoard();
    }

    public String getUsername() {
        JOptionPane box = new JOptionPane();
        //box.createDialog("Please enter your username");
        return box.showInputDialog("Enter your username");
    }

    private void initPlayer(String username) {
        currentPlayer = players.findPlayer(username);
        if (currentPlayer == null) {
            currentPlayer = new Player();
            currentPlayer.updateUsername(username);
            players.addPlayer(currentPlayer);
        }
    }

    private void initGame(String username) {
        try {
            game = new Game(currentPlayer);
            game.getCurrentPlayer().updateUsername(username);
            game.loadGame();
            // if game loaded has already been won, force make a new game
            if (game.checkWinOnLoad()) {
                this.game = new Game(currentPlayer, type);
                game.getCurrentPlayer().updateUsername(username);
            }
            this.encrypted = game.getCurrentCryptogram().getEncryptedPhrase();
            this.encryptedTokens = new String[encrypted.length()];
        }
        catch (Exception e) {
            // Null pointer exception if game not loaded
            this.game = new Game(currentPlayer, type); // Hard coded for now
            game.getCurrentPlayer().updateUsername(username);
            this.encrypted = game.getCurrentCryptogram().getEncryptedPhrase();
            this.encryptedTokens = new String[encrypted.length()];
        }
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
        //add(gamePanel, BorderLayout.CENTER);

        // Adds wrapper with diffrent formatting for game panel, removes weird spacing
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new FlowLayout((FlowLayout.CENTER)));
        wrapper.add(gamePanel);

        add(wrapper);
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

    private void buildFreqRow() {
        freqRow = new JPanel();
        Map<Object, Integer> freqs = game.getCurrentCryptogram().getFrequencies();

        if (type.equals("Letter")) {
            for (char c : encrypted.toCharArray()) {
                if (c == ' ') {
                    JLabel label = new JLabel(" ");
                    freqRow.add(label);
                }
                Integer freq = freqs.get(c);
                if (freq != null) {
                    JLabel label = new JLabel(freq.toString());
                    freqRow.add(label);
                }
            }
        }

        if (type.equals("Number")) {
            for (String x : encrypted.split(" ")) {
                if (x.equals("")) continue;

                Integer key = Integer.parseInt(x);
                Integer freq = freqs.get(key);
                if (freq != null) {
                    JLabel label = new JLabel(freq.toString());
                    freqRow.add(label);
                }
            }
        }
        gamePanel.add(freqRow);
    }

    private void buildActionsRow() {
        JPanel actionsPanel = new JPanel();

        JButton btnShowSolution = new JButton("Show Solution");
        btnShowSolution.addActionListener(e -> {
            game.revealSolution();
            refreshBoard();
        });

        JButton btnGetHint = new JButton("Get Hint");
        btnGetHint.addActionListener(e -> {
            game.getHint();
            refreshBoard();
        });

        actionsPanel.add(btnShowSolution);
        actionsPanel.add(btnGetHint);
        gamePanel.add(actionsPanel, BorderLayout.SOUTH);
    }

    // adds listeners
    private void addListeners() {
        // adds a listener that lets us do things when the window closes
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                players.savePlayers();
            }
        });

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
        if (guessFields == null) return;
        game.saveGame();
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

    // Method to compare guesses to actual answer to check if player won and display the win message
    private void checkWin() {
        if (game.checkWin()) {
            int answer = JOptionPane.showConfirmDialog(null, "Would you like to start a new game?", "You Won!", JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                exitGame();
                startGame(getUsername());
            }
            if (answer == JOptionPane.NO_OPTION) {
                exitGame();
            }
        }
    }

    private void exitGame() {
        //gamePanel.removeAll();
        players.savePlayers();
        getContentPane().remove(gamePanel);
        gamePanel.removeAll();
        gamePanel.revalidate();
        gamePanel.repaint();
        dispose();
    }
}
