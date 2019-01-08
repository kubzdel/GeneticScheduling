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

    public ArrayList<Individual> generatePopulation(ArrayList<Task> tasks, int populationSize, int dueDate) {
        ArrayList<Individual> population = new ArrayList<>(populationSize);

        Individual firstIndividual = createKonradIndividual(tasks, dueDate);
      //  Individual lastIndividual = createInitialIndividual(tasks,dueDate);
        Random rand = new Random();
        // V-shape sorted list is on the first place
        population.add(firstIndividual);
        for(int m=0;m<populationSize-1;m++)
        {
            ArrayList<Task> newTasksList = new ArrayList<>(firstIndividual.getTasks());
           // ArrayList<Task> newTasksList2 = new ArrayList<>(lastIndividual.getTasks());
            for(int i=0;i<rand.nextInt(populationSize);i++)
            {
                //random V shape
                Collections.swap(newTasksList, rand.nextInt(tasks.size()), rand.nextInt(tasks.size()));
                //random Konrad shape
              //  Collections.swap(newTasksList2, rand.nextInt(tasks.size()), rand.nextInt(tasks.size()));
            }
            population.add(new Individual(newTasksList));

          //  population.add(new Individual(newTasksList2));
        }
        //Konrad sorted is on the last place
     //   population.add(lastIndividual);
        return population;
    }

    private int sumTime(ArrayList<Task>list){
        if(list.isEmpty()==true)return 0;
        int sum = 0;
        for(Task t:list){
            sum+=t.getProcTime();
        }
        return sum;
    }

    private Task getMin(ArrayList<Task> list){
        Task min= null;
        int diff = Integer.MAX_VALUE;
        double diffP = Double.MAX_VALUE;
        for(Task t:list){
            if(t.getTardinessP()-t.getEarlinessP()<diff)
            {  min = t;
                diff = t.getTardinessP()-t.getEarlinessP();
                diffP = t.getProcTime()/(double)t.getTardinessP();}
            else if((t.getTardinessP()-t.getEarlinessP())==diff)
                if(t.getProcTime()/(double)t.getTardinessP()<diffP){
                    min = t;
                    diffP = t.getProcTime()/(double)t.getTardinessP();
                }
        }
        return min;
    }
    private Individual createKonradIndividual(ArrayList<Task> tasks,int dueDate)
    {
        tasks.sort(new TaskEarlTardComparator());
        ArrayList<Task>tempB = new ArrayList<>(tasks);
        ArrayList<Task>tempA = new ArrayList<>();
        while(sumTime(tempB)>dueDate){
            Task top = getMin(tempB);
            tempA.add(top);
            tempB.remove(top);
        }
        tempA.sort(new TaskTardTimeComparator());
        tempB.sort(new TaskEarlTimeComparator());
        ArrayList<Task> sortedTasks =  new ArrayList<>(tempB);
        sortedTasks.addAll(tempA);
        return new Individual((sortedTasks));
    }
    private Individual createInitialIndividual(ArrayList<Task> tasks, int dueDate)
    {
        tasks.sort(new TaskEarlinessComparator());

        ArrayList<Task> finalList = new ArrayList<>();
        int sum = 0;
        for(int i = 0; i < tasks.size();i++)
        {
            sum += tasks.get(i).getProcTime();
            if(sum < dueDate)
            {
                // add tasks sorted ascending by earliness as long as they don't exceed dueDate
                finalList.add(tasks.get(i));
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
