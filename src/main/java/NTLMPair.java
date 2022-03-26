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

    public NTLMPair(String word, String hash) {
        this.word = word;
        this.hash = hash;
    }
}
