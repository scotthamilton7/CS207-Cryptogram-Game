import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

class JunitTests {

    // if you think more tests are needed for certain functions msg me on teams

    private Player testPlayer;
    private LetterCryptogram letterCryptogram;
    private NumberCryptogram numberCryptogram;
    private Game letterGame;
    private Game numberGame;
    private Players players;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        // creates test player
        testPlayer = new Player();
        testPlayer.updateUsername("test player");

        // creates test phrases file
        Path phrasesFile = tempDir.resolve("phrases.txt");
        List<String> phrases = Arrays.asList("HELLO WORLD", "TEST PHRASE", "CRYPTOGRAM GAME");
        Files.write(phrasesFile, phrases);

        // creates cryptograms with test file
        letterCryptogram = new LetterCryptogram(phrasesFile.toString());
        numberCryptogram = new NumberCryptogram(phrasesFile.toString());

        // creates games
        letterGame = new Game(testPlayer, "Letter");
        numberGame = new Game(testPlayer, "Number");

        // creates players
        players = new Players();
    }

    // player tests

    @Test
    void player() {
        Player player = new Player();
        assertEquals("", player.getUsername());
        assertEquals(0.0, player.getAccuracy());
        assertEquals(0, player.getTotalGuesses());
        assertEquals(0, player.getNumCryptogramsPlayed());
        assertEquals(0, player.getNumCryptogramsCompleted());
    }

    @Test
    void updateUsername() {
        Player player = new Player();
        player.updateUsername("NewUser");
        assertEquals("NewUser", player.getUsername());
    }

    @Test
    void incrementMethods() {
        Player player = new Player();
        player.incrementCryptogramsPlayed();
        player.incrementCryptogramsPlayed();
        assertEquals(2, player.getNumCryptogramsPlayed());

        player.incrementCryptogramsCompleted();
        assertEquals(1, player.getNumCryptogramsCompleted());

        player.incrementTotalGuesses();
        player.incrementTotalGuesses();
        player.incrementTotalGuesses();
        assertEquals(3, player.getTotalGuesses());
    }

    // cryptogram tests

    @Test
    void letterCryptogram() {
        assertNotNull(letterCryptogram);
        assertNotNull(letterCryptogram.getPhrase());
        assertFalse(letterCryptogram.getPhrase().isEmpty());
    }

    @Test
    void numberCryptogram() {
        assertNotNull(numberCryptogram);
        assertNotNull(numberCryptogram.getPhrase());
        assertFalse(numberCryptogram.getPhrase().isEmpty());
    }

    // checks if spaces are being counted in frequency

    @Test
    void letterFrequencies() {
        LetterCryptogram testCrypto = new LetterCryptogram("test");
        Map<Character, Integer> frequencies = testCrypto.getFrequencies();

        assertNotNull(frequencies);
        for (Map.Entry<Character, Integer> entry : frequencies.entrySet()) {
            assertNotEquals(' ', entry.getKey());
        }
    }

    @Test
    void numberFrequencies() {
        Map<Integer, Integer> frequencies = numberCryptogram.getFrequencies();
        assertNotNull(frequencies);
        assertFalse(frequencies.containsKey(999));
    }

    // checks that they are returning a valid character

    @Test
    void getPlainLetter() {
        String encryptedPhrase = letterCryptogram.getEncryptedPhrase();
        if (!encryptedPhrase.isEmpty() && encryptedPhrase.charAt(0) != ' ') {
            char encryptedChar = encryptedPhrase.charAt(0);
            char plainLetter = letterCryptogram.getPlainLetter(encryptedChar);
            assertTrue(plainLetter >= 'A' && plainLetter <= 'Z' || plainLetter == ' ');
        }
    }

    @Test
    void getPlainNumber() {
        String encryptedPhrase = numberCryptogram.getEncryptedPhrase();
        String[] tokens = encryptedPhrase.trim().split(" ");
        if (tokens.length > 0 && !tokens[0].isEmpty()) {
            int encryptedNum = Integer.parseInt(tokens[0]);
            char plainLetter = numberCryptogram.getPlainLetter(encryptedNum);
            assertTrue(plainLetter >= 'A' && plainLetter <= 'Z' || plainLetter == ' ');
        }
    }

     // checks if phrases actually get encrypted

    @Test
    void letterEncryptedPhrase() {
        String encrypted = letterCryptogram.getEncryptedPhrase();
        assertNotNull(encrypted);
        assertEquals(letterCryptogram.phrase.length(), encrypted.length());
    }

    @Test
    void numberEncryptedPhrase() {
        String encrypted = numberCryptogram.getEncryptedPhrase();
        assertNotNull(encrypted);
        assertTrue(encrypted.contains(" "));
    }

    // game tests

    @Test
    void gameConstructor() {
        Game game = new Game(testPlayer, "Letter");
        assertEquals(testPlayer, game.getCurrentPlayer());
        assertEquals("Letter", game.getCryptType());
        assertNotNull(game.getCurrentCryptogram());
        assertNotNull(game.getPlayergameMapping());
    }

    // all below makes sure undoing and entering works

    @Test
    void enterLetter() {
        String encryptedPhrase = letterGame.getCurrentCryptogram().getEncryptedPhrase();
        if (!encryptedPhrase.isEmpty() && encryptedPhrase.charAt(0) != ' ') {
            char encryptedChar = encryptedPhrase.charAt(0);
            letterGame.enterLetter(encryptedChar, 'X');

            Map<Object, Character> mapping = letterGame.getPlayergameMapping();
            assertEquals('X', mapping.get(encryptedChar));
        }
    }

    @Test
    void enterNumber() {
        String encryptedPhrase = numberGame.getCurrentCryptogram().getEncryptedPhrase();
        String[] tokens = encryptedPhrase.trim().split(" ");

        for (String token : tokens) {
            if (!token.isEmpty() && !token.equals("     ")) {
                try {
                    int encryptedNum = Integer.parseInt(token);
                    numberGame.enterLetter(encryptedNum, 'A');

                    Map<Object, Character> mapping = numberGame.getPlayergameMapping();
                    assertEquals('A', mapping.get(encryptedNum));
                    break;
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    @Test
    void undoLetter() {
        String encryptedPhrase = letterGame.getCurrentCryptogram().getEncryptedPhrase();
        if (!encryptedPhrase.isEmpty() && encryptedPhrase.charAt(0) != ' ') {
            char encryptedChar = encryptedPhrase.charAt(0);
            letterGame.enterLetter(encryptedChar, 'Z');
            assertEquals('Z', letterGame.getPlayergameMapping().get(encryptedChar));

            letterGame.undoLetter(encryptedChar);
            assertNull(letterGame.getPlayergameMapping().get(encryptedChar));
        }
    }

    @Test
    void undoNumber() {
        String encryptedPhrase = numberGame.getCurrentCryptogram().getEncryptedPhrase();
        String[] tokens = encryptedPhrase.trim().split(" ");

        for (String token : tokens) {
            if (!token.isEmpty() && !token.equals("     ")) {
                try {
                    int encryptedNum = Integer.parseInt(token);
                    numberGame.enterLetter(encryptedNum, 'A');
                    assertEquals('A', numberGame.getPlayergameMapping().get(encryptedNum));

                    numberGame.undoLetter(encryptedNum);
                    assertNull(numberGame.getPlayergameMapping().get(encryptedNum));
                    break;
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    @Test
    void gameSettersGetters() {
        Player newPlayer = new Player();
        newPlayer.updateUsername("NewPlayer");

        letterGame.setCurrentPlayer(newPlayer);
        assertEquals(newPlayer, letterGame.getCurrentPlayer());

        Map<Object, Character> newMapping = new HashMap<>();
        newMapping.put('A', 'B');
        letterGame.setPlayergameMapping(newMapping);
        assertEquals(newMapping, letterGame.getPlayergameMapping());

        letterGame.setCryptType("Number");
        assertEquals("Number", letterGame.getCryptType());

        LetterCryptogram newCrypto = new LetterCryptogram("test");
        letterGame.setCurrentCryptogram(newCrypto);
        assertEquals(newCrypto, letterGame.getCurrentCryptogram());
    }

    // player data tests

    @Test
    void addPlayer() {
        Player player1 = new Player();
        player1.updateUsername("Player1");
        Player player2 = new Player();
        player2.updateUsername("Player2");

        players.addPlayer(player1);
        players.addPlayer(player2);

        double[] accuracies = players.getAllPlayersAccuracies();
        assertEquals(2, accuracies.length);
    }

    @Test
    void findPlayer() {
        Player player = new Player();
        player.updateUsername("FindMe");
        players.addPlayer(player);

        Player found = players.findPlayer(player);
        assertNotNull(found);
        assertEquals("FindMe", found.getUsername());

        Player nonExistent = new Player();
        nonExistent.updateUsername("NonExistent");
        assertNull(players.findPlayer(nonExistent));
    }

    @Test
    void getAllAccuracies() {
        Player p1 = new Player();
        p1.updateUsername("P1");

        Player p2 = new Player();
        p2.updateUsername("P2");

        players.addPlayer(p1);
        players.addPlayer(p2);

        double[] accuracies = players.getAllPlayersAccuracies();
        assertEquals(2, accuracies.length);
        assertEquals(0.0, accuracies[0]);
        assertEquals(0.0, accuracies[1]);
    }

    @Test
    void getAllPlayed() {
        Player p1 = new Player();
        p1.incrementCryptogramsPlayed();
        p1.incrementCryptogramsPlayed();

        Player p2 = new Player();
        p2.incrementCryptogramsPlayed();

        players.addPlayer(p1);
        players.addPlayer(p2);

        int[] played = players.getAllPlayersCryptogramsPlayed();
        assertEquals(2, played.length);
        assertEquals(2, played[0]);
        assertEquals(1, played[1]);
    }

    @Test
    void getAllCompleted() {
        Player p1 = new Player();
        p1.incrementCryptogramsCompleted();

        Player p2 = new Player();
        p2.incrementCryptogramsCompleted();
        p2.incrementCryptogramsCompleted();

        players.addPlayer(p1);
        players.addPlayer(p2);

        int[] completed = players.getAllPlayersCryptogramsCompleted();
        assertEquals(2, completed.length);
        assertEquals(1, completed[0]);
        assertEquals(2, completed[1]);
    }

    @Test
    void clearPlayerData() {
        Player p1 = new Player();
        p1.updateUsername("ClearTest");
        players.addPlayer(p1);

        players.clearPlayerData();

        assertNotNull(players.getAllPlayersAccuracies());
    }

    // full game tests

    @Test
    void letterGame() {
        Game game = new Game(testPlayer, "Letter");
        Cryptogram crypto = game.getCurrentCryptogram();
        String solution = crypto.getPhrase();
        String encrypted = crypto.getEncryptedPhrase();

        assertNotNull(solution);
        assertNotNull(encrypted);
        assertEquals(solution.length(), encrypted.length());

        for (int i = 0; i < Math.min(3, encrypted.length()); i++) {
            if (encrypted.charAt(i) != ' ') {
                char guess = solution.charAt(i);
                char expectedGuess = Character.toUpperCase(guess);
                game.enterLetter(encrypted.charAt(i), guess);
                assertEquals(expectedGuess, game.getPlayergameMapping().get(encrypted.charAt(i)));
            }
        }
    }

    @Test
    void numberGame() {
        Game game = new Game(testPlayer, "Number");
        Cryptogram crypto = game.getCurrentCryptogram();
        String solution = crypto.getPhrase();
        String encryptedStr = crypto.getEncryptedPhrase();

        assertNotNull(solution);
        assertNotNull(encryptedStr);

        String[] tokens = encryptedStr.trim().split(" ");
        int solutionIndex = 0;

        for (String token : tokens) {
            if (!token.isEmpty() && !token.equals("     ")) {
                try {
                    int encryptedNum = Integer.parseInt(token);
                    while (solutionIndex < solution.length() && solution.charAt(solutionIndex) == ' ') {
                        solutionIndex++;
                    }
                    if (solutionIndex < solution.length()) {
                        char guess = solution.charAt(solutionIndex);
                        game.enterLetter(encryptedNum, guess);
                        assertEquals(guess, game.getPlayergameMapping().get(encryptedNum));
                        solutionIndex++;
                    }
                } catch (NumberFormatException e) {
                    // skips non numbers
                }
            }
        }
    }

    // tests if a letter is not equal to the non encrypted one

    @Test
    void noSelfMapping() {
        LetterCryptogram crypto = new LetterCryptogram("test");
        String encrypted = crypto.getEncryptedPhrase();

        for (int i = 0; i < encrypted.length(); i++) {
            char encryptedChar = encrypted.charAt(i);
            if (encryptedChar >= 'A' && encryptedChar <= 'Z') {
                char plain = crypto.getPlainLetter(encryptedChar);
                assertNotEquals(encryptedChar, plain);
            }
        }
    }

    // checks spaces are inserted correctly in number games

    @Test
    void numberSpace() {
        NumberCryptogram crypto = new NumberCryptogram("test");
        char spacePlain = crypto.getPlainLetter(999);
        assertEquals(' ', spacePlain);
    }
}