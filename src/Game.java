import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Current Issues:
 *
 */

public class Game {

    private Player currentPlayer;
    // @Object - The Character or Int to replace, @Character - The Guessed Character
    private Map<Object, Character> playergameMapping;
    private String cryptType;
    private Cryptogram<?> currentCryptogram;

    // could maybe move this to global scope
    private String phrasesFile = "phrases.txt";
    private String gameDataFile = "src/gameData.csv";

    public Game(Player p, String cryptType) {
        this.currentPlayer = p;
        this.cryptType = cryptType;
        this.playergameMapping = new HashMap<Object, Character>();

        if (cryptType.equals("Letter")) {
            currentCryptogram = new LetterCryptogram(phrasesFile);
        } else if (cryptType.equals("Number")) {
            currentCryptogram = new NumberCryptogram(phrasesFile);
        } else {
            //creates letter cryptogram by default, not sure if this is what we want
            currentCryptogram = new LetterCryptogram(phrasesFile);
        }

        // New game created
        currentPlayer.incrementCryptogramsPlayed();
    }

    public Game(Player p) {
        this.currentPlayer = p;
        this.playergameMapping = new HashMap<Object, Character>();
    }

    public void generateCryptogram() {

    }

    // Method to enter a guessed letter for an encrypted character
    public void enterLetter(Object encryptedChar, char guessedChar) { 
        if (playergameMapping.containsKey(encryptedChar)) {
            System.out.println("Already guessed letter");
        }
        else {
            try {
                if (Character.isLetter((Character) encryptedChar)) {
                    encryptedChar = Character.toUpperCase((Character)encryptedChar);//Converts encrypted character to uppercase to match with guess
                }
                if (Character.isLetter(guessedChar)) {
                    guessedChar = Character.toUpperCase(guessedChar);//Converts guessed character to uppercase to match with encrypted char
                }
            }
            catch (Exception e) {
                // Integer passed in
            }

            //Maps encrypted char to guessed char
            playergameMapping.put(encryptedChar, guessedChar);

            // Increment guesses made
            currentPlayer.incrementTotalGuesses();

            // Save game
            saveGame();
        }
    }

    // Method to undo guessed letter for specified encrypted char
    public void undoLetter(char encryptedChar) {
        if (playergameMapping.containsKey(encryptedChar)) {
            encryptedChar = Character.toUpperCase(encryptedChar);
            playergameMapping.remove(encryptedChar);

            // Save game
            saveGame();
        }
        else {
            System.out.println("Invalid letter");
        }
    }

    // method overload to handle int input
    public void undoLetter(int encryptedChar) {
        if (playergameMapping.containsKey(encryptedChar)) {
            playergameMapping.remove(encryptedChar);

            // Save game
            saveGame();
        }
        else {
            System.out.println("Invalid letter");
        }
    }

    // should probably be changed to return the solution when the UI is setup
    public void showSolution() {
        System.out.println(currentCryptogram.getPhrase());
    }

    // Get and set methods just incase, could also just make fields protected

    public Player getCurrentPlayer() { return currentPlayer; }
    public Map<Object, Character> getPlayergameMapping() { return playergameMapping; }
    public String getCryptType() { return cryptType; }
    public Cryptogram getCurrentCryptogram() { return currentCryptogram; }

    public void setCurrentPlayer(Player p) { currentPlayer = p; }
    public void setPlayergameMapping(Map<Object, Character> p) { playergameMapping = p; }
    public void setCryptType(String c) { cryptType = c; }
    public void setCurrentCryptogram(Cryptogram c) { currentCryptogram = c; }

    // save current game, save it in place if currentPlayer has already got a game saved
    // saves game data in format "{username},{unencryptedphrase},{encryptedLetter}-{guessedletter} ...
    // should maybe include full encrypted phrase aswell, easy to add
//    public void saveGame() {
//        try {
//            FileWriter fw = new FileWriter(gameDataFile);
//            //clearPlayerData();
//
//            fw.write(currentPlayer.getUsername() + ",");
//            fw.write(currentCryptogram.getPhrase() + ",");
//            for (Map.Entry<Object, Character> entry : playergameMapping.entrySet()) {
//                fw.write(entry.getKey().toString() + "-" + entry.getValue().toString() + " ");
//            }
//            fw.write(",\n");
//            fw.close();
//        } catch (IOException e) {
//            // Print error message if write operation fails
//            System.out.println("Error saving game data: " + e.getMessage());
//        }
//    }

