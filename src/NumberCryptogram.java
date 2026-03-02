import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class NumberCryptogram extends Cryptogram<Integer> {

    public NumberCryptogram() {
        cryptogramAlphabet = new HashMap<Integer, Character>();
        generateMapping();
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

        // Store mapping of encrypted -> alphabet
        for (int i = 0; i < alphabet.size(); i++) {
            cryptogramAlphabet.put(encrypted.get(i), alphabet.get(i));
        }
    }

}
