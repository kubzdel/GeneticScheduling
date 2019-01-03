import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class PopulationGenerator {
    public ArrayList<Individual> randomGenerator(ArrayList<Task> tasks, int populationSize)
    {
        ArrayList<Individual> population = new ArrayList<>();
        Random rand = new Random();
        for(int m=0; m<populationSize; m++) //duplikacja i przemieszanie
        {
            ArrayList<Task> tempTask = new ArrayList<>(tasks);
            for(int i=0; i<rand.nextInt(tempTask.size()); i++){
                Collections.swap(tempTask, rand.nextInt(tempTask.size()),rand.nextInt(tempTask.size()));
            }
            population.add(new Individual(tempTask));
        }
        return population;
    }
}
