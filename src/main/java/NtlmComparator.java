import java.util.Comparator;

public class NtlmComparator implements Comparator<NTLMPair> {
    @Override
    public int compare(NTLMPair o1, NTLMPair o2) {
        return o1.hash.compareTo(o2.hash);
    }
}
