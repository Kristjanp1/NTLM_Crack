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

    public boolean cacheEmpty() {
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
            String[] lines = line.strip().split(this.delimiter);
            try {
                this.cache = new NTLMPair(lines[0], lines[1]);
            }catch (IndexOutOfBoundsException e){
                this.cache = new NTLMPair("null", "null"); //In case the file contains a bad row
            }
        } else {
            this.cache = null;
        }


    }

    private BufferedReader fbr;
    private String delimiter;

    private NTLMPair cache;

}