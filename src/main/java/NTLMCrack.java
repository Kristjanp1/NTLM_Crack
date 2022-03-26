import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class NTLMCrack {

    public static void main(String[] args) {

        File hashes = new File("src/ntlm_rasid.txt");
        File wordList = new File("src/kaonashi14M2.txt");
        try {
            ExternalSort.sort(wordList);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        List<String> hashList = wordListToList(hashes);



//        wordListToTable(wordList, "table.txt", "::");
//        List<NTLMPair> pairList = tableToList(new File("table.txt"), "::");
//        pairList.sort(new NtlmComparator());
//
//        for (String hash : hashList) {
//            String out = crack(pairList, hash);
//            if(out != null){
//                System.out.println("MATCH: \t"+hash+":"+out);
//            }
//        }


    }

    public static String crack(List<NTLMPair> pairList, String hash) {
        int atIndex = search(pairList, hash);
        return atIndex != -1 ? pairList.get(atIndex).word : null;
    }

    public static int search(List<NTLMPair> pairList, String hash) {
        int l = 0, r = pairList.size() - 1;
        while (l <= r) {
            int m = l + (r - l) / 2;
            int res = hash.compareTo(pairList.get(m).hash);
            if (res == 0)
                return m;
            if (res > 0)
                l = m + 1;
            else
                r = m - 1;
        }
        return -1;
    }

    public static void wordListToTable(File wordlist, String outputName, String delimiter) {
        BufferedReader reader;
        FileWriter fw;
        try {
            fw = new FileWriter(new File(outputName).getAbsolutePath());
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(wordlist.getAbsolutePath())));
            for (String line; (line = reader.readLine()) != null; ) {
                String hash = NTLMUtility.encode(line);
                fw.write(line + delimiter + hash);
                if (hash.equals("")) {
                    System.out.println(line);
                }
                fw.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<NTLMPair> tableToList(File table, String delimiter) {
        BufferedReader reader;
        List<NTLMPair> pairList = new ArrayList<>();
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(table.getAbsolutePath())));
            for (String line; (line = reader.readLine()) != null; ) {
                line = line.strip();
                String[] split = line.split(delimiter);
                NTLMPair pair = new NTLMPair(split[0], split[1]);
                if (!pair.hash.equals("")) {
                    pairList.add(pair);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pairList;
    }

    public static List<String> wordListToList(File wordlist) {
        BufferedReader reader;
        List<String> wordList = new ArrayList<>();
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(wordlist.getAbsolutePath())));
            for (String line; (line = reader.readLine()) != null; ) {
                line = line.strip();
                wordList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wordList;
    }

}
