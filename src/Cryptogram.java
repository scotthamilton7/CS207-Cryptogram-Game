import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public abstract class Cryptogram<T> {

    protected String phrase;
    protected T[] encrypted;
    protected Map<T, Character> cryptogramAlphabet;
    protected Map<Character, T> encryptionKey;

    public Map<T, Integer> getFrequencies() {
        Map<T, Integer> freq = new HashMap<>();
        for (T x : encrypted) {
            freq.put(x, freq.getOrDefault(x, 0) + 1);
        }
        return freq;
    }

    public abstract char getPlainLetter(T encrypted);

    public void loadPhrase() {
        try (Scanner reader = new Scanner(new File("phrases.txt"))) {
            // Load phrases from file
            ArrayList<String> phrases = new ArrayList<>();
            while (reader.hasNextLine()) {
                phrases.add(reader.nextLine());
            }

            // Select random phrase from options
            int i = new Random().nextInt(phrases.size());
            phrase = phrases.get(i);
        }
        catch (Exception e) {
            // Error loading file
            phrase = "";
            System.out.println("Error loading phrases file");
        }
    }

    public void encryptPhrase() {
        int i = 0;
        for (char c : phrase.toUpperCase().toCharArray()) {
            encrypted[i++] = encryptionKey.get(c);
        }
    }

    public String getPhrase() {
        return phrase;
    }

    public abstract String getEncryptedPhrase();

}
