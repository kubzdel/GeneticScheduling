import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String args[]) throws IOException {
        InstanceProperties instanceProperties = CMDArgumentsParser.Companion.validateArguments(args);
        int populationSize = 20;
        if(args.length == 5)
            populationSize = Integer.parseInt(args[4]);

        final long preparationTimeStart = System.currentTimeMillis();
        PopulationGenerator populationGenerator = new PopulationGenerator();
        ArrayList<Task> tasks = FileManager.loadFromTextFile(instanceProperties);

        GeneticAlgorithm heuristic = new GeneticAlgorithm(instanceProperties, populationGenerator.randomGenerator(tasks, populationSize));

        final long TIME_LIMIT = instanceProperties.getN() * 10000;
        long preparationTimeEnd = System.currentTimeMillis();
        long preparationTime = preparationTimeEnd - preparationTimeStart;
        int totalIterations = 0;
        while(System.currentTimeMillis() + preparationTime - preparationTimeEnd < TIME_LIMIT)
        {
            heuristic.generateNextGeneration();
            totalIterations++;
        }
        long executionTimeEnd = System.currentTimeMillis();
        final Individual best = heuristic.getPopulation().getFittest(instanceProperties.getDueDate());
        FileManager.saveToTextFile(best, instanceProperties);
        long endTime = System.currentTimeMillis();

        System.out.println(String.format("Best instance cost %d", CostCalculator.calculateCost(best, instanceProperties.getDueDate())));
        System.out.println(String.format("Preparation time %d", preparationTimeEnd - preparationTimeStart));
        System.out.println(String.format("Execution time %d", executionTimeEnd - preparationTimeEnd));
        System.out.println(String.format("Total iterations %d", totalIterations));
        System.out.println(String.format("Save result time %d", endTime - executionTimeEnd));
    }
}
