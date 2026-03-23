import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private Cryptogram currentCryptogram;

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
    }

    // Method to undo guessed letter for specified encrypted char
    public void undoLetter(char encryptedChar) {
        encryptedChar = Character.toUpperCase(encryptedChar);
        playergameMapping.remove(encryptedChar);
    }

    // method overload to handle int input
    public void undoLetter(int encryptedChar) {
        playergameMapping.remove(encryptedChar);
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
    public void saveGame() {
        try {
            FileWriter fw = new FileWriter(gameDataFile);
            //clearPlayerData();

            fw.write(currentPlayer.getUsername() + ",");
            fw.write(currentCryptogram.getPhrase() + ",");
            for (Map.Entry<Object, Character> entry : playergameMapping.entrySet()) {
                fw.write(entry.getKey().toString() + "-" + entry.getValue().toString() + " ");
            }
            fw.write(",\n");
            fw.close();
        } catch (IOException e) {
            // Print error message if write operation fails
            System.out.println("Error saving game data: " + e.getMessage());
        }
    }

    // load game by user playing it (by currentPlayer)
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
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts[0].equals(username)) {
                String[] mappingParts = parts[1].split(" ");
                for (String mappingPart : mappingParts) {
                    String[] gameMappingParts = mappingPart.split("-");
                    playergameMapping.put(gameMappingParts[0].charAt(0), gameMappingParts[1].charAt(0));
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


}
