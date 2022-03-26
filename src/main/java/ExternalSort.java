import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class ExternalSort {


    public static void sort(File file) throws IOException {
        int maxTempFiles = 1024;
        long fileSize = getFileSize(file);
        long maxBlockSize = estimateBestSizeOfBlocks(fileSize, maxTempFiles, estimateAvailableMemory());
        //List<File> sortedTempFiles = distribute(file,maxBlockSize,"::",new File("src/"));
        List<File> SortedYEET = new ArrayList<>();
        SortedYEET.add(new File("src\\temp-file12452488234315142491.tmp"));
        SortedYEET.add(new File("src\\temp-file7658677191120903075.tmp"));
        initializeMerge(SortedYEET,new File("src/outPUT.txt"),"::");



    }
    public static long initializeMerge(List<File> sortedTempFiles, File outputFile, String delimiter) throws IOException {
        ArrayList<IOStringStack> buffers = new ArrayList<>();
        for(File file: sortedTempFiles){
            InputStream inputStream = new FileInputStream(file);
            BufferedReader bufferedReader;
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            FileBuffer buffer =  new FileBuffer(bufferedReader,delimiter);
            buffers.add(buffer);
        }
        BufferedWriter bufferedWriter =  new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile,false)));
        long rowCounter = merge(bufferedWriter,buffers);
        for(File f: sortedTempFiles){
            f.deleteOnExit();
        }
        return rowCounter;
    }

    public static long merge(BufferedWriter bufferedWriter, List<IOStringStack> buffers) throws IOException {
        Comparator<NTLMPair> comp = new NtlmComparator();
        PriorityQueue<IOStringStack> priorityQueue =  new PriorityQueue<>(11, (o1, o2) -> comp.compare(o1.getCache(),o2.getCache()));
        for (IOStringStack buffer: buffers){
            if(!buffer.cacheEmpty()){
                priorityQueue.add(buffer);
            }
        }
        long rowcounter= 0;
        try{
            NTLMPair lastline = null;
            while (priorityQueue.size()> 0){
                IOStringStack priorityBuffer = priorityQueue.poll();
                NTLMPair pair = priorityBuffer.popCache();
                bufferedWriter.write(pair.toString());
                bufferedWriter.newLine();
                lastline = pair;
                ++rowcounter;
                if(priorityBuffer.cacheEmpty()){
                    priorityBuffer.close();
                }else{
                    priorityQueue.add(priorityBuffer);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            bufferedWriter.close();
            for(IOStringStack buffer: priorityQueue){
                buffer.close();
            }
        }
        return rowcounter;
    }


    public static List<File> distribute(File file, long maxBlockSize, String delimiter, File directory) throws IOException {

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
        }catch (IOException e){
            e.printStackTrace();
            if(temp.size()>0){
                files.add(saveToTempFiles(temp,directory,delimiter));
                temp.clear();
            }
        }
        finally {
            bufferedReader.close();
        }
        return files;
    }

    public static File saveToTempFiles(List<NTLMPair> temp, File directory, String delimiter) throws IOException {
        String fileTemplate = "temp-file";
        temp.sort(new NtlmComparator());
        File tempFile = File.createTempFile(fileTemplate, null,(new File("src/")));
        try {
            tempFile.deleteOnExit();
            OutputStream out = new FileOutputStream(tempFile);

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(out));
            for (NTLMPair pair : temp) {
                bufferedWriter.write(pair.word + delimiter + pair.hash);
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        long blocksize = sizeoffile / maxtmpfiles
                + (sizeoffile % maxtmpfiles == 0 ? 0 : 1);
        if (blocksize < maxMemory / 2) {
            blocksize = maxMemory / 2;
        }
        return blocksize/2;
    }

    public static int getStringSizeinBytes(String line) {
        return line.getBytes(StandardCharsets.UTF_8).length;
    }
}
