import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class ExternalSort {


    public static void sort(File file, File output, String delimiter, int maxTempFiles, File destination) throws IOException {
        System.out.println("Starting external merge.");
        long fileSize = getFileSize(file);
        long maxBlockSize = estimateBestSizeOfBlocks(fileSize, maxTempFiles, estimateAvailableMemory());
        List<File> sortedTempFiles = distribute(file, maxBlockSize, delimiter, destination);
        initializeMerge(sortedTempFiles, output, delimiter);
    }

    public static void initializeMerge(List<File> sortedTempFiles, File outputFile, String delimiter) throws IOException {
        System.out.println("Starting to merge smaller files.");
        ArrayList<IOStringStack> buffers = new ArrayList<>();
        for (File file : sortedTempFiles) {
            InputStream inputStream = new FileInputStream(file);
            BufferedReader bufferedReader;
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            FileBuffer buffer = new FileBuffer(bufferedReader, delimiter);
            buffers.add(buffer);
        }
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile, false)));
        merge(bufferedWriter, buffers, delimiter);
        for (File f : sortedTempFiles) {
            f.deleteOnExit();
        }
    }

    public static void merge(BufferedWriter bufferedWriter, List<IOStringStack> buffers, String delimiter) throws IOException {
        Comparator<NTLMPair> comp = new NtlmComparator();
        PriorityQueue<IOStringStack> priorityQueue = new PriorityQueue<>(11, (o1, o2) -> comp.compare(o1.getCache(), o2.getCache()));
        for (IOStringStack buffer : buffers) {
            if (!buffer.isCacheEmpty()) {
                priorityQueue.add(buffer);
            }
        }
        try {
            while (priorityQueue.size() > 0) {
                IOStringStack priorityBuffer = priorityQueue.poll();
                NTLMPair pair = priorityBuffer.popCache();
                bufferedWriter.write(pair.word + delimiter + pair.hash);
                bufferedWriter.newLine();
                if (priorityBuffer.isCacheEmpty()) {
                    priorityBuffer.close();
                } else {
                    priorityQueue.add(priorityBuffer);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            bufferedWriter.close();
            for (IOStringStack buffer : priorityQueue) {
                buffer.close();
            }
            System.out.println("Merging done.");
        }
    }


    public static List<File> distribute(File file, long maxBlockSize, String delimiter, File directory) throws IOException {
        System.out.println("Splitting given wordlist into smaller sorted files...");
        List<File> files = new ArrayList<>();
        BufferedReader bufferedReader = null;
        List<NTLMPair> temp = new ArrayList<>();
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsolutePath())));
            String line = "";
            while (line != null) {
                long currentBlockSize = 0;
                while (currentBlockSize < maxBlockSize && (line = bufferedReader.readLine()) != null) {
                    line = line.strip();
                    String hash = NTLMUtility.encode(line);
                    temp.add(new NTLMPair(line, hash));
                    currentBlockSize += getStringSizeinBytes(line + delimiter + hash);
                }
                files.add(saveToTempFiles(temp, directory, delimiter));
                temp.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (temp.size() > 0) {
                files.add(saveToTempFiles(temp, directory, delimiter));
                temp.clear();
            }
        } finally {
            bufferedReader.close();
        }
        return files;
    }

    public static File saveToTempFiles(List<NTLMPair> temp, File targetDirectory, String delimiter) throws IOException {
        String fileTemplate = "temp-file";
        temp.sort(new NtlmComparator());
        File tempFile = File.createTempFile(fileTemplate, null, targetDirectory);
        try {
            tempFile.deleteOnExit();
            OutputStream out = new FileOutputStream(tempFile);

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(out),8192*10);
            for (NTLMPair pair : temp) {
                bufferedWriter.write(pair.word + delimiter + pair.hash);
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Created:\t" + tempFile.getName());
        return tempFile;
    }


    public static long getFileSize(File file) {
        long fileSize = 0L;
        try {
            fileSize = Files.size(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileSize;
    }

    public static long estimateAvailableMemory() {
        System.gc();
        Runtime r = Runtime.getRuntime();
        long allocatedMemory = r.totalMemory() - r.freeMemory();
        return r.maxMemory() - allocatedMemory;
    }

    public static long estimateBestSizeOfBlocks(final long sizeoffile,
                                                final int maxtmpfiles, final long maxMemory) {
        System.out.println("Estimating block size.");
        long blocksize = sizeoffile / maxtmpfiles + (sizeoffile % maxtmpfiles == 0 ? 0 : 1);
        if (blocksize < maxMemory / 2) {
            blocksize = maxMemory / 2;
        }
        return blocksize / 4;
    }

    public static int getStringSizeinBytes(String line) {
        return line.getBytes(StandardCharsets.UTF_8).length;
    }
}