    // load game by user playing it (by currentPlayer)
//    public void loadGame() {
//        String username = currentPlayer.getUsername();
//        List<String> lines;
//        try {
//            lines = Files.readAllLines(Path.of(gameDataFile));
//        } catch (IOException e) {
//            System.out.println("Game data file not found");
//            return;
//        }
//        // Splits each line in the saved format, only gets data for matching username
//        for (String line : lines) {
//            String[] parts = line.split(",");
//            if (parts[0].equals(username)) {
//                String[] mappingParts = parts[1].split(" ");
//                for (String mappingPart : mappingParts) {
//                    String[] gameMappingParts = mappingPart.split("-");
//                    playergameMapping.put(gameMappingParts[0].charAt(0), gameMappingParts[1].charAt(0));
//                }
//            }
//        }
//    }

    // Save in following format
    // "{username};{type};{plainPhrase};{encryptedPhrase};{[encryptedLetter-guessedLetter,...]};{[encryptedLetter-plainLetter,...]}"
    public void saveGame() {
        try {
            // Read existing lines
            List<String> lines = new ArrayList<>();
            try {
                lines = Files.readAllLines(Path.of(gameDataFile));
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileWriter fw = new FileWriter(gameDataFile);
            //clearPlayerData();

            // Save existing lines
            for (String line : lines) {
                if (line.startsWith(currentPlayer.getUsername())) continue;
                fw.write(line + "\n");
            }

            fw.write(currentPlayer.getUsername() + ";");
            fw.write(cryptType + ";");
            fw.write(currentCryptogram.getPhrase() + ";");
            fw.write(currentCryptogram.getEncryptedPhrase() + ";");
            // Write mapping of encrypted-plain guesses
            for (var entry : playergameMapping.entrySet()) {
                fw.write(entry.getKey().toString() + "-" + entry.getValue().toString() + ",");
            }
            fw.write(";");
            // Write current cryptogram alphabet
            for (var entry : currentCryptogram.cryptogramAlphabet.entrySet()) {
                fw.write(entry.getKey().toString() + "-" + entry.getValue().toString() + ",");
            }

            fw.write(",\n");
            fw.close();
        } catch (IOException e) {
            // Print error message if write operation fails
            System.out.println("Error saving game data: " + e.getMessage());
        }
    }

    // Loads data in following format
    // "{username};{type};{plainPhrase};{encryptedPhrase};{[encryptedLetter-guessedLetter,...]};{[encryptedLetter-plainLetter,...]}"
    public void loadGame() {
        String username = currentPlayer.getUsername();
        List<String> lines;
        try {
            lines = Files.readAllLines(Path.of(gameDataFile));
        } catch (IOException e) {
            System.out.println("Game data file not found");
            return;
        }

        // Splits each line in the saved format, only gets data for matching username
        // parts[0] = username
        // parts[1] = type
        // parts[2] = plain phrase
        // parts[3] = encrypted phrase
        // parts[4] = unsplit game mapping
        // parts[5] = unsplit alphabet

        for (String line : lines) {
            String[] parts = line.split(";");
            if (parts[0].equals(username)) {
                // User data exists, load it
                cryptType = parts[1];
                String plainPhrase = parts[2];
                String encryptedPhrase = parts[3];

                String[] mappingParts = parts[4].split(",");
                for (String mappingPart : mappingParts) {
                    String[] gameMappingParts = mappingPart.split("-");
                    playergameMapping.put(gameMappingParts[0].charAt(0), gameMappingParts[1].charAt(0));
                }

                if (cryptType.equals("Letter")) {
                    HashMap<Character, Character> tempAlphabet = new HashMap<>();
                    HashMap<Character, Character> tempEncryptionKey = new HashMap<>();
                    String[] alphabetParts = parts[5].split(",");
                    for (String alphabetPart : alphabetParts) {
                        String[] cryptogramAlphabetParts = alphabetPart.split("-");
                        tempAlphabet.put(cryptogramAlphabetParts[0].charAt(0), cryptogramAlphabetParts[1].charAt(0));
                        tempEncryptionKey.put(cryptogramAlphabetParts[1].charAt(0), cryptogramAlphabetParts[0].charAt(0));
                    }

                    Character[] encryptedAsArray = new Character[encryptedPhrase.length()];
                    for (int i = 0; i < encryptedPhrase.length(); i++) {
                        encryptedAsArray[i] = Character.toUpperCase(encryptedPhrase.charAt(i));
                    }

                    currentCryptogram = new LetterCryptogram(tempAlphabet, tempEncryptionKey, plainPhrase, encryptedAsArray);
                }

                else if (cryptType.equals("Number")) {
                    HashMap<Integer, Character> tempAlphabet = new HashMap<>();
                    HashMap<Character, Integer> tempEncryptionKey = new HashMap<>();
                    String[] alphabetParts = parts[5].split(",");
                    for (String alphabetPart : alphabetParts) {
                        String[] cryptogramAlphabetParts = alphabetPart.split("-");
                        tempAlphabet.put(Integer.parseInt(String.valueOf(cryptogramAlphabetParts[0].charAt(0))), cryptogramAlphabetParts[1].charAt(0));
                        tempEncryptionKey.put(cryptogramAlphabetParts[1].charAt(0), Integer.parseInt(String.valueOf(cryptogramAlphabetParts[0].charAt(0))));
                    }

                    Integer[] encryptedAsArray = new Integer[encryptedPhrase.length()];
                    for (int i = 0; i < encryptedPhrase.length(); i++) {
                        encryptedAsArray[i] = Integer.parseInt(String.valueOf(Character.toUpperCase(encryptedPhrase.charAt(i))));
                    }

                    currentCryptogram = new NumberCryptogram(tempAlphabet, tempEncryptionKey, plainPhrase, encryptedAsArray);
                }

                else {
                    // Invalid type
                    System.out.println("Could not load cryptogram, invalid save game data");
                }
            }
        }
    }

    public boolean hasWon() {
        String SolutionPhrase = getCurrentCryptogram().getPhrase(); // Stores unencrypted phrase
        String EncryptedPhrase = getCurrentCryptogram().getEncryptedPhrase(); // Stores encrypted phrase
        Map<Object, Character> guesses = getPlayergameMapping();

        if (getCryptType().equals("Number")) {
            String[] tokens = EncryptedPhrase.trim().split(" ");
            int phraseIndex = 0;
            for (int i = 0; i < tokens.length; i++){
                String currentToken = tokens[i];
                if (currentToken.equals("")) continue; //skips the empty tokens

                int numEncrypt;
                try{
                    numEncrypt = Integer.parseInt(currentToken);
                } catch (NumberFormatException e ){
                    continue; // skips if not a number
                }

                // this is used to move past empty spaces in phrase
                while (phraseIndex < SolutionPhrase.length() && SolutionPhrase.charAt(phraseIndex) == ' ') {
                    phraseIndex++;
                }


                Character guess = guesses.get(numEncrypt);
                //change to uppercase to allow for comparison to identify if correct or not
                if (guess == null || Character.toUpperCase(guess) != Character.toUpperCase(SolutionPhrase.charAt(phraseIndex))) {
                    return false; // not correct guess yet
                }
                phraseIndex++;
            }
            // Sets guesses to answer and goes until player gets correct answer
        }else if (getCryptType().equals("Letter")) {
            String removeSpacePhrase = EncryptedPhrase.replace(" ", ""); // removes the spaces in phrase
            int phraseIndex = 0;
            for (int i = 0; i < removeSpacePhrase.length(); i++) {
                char encryptChar = removeSpacePhrase.charAt(i);
                Character guess = guesses.get(encryptChar);

                // this is used to move past empty spaces in phrase
                while (phraseIndex < SolutionPhrase.length() && SolutionPhrase.charAt(phraseIndex) == ' ') {
                    phraseIndex++;
                }

                //change to uppercase to allow for comparison to identify if correct or not
                if (guess == null || Character.toUpperCase(guess) != Character.toUpperCase(SolutionPhrase.charAt(phraseIndex))) {
                    return false; // not correct guess yet
                }
                phraseIndex++;
            }
        }

        // Player has won
        currentPlayer.incrementCryptogramsCompleted();
        return true;
    }

    public void revealSolution() {
        // Clear any guesses made in case they are incorrect
        playergameMapping.clear();

        if (currentCryptogram.cryptogramAlphabet != null) {
            playergameMapping.putAll(currentCryptogram.cryptogramAlphabet);
        }

        // Save current game state
        saveGame();

        // Remove win from player as they did not actually complete it
        currentPlayer.decrementCryptogramsCompleted();
    }

    public void getHint() {
        String encryptedPhrase = currentCryptogram.getEncryptedPhrase();

        List<Object> validHintKeys = new ArrayList<>();

        for (Object key : currentCryptogram.cryptogramAlphabet.keySet()) {
            String keyString = key.toString();

            if (encryptedPhrase.contains(keyString) && !playergameMapping.containsKey(key)) {
                validHintKeys.add(key);
            }
        }

        if (validHintKeys.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Error displaying hint. You may have already guessed every letter!");
            return;
        }

        int randomIndex = (int) (Math.random() * validHintKeys.size());
        Object hintKey = validHintKeys.get(randomIndex);
        Character correctChar = (Character) currentCryptogram.cryptogramAlphabet.get(hintKey);

        playergameMapping.put(hintKey, correctChar);

        saveGame();
    }


}
