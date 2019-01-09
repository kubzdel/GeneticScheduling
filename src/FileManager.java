import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

class FileManager {
    static ArrayList<Task> loadFromTextFile(InstanceProperties instanceProperties) throws IOException {
        File file = getFile(inputDirectory, String.format("sch%d.txt", instanceProperties.getN()));
        BufferedReader br = new BufferedReader(new FileReader(file));
        br.readLine().trim();
        for (int curr = 1; curr < instanceProperties.getK(); curr++) {
            String size = br.readLine().trim();
            for (int i = 0; i < Integer.parseInt(size); i++)
                br.readLine();
        }
        br.readLine();
        ArrayList<Task> individual = new ArrayList<>(instanceProperties.getN());
        for (int i = 0; i < instanceProperties.getN(); i++) {
            String st = br.readLine();
            String[] data = st.split(" +");
            int procTime = Integer.parseInt(data[1]);
            int earliness = Integer.parseInt(data[2]);
            int tardiness = Integer.parseInt(data[3]);
            Task task = new Task(i, procTime, earliness, tardiness);
            individual.add(task);
        }
        return individual;
    }

    static void saveToTextFile(Individual individual, InstanceProperties instanceProperties) throws IOException {
        String outputFilename = String.format("out_indeks_%d_%d_%d.out", instanceProperties.getN(), instanceProperties.getK(), (int)(instanceProperties.getH()*10));
        File outputFile = getFile(outputDirectory, outputFilename);
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        writer.write(String.format("%d\n", CostCalculator.calculateCost(individual, instanceProperties.getDueDate())));
        writer.write(individual.toString());
        writer.close();
    }

    private static File getFile(String directory, String filename)
    {
        return Paths.get(
                FileSystems.getDefault().getPath(directory).toAbsolutePath().toString(),
                filename
        ).toFile();
    }

    public static ArrayList<Integer> loadBounds() throws IOException {
        Path cwd = FileSystems.getDefault().getPath("benchmarks").toAbsolutePath();
        File file = new File(cwd.toString() + "/newbounds.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        ArrayList<Integer>bounds = new ArrayList<>();
        while((st=br.readLine())!=null){
            String splited[] = st.split("\t+");
            for(int i=0;i<2;i++){
                splited[i] = splited[i].replace("*","");
                bounds.add(Integer.valueOf(splited[i]));
            }
        }
        return bounds;

    }

    static final String inputDirectory = "benchmarks";
    static final String outputDirectory = "output";
}
