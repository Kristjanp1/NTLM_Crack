import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;


public class NTLMCrack {

    public static final String DEFAULT_DELIMITER = "::";
    public static final String DEFAULT_DESTINATION = "./";
    public static final String DEFAULT_OUTPUT = "genTable.txt";
    public static final int DEFAULT_MAXTEMPFILES = 1024;
    public static final int DEFAULT_SEARCH_MAXBLOCKSIZE = 100000;

    public static void main(String[] args) {
        int maxTempFiles = DEFAULT_MAXTEMPFILES;
        int maxBlockSize = DEFAULT_SEARCH_MAXBLOCKSIZE;
        String delimiter = DEFAULT_DELIMITER;
        String destination = DEFAULT_DESTINATION;
        String outputName = DEFAULT_OUTPUT;
        String tableSource = null;
        String hashesSource = null;
        String wordListSource = null;
        boolean delimiterProvided = false;

        for (int param = 0; param < args.length; param++) {
            switch (args[param]) {
                case "-s", "--source" -> {
                    param++;
                    hashesSource = args[param];
                }
                case "-d", "--destination" -> {
                    param++;
                    destination = args[param];
                }
                case "-dl", "--delimiter" -> {
                    param++;
                    delimiter = args[param];
                    delimiterProvided = true;
                }
                case "-n", "--name" -> {
                    param++;
                    outputName = args[param];
                }
                case "-t", "--table" -> {
                    param++;
                    tableSource = args[param];
                }
                case "-w", "--wordlist" -> {
                    param++;
                    wordListSource = args[param];
                }
                case "-h", "--help" -> {
                    listParameters();
                }
                case "-mt", "--maxtemp" -> {
                    param++;
                    maxTempFiles = Integer.parseInt(args[param]);
                }
                case "-mb", "--maxblock" -> {
                    param++;
                    maxBlockSize = Integer.parseInt(args[param]);
                }

            }
        }
        if (hashesSource == null) {
            System.out.println("You need to provide a list of hashes and a wordlist.");
            listParameters();
            return;
        }
        if (wordListSource == null && tableSource == null) {
            System.out.println("You need to provide a list of hashes and a wordlist.");
            listParameters();
            return;
        }
        if (tableSource != null && !delimiterProvided) {
            System.out.println("When using a non-generated table, you must provide a delimiter.");
            listParameters();
            return;
        }
        File destinationFile = new File(destination);
        File outputFile = new File(destination + outputName);
        File hashFile = new File(hashesSource);
        File wordListFile = wordListSource != null ? new File(wordListSource) : null;
        File tableFile = tableSource != null ? new File(tableSource) : null;

        List<String>hashes = wordListToList(hashFile);

        if (wordListFile != null) {
            try {
                ExternalSort.sort(wordListFile, outputFile, delimiter, maxTempFiles, destinationFile);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        try {
            if (tableFile != null) {
                List<NTLMPair> matches = searchFile(tableFile, hashes, delimiter, maxBlockSize);
                for (NTLMPair pair : matches) {
                    System.out.println(pair.toString());
                }
            } else {
                List<NTLMPair> matches = searchFile(outputFile, hashes, delimiter, maxBlockSize);
                for (NTLMPair pair : matches) {
                    System.out.println(pair.toString());
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void listParameters() {
        System.out.println("List of parameters are:");
        System.out.println("-s or --source: \t Source for list of hashes");
        System.out.println("-w or --wordlist: \t Source for wordlist");
        System.out.println("-t or --table: \t Source for a custom table");
        System.out.println("-dl or --delimiter: \t Delimiter for custom table");
        System.out.println("-d or --destination: \t Destination for generated table");
        System.out.println("-n or --name: \t Name for generated table");
        System.out.println("-h or --help: \t Display this message");

    }

    public static List<NTLMPair> searchFile(File file, List<String> hashes, String delimiter, int maxBlockSize) throws IOException {
        System.out.println("Starting to bruteforce hashes...");
        List<NTLMPair> tempList = new ArrayList<>();
        List<NTLMPair> matches = new ArrayList<>();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader =  new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsolutePath())),8192*100);
            String line = "";
            int rows = 0;
            while (line != null) {
                while (rows < maxBlockSize && (line = bufferedReader.readLine()) != null) {
                    String[] pair = line.strip().split(delimiter);
                    tempList.add(new NTLMPair(pair));
                    rows++;
                }
                for (String hash : hashes) {
                    NTLMPair output = crack(tempList, hash);
                    if (output != null) {
                        matches.add(output);
                    }
                }
                tempList.clear();
                rows = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            bufferedReader.close();
            if (tempList.size() > 0) {
                for (String hash : hashes) {
                    NTLMPair output = crack(tempList, hash);
                    if (output != null) {
                        matches.add(output);
                    }
                }
            }
        }
        return matches;
    }

    public static NTLMPair crack(List<NTLMPair> pairList, String hash) {
        int atIndex = search(pairList, hash);
        return atIndex != -1 ? pairList.get(atIndex) : null;
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
