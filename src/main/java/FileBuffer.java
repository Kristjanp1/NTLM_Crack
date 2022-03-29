import java.io.BufferedReader;
import java.io.IOException;

public final class FileBuffer implements IOStringStack {
    public FileBuffer(BufferedReader r, String delimiter) throws IOException {
        this.delimiter = delimiter;
        this.fbr = r;
        reload();
    }

    public void close() throws IOException {
        this.fbr.close();
    }

    public boolean isCacheEmpty() {
        return this.cache == null;
    }

    public NTLMPair getCache() {
        return this.cache;
    }

    public NTLMPair popCache() throws IOException {
        NTLMPair answer = this.cache;
        reload();
        return answer;
    }

    private void reload() throws IOException {
        String line = this.fbr.readLine();
        if (line != null) {
            String[] lines = line.trim().split(this.delimiter);
            //In the case that the wordlist contains a bad row, skip it.
            if(lines.length == 2) {
                this.cache = new NTLMPair(lines[0], lines[1]);
            }else{
                reload();
            }

        } else {
            this.cache = null;
        }


    }

    private final BufferedReader fbr;
    private final String delimiter;

    private NTLMPair cache;

}