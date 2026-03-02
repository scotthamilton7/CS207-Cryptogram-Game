import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class LetterCryptogram extends Cryptogram<Character> {

    public LetterCryptogram() {
        cryptogramAlphabet = new HashMap<Character, Character>();
        generateMapping();
    }

    @Override
    public char getPlainLetter(Character cryptoLetter) {
        return cryptogramAlphabet.get(Character.toUpperCase(cryptoLetter));
    }

    private void generateMapping() {
        ArrayList<Character> alphabet = new ArrayList<>();
        ArrayList<Character> encrypted = new ArrayList<>();
        boolean valid = false;

        // Fill alphabet lists
        for (char c = 'A'; c <= 'Z'; c++) {
            alphabet.add(c);
            encrypted.add(c);
        }

        // Check if any letters have been mapped to themselves
        // If they have reshuffle encrypted alphabet
        while (!valid) {
            Collections.shuffle(encrypted);
            valid = true;
            for (int i = 0; i < alphabet.size(); i++) {
                if (alphabet.get(i).equals(encrypted.get(i))) {
                    valid = false;
                    break;
                }
            }
        }

        // Store mapping of encrypted -> alphabet
        for (int i = 0; i < alphabet.size(); i++) {
            cryptogramAlphabet.put(encrypted.get(i), alphabet.get(i));
        }
    }

}
