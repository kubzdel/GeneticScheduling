import java.util.ArrayList;

interface PopulationGenerator {
    ArrayList<Individual> generatePopulation(ArrayList<Task> tasks, int populationSize, int dueDate);
}

