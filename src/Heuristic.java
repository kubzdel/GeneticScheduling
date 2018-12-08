import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class Heuristic {

    private double totalTime;
    private int d;
    private int size;
    private ArrayList<Task[]>population = new ArrayList<>();


    public static void swap(Task[] arr, int i, int j) {
        Task temp = arr[i];
        arr[i]= arr[j];
        arr[j] = temp;
    }

    public void loadFile(String n, int k, double h,int populationSize) throws IOException {
        Path cwd = FileSystems.getDefault().getPath("benchmarks").toAbsolutePath();
        File file = new File(cwd.toString() + "/sch" + n + ".txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        double SUM_P = 0;
        br.readLine().trim();
        for (int curr = 1; curr < k; curr++) {
            String size = br.readLine().trim();
            for (int i = 0; i < Integer.parseInt(size); i++)
                br.readLine();
        }
        int size = Integer.parseInt(br.readLine().trim());
        this.size = size;
        Task[] individual = new Task[size];
        for (int i = 0; i < size; i++) {
            String st = br.readLine();
            String[] data = st.split(" +");
            int procTime = Integer.parseInt(data[1]);
            int earliness = Integer.parseInt(data[2]);
            int tardiness = Integer.parseInt(data[3]);
            SUM_P += procTime;
            Task task = new Task(i , procTime, earliness, tardiness);
            individual[i] = task;
        }
        Random rand = new Random();
        for(int m=0;m<populationSize;m++) //duplikacja i przemieszanie
            for(int i=0;i<size;i++){
                swap(individual, rand.nextInt(size),rand.nextInt(size));
                population.add(individual);
            }

        this.d = (int) (SUM_P * h);
        this.totalTime = SUM_P;
    }
}
