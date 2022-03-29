import java.util.Objects;

public class NTLMPair {
    String word;
    String hash;

    @Override
    public String toString() {
        return word + ":"+ hash;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NTLMPair ntlmPair = (NTLMPair) o;
        return word.equals(ntlmPair.word) && hash.equals(ntlmPair.hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word, hash);
    }

    public NTLMPair(String word, String hash) {
        this.word = word;
        this.hash = hash;
    }
    public NTLMPair(String[] pair) {
        this.word = pair[0];
        this.hash = pair[1];
    }
}
