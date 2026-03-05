import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class NumberCryptogram extends Cryptogram<Integer> {

    public NumberCryptogram(String file) {
        cryptogramAlphabet = new HashMap<>();
        encryptionKey = new HashMap<>();

        generateMapping();
        loadPhrase(file);

        encrypted = new Integer[phrase.length()];
        encryptPhrase();
    }

    @Override
    public char getPlainLetter(Integer cryptoValue) {
        return cryptogramAlphabet.get(cryptoValue);
    }

    private void generateMapping() {
        ArrayList<Character> alphabet = new ArrayList<>();
        ArrayList<Integer> encrypted = new ArrayList<>();

        // Fill regular alphabet list
        for (char c = 'A'; c <= 'Z'; c++) {
            alphabet.add(c);
        }

        // Fill integer list
        for (int i = 0; i < alphabet.size(); i++) {
            encrypted.add(i+1);
        }

        // Shuffle integer list
        Collections.shuffle(encrypted);

        // Add spaces to ensure they always map to each other
        alphabet.add(' ');
        encrypted.add(999);

        // Store mapping of encrypted -> alphabet
        for (int i = 0; i < alphabet.size(); i++) {
            cryptogramAlphabet.put(encrypted.get(i), alphabet.get(i));
            encryptionKey.put(alphabet.get(i), encrypted.get(i));
        }
    }

    public String getEncryptedPhrase() {
        StringBuilder encryptedPhrase = new StringBuilder();
        for (int i = 0; i < encrypted.length; i++) {
            if (encrypted[i].equals(999)) {
                // Space
                encryptedPhrase.append("     ");
            }
            else {
                encryptedPhrase.append(encrypted[i]);
                encryptedPhrase.append(" ");
            }
        }
        return encryptedPhrase.toString();
    }

}
