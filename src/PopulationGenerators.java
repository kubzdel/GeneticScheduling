import java.awt.List;
import java.util.*;
import java.util.stream.IntStream;

class RandomPopulationGenerator implements PopulationGenerator {
    @Override
    public ArrayList<Individual> generatePopulation(ArrayList<Task> tasks, int populationSize, int dueDate)
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

class VShapePopulationGenerator implements PopulationGenerator{
    @Override
    public ArrayList<Individual> generatePopulation(ArrayList<Task> tasks, int populationSize, int dueDate) {
        ArrayList<Individual> population = new ArrayList<>(populationSize);

        Individual firstIndividual = createInitialIndividual(tasks, dueDate);
        Random rand = new Random();
        // V-shape sorted list is on the first place
        population.add(firstIndividual);
        for(int m=0;m<populationSize-1;m++)
        {
            ArrayList<Task> newTasksList = new ArrayList<>(firstIndividual.getTasks());
            for(int i=0;i<rand.nextInt(populationSize);i++)
            {
                Collections.swap(newTasksList, rand.nextInt(tasks.size()), rand.nextInt(tasks.size()));
            }
            population.add(new Individual(newTasksList));
        }

        return population;
    }

    private Individual createInitialIndividual(ArrayList<Task> tasks, int dueDate)
    {
        tasks.sort(new TaskEarlinessComparator());

        ArrayList<Task> finalList = new ArrayList<>();
        int sum = 0;
        for(int i = 0; i < tasks.size();i++)
        {
            if(sum < dueDate)
            {
                // add tasks sorted ascending by earliness as long as they don't exceed dueDate
                finalList.add(tasks.get(i));
                sum += tasks.get(i).getProcTime();
            } else {
                // other task just sort descending by tardiness and add to the finalList
                tasks.sort(new TaskTardinessComparator());
                Collections.reverse(tasks);
                int elementsToTake = tasks.size() - finalList.size();
                finalList.addAll(tasks.subList(0, elementsToTake));
            }
        }
        return new Individual(finalList);
    }
}
