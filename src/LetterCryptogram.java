import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class LetterCryptogram extends Cryptogram<Character> {

    public LetterCryptogram() {
        cryptogramAlphabet = new HashMap<>();
        encryptionKey = new HashMap<>();

        loadPhrase();
        encrypted = new Character[phrase.length()];

        generateMapping();
        encryptPhrase();
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

        // Add spaces to ensure they always map to each other
        alphabet.add(' ');
        encrypted.add(' ');

        // Store mapping of encrypted -> alphabet
        for (int i = 0; i < alphabet.size(); i++) {
            cryptogramAlphabet.put(encrypted.get(i), alphabet.get(i));
            encryptionKey.put(alphabet.get(i), encrypted.get(i));
        }
    }

    public String getEncryptedPhrase() {
        StringBuilder encryptedPhrase = new StringBuilder();
        for (int i = 0; i < encrypted.length; i++) {
            encryptedPhrase.append(encrypted[i]);
        }
        return encryptedPhrase.toString();
    }

}
