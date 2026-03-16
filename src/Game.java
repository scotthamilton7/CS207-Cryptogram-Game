import javax.swing.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 *  Current Issues:
 *  Seems to work fine for Letter Cryptograms, however Number Cryptograms dont work -
 *  Because when mapping a guess, the encrypted character just takes the first number (E.g from 14 it just takes 1)
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
    }

    public Game(Player p) {
        // Still not sure what this should do?
    }

    public void generateCryptogram() {
        // This method might be redundant since when a Cryptogram object is made it generates a Cryptogram itself
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
        playergameMapping.put(encryptedChar, guessedChar);//Maps encrypted char to guessed char 
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

    public boolean hasWon() {
        // Check if the player won, not sure what to do after win?
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
        return true;
    }


}
