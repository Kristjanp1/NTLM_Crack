import java.io.IOException;
public interface IOStringStack {
    void close() throws IOException;

    boolean isCacheEmpty();

    NTLMPair getCache();

    NTLMPair popCache() throws IOException;

}