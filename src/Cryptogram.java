import java.util.HashMap;
import java.util.Map;

public abstract class Cryptogram<T> {

    private String phrase;
    protected Map<T, Character> cryptogramAlphabet;

    public Map<Character, Integer> getFrequencies() {
        Map<Character, Integer> freq = new HashMap<>();
        for (char c : this.phrase.toUpperCase().toCharArray()) {
            freq.put(c, freq.getOrDefault(c, 0) + 1);
        }
        return freq;
    }

    public abstract char getPlainLetter(T encrypted);

}
