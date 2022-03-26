import java.io.IOException;
public interface IOStringStack {
    void close() throws IOException;

    boolean cacheEmpty();

    NTLMPair getCache();

    NTLMPair popCache() throws IOException;

}