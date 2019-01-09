import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class Main {
    public static void main(String args[]) throws IOException {
        ArrayList<Integer>bounds = FileManager.loadBounds();
        BufferedWriter writer = new BufferedWriter( new FileWriter( "sch_127331_127083.txt"));
        int instanceNO=0;
     //  String[] files = {"10","20","50","100","200","500","1000"};
        String[] files = {"1000"};
        String ks[] = {"4","9"};
        String hs[] = {"0.4","0.6"};
        for (String file:files)
                for (String k:ks)
                   for(String h:hs)
                   {
                       String aargs[]= {file, k,h, "150"};
        InstanceProperties instanceProperties = CMDArgumentsParser.Companion.validateArguments(aargs);
        int populationSize =150;
        if(args.length == 5)
            populationSize = Integer.parseInt(aargs[4]);

        final long preparationTimeStart = System.currentTimeMillis();
        ArrayList<Task> tasks = FileManager.loadFromTextFile(instanceProperties);

        instanceProperties.setSumPAndDueDate(tasks.stream().mapToInt(Task::getProcTime).sum());

        GeneticAlgorithm heuristic = new GeneticAlgorithm(instanceProperties, tasks, new Random().nextInt());
        // create initial population
        heuristic.generateNewPopulation(new VShapePopulationGenerator(), populationSize);

        final long TIME_LIMIT = instanceProperties.getN() * 100;
        long preparationTimeEnd = System.currentTimeMillis();
        long preparationTime = preparationTimeEnd - preparationTimeStart;
        int totalIterations = 0;
        while(System.currentTimeMillis() - preparationTimeEnd < TIME_LIMIT)
        {
            heuristic.step();
            System.out.println(heuristic.getPopulation().getFittest(instanceProperties.getDueDate()).calculateFitness(instanceProperties.getDueDate()));
            totalIterations++;
        }
        long executionTimeEnd = System.currentTimeMillis();
        final Individual best = heuristic.getPopulation().getFittest(instanceProperties.getDueDate());
        final int bestCost = best.calculateFitness(instanceProperties.getDueDate());
        FileManager.saveToTextFile(best, instanceProperties);
        long endTime = System.currentTimeMillis();

        System.out.println(String.format("Best instance cost %d", bestCost));
        System.out.println(String.format("Preparation time %d", preparationTimeEnd - preparationTimeStart));
        System.out.println(String.format("Execution time %d", executionTimeEnd - preparationTimeEnd));
        System.out.println(String.format("Total iterations %d", totalIterations));
        System.out.println(String.format("Save result time %d", endTime - executionTimeEnd));
        double relError = ((bounds.get(instanceNO)-bestCost)/(double)bounds.get(instanceNO))*-100;
        String resultLine =String.valueOf(file+','+k+','+h+','+bounds.get(instanceNO)+','+bestCost+','+relError+"%"+','+(executionTimeEnd-preparationTimeEnd)+'\n');
        writer.write(resultLine);
        instanceNO++;
    }
        writer.close();
    }
}
