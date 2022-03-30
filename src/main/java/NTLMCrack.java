import java.awt.*;
import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class NTLMCrack {

    public static final String DEFAULT_DELIMITER = "::";
    public static final String DEFAULT_DESTINATION = "./";
    public static final String DEFAULT_OUTPUT = "genTable.txt";
    public static final int DEFAULT_MAXTEMPFILES = 1024;
    public static final int DEFAULT_SEARCH_MAXBLOCKSIZE = 90000;
    public static long totalLines = 0;

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
                    return;
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

        List<String> hashes = wordListToList(hashFile);
        int hashesCount = hashes.size();

        if (wordListFile != null && tableFile ==  null) {
            try {
                ExternalSort.sort(wordListFile, outputFile, delimiter, maxTempFiles, destinationFile);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        long start = System.nanoTime();

        try {
            if (tableFile != null) {
                HashSet<NTLMPair> matches = searchFile(tableFile, hashes, delimiter, maxBlockSize);
                System.out.println("*".repeat(50));
//                for (NTLMPair pair : matches) {
//                    System.out.println(pair.toString());
//                }
                System.out.println("*".repeat(50));
                System.out.println("Passwords broken: " + matches.size());
                System.out.println("Success percentage: " + (double) matches.size() / hashesCount);
            } else {
                HashSet<NTLMPair> matches = searchFile(outputFile, hashes, delimiter, maxBlockSize);
                System.out.println("*".repeat(50));
//                for (NTLMPair pair : matches) {
//                    System.out.println(pair.toString());
//                }
                System.out.println("*".repeat(50));
                System.out.println("Passwords broken: " + matches.size());
                System.out.println("Success percentage: " + (double) matches.size() / hashesCount);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Time elapsed: " + TimeUnit.SECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS) + " seconds");
        System.out.println("Total hashes checked: " + totalLines);

    }

    public static void listParameters() {
        System.out.println("List of parameters are:");
        System.out.println("-s or --source: \t Source for list of hashes");
        System.out.println("-w or --wordlist: \t Source for wordlist");
        System.out.println("-t or --table: \t Source for a custom table");
        System.out.println("-dl or --delimiter: \t Delimiter for custom table");
        System.out.println("-d or --destination: \t Destination for generated table");
        System.out.println("-n or --name: \t Name for generated table");
        System.out.println("-mb or --maxtemp: \t Amount of allowed temporary files");
        System.out.println("-mt or --maxblock: \t Amount of rows read into memory when breaking hashes");
        System.out.println("-h or --help: \t Display this message");

    }

    public static HashSet<NTLMPair> searchFile(File file, List<String> hashes, String delimiter, int maxBlockSize) throws IOException {
        System.out.println("Starting to bruteforce hashes...");
        List<NTLMPair> tempList = new ArrayList<>();
        HashSet<NTLMPair> matches = new HashSet<>();
        int rows = 0;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file.getAbsolutePath()), 512)) {
            String line = "";
            while (line != null) {
                while (rows < maxBlockSize && (line = bufferedReader.readLine()) != null) {
                    StringTokenizer tokenizer = new StringTokenizer(line.trim(), delimiter);
                    try {
                        tempList.add(new NTLMPair(tokenizer.nextToken(), tokenizer.nextToken()));
                    } catch (NoSuchElementException e) {
                        continue;
                    }
                    rows++;
                }
                for (int i = 0; i < hashes.size(); i++) {
                    NTLMPair output = crack(tempList, hashes.get(i));
                    if (output != null) {
                        matches.add(output);
                        hashes.remove(i);
                    }
                }
                tempList.clear();
                totalLines += rows;
                rows = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (tempList.size() > 0) {
                for (String hash : hashes) {
                    NTLMPair output = crack(tempList, hash);
                    if (output != null) {
                        matches.add(output);
                        hashes.remove(output.hash);
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
        String startHash = pairList.get(0).hash;
        String endHash = pairList.get(pairList.size()-1).hash;
        if(outsideRange(startHash,endHash,hash)){
            return -1;
        }

        int l = 0, r = pairList.size() - 1;
        while (l <= r) {
            int m = l + (r - l) / 2;
            int res = hash.compareTo(pairList.get(m).hash);
            if (res == 0){
                return m;}
            if (res > 0)
                l = m + 1;
            else
                r = m - 1;
        }

        return -1;
    }

    public static boolean outsideRange(String start, String end, String in ){
        int overEnd = in.compareTo(end);
        int belowStart = in.compareTo(start);
        return (overEnd > 0) && (belowStart < 0);
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
