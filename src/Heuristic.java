import java.util.*;

abstract class Heuristic {
    protected Instance instance;

    Heuristic(InstanceProperties instanceProperties, ArrayList<Task> tasks) {
        instance = new Instance(tasks, instanceProperties);
    }

    // method performed in each algorithm iteration
    public abstract void step();

    public InstanceProperties getInstanceProperties() {
        return instance.getInstanceProperties();
    }
}

class GeneticAlgorithm extends Heuristic {
    public Population population;
    private double mutationRate = 0.1f;
    private boolean elitism = true;
    private int elitismOffset = 0;
    private double crossoverRate = 0.7f;
    private int tournamentSize = 5;
    private int populationSize = 100;
    private double VsortRate = 0.4f;
    private double lastBestScore = Double.MAX_VALUE;
    private int consecutiveSameScore = 0;
    private Random randomGenerator;
    // best individual from 1st population
    private Individual initialBestIndividual;

    GeneticAlgorithm(InstanceProperties instanceProperties, ArrayList<Task> tasks,
                     long seed, PopulationGenerator populationGenerator, int populationSize) {
        super(instanceProperties, tasks);
        population = new Population(
                populationGenerator.generatePopulation(tasks, populationSize, instanceProperties.getDueDate()));
        randomGenerator = new Random(seed);
        tournamentSize = Math.max((int) (.1f * tasks.size()), 1);
    }

    GeneticAlgorithm(InstanceProperties instanceProperties, ArrayList<Task> tasks,
                     PopulationGenerator populationGenerator, int populationSize) {
        this(instanceProperties, tasks, 0, populationGenerator, populationSize);
        this.populationSize = populationSize;
    }

    GeneticAlgorithm(InstanceProperties instanceProperties, ArrayList<Task> tasks, long seed) {
        this(instanceProperties, tasks, seed, new VShapePopulationGenerator(), 100);
    }

    public void generateNewPopulation(PopulationGenerator populationGenerator, int populationSize) {
        population = new Population(
                populationGenerator.generatePopulation(instance.getTasks(), populationSize, getInstanceProperties().getDueDate())
        );
        this.populationSize = populationSize;
        initialBestIndividual = population.getFittest(getInstanceProperties().getDueDate());
    }

    public void generateNextGeneration() {
        Population newPopulation = new Population();
        Individual bestAsFar = population.getFittest(getInstanceProperties().getDueDate());
        //elitism = randomGenerator.nextBoolean();
        if (elitism) {
            newPopulation.getIndividuals().add(bestAsFar);
            elitismOffset = 1;
        } else {
            elitismOffset = 0;
        }

        for (int i = elitismOffset; i < populationSize; i++) {
            // choose two individuals from 2 tournaments
            Individual indiv1 = tournamentSelection(population, tournamentSize);
            Individual indiv2 = tournamentSelection(population, tournamentSize);

            // perform crossover over those two individuals
            // Individual newIndiv = crossover(indiv1, indiv2);
            Individual newIndiv = subParentCrossover(indiv1, indiv2);


            // mutation on the new one
            newIndiv = notWorstMutation(newIndiv, 10, getInstanceProperties().getDueDate());

            // sort V shape (p/a and p/b) with given probability)
            sortVnearD(newIndiv);

            // add it to a new population
            newPopulation.getIndividuals().add(i, newIndiv);

        }

        //if the best score of the population doesn't get better 5 times in a row
        //use mutation on every individual in this population except the best one
            if(newPopulation.getFittest(getInstanceProperties().getDueDate()).calculateFitness(getInstanceProperties().getDueDate())<lastBestScore){
                lastBestScore = newPopulation.getFittest(getInstanceProperties().getDueDate()).calculateFitness(getInstanceProperties().getDueDate());
                consecutiveSameScore = 0;
            }
            else{
                consecutiveSameScore++;
            }
            if(consecutiveSameScore>=5){
                for(Individual i : newPopulation.getIndividuals()){
                konradMutation(i,bestAsFar);
                }
            }
        population = newPopulation;
    }

    private Individual tournamentSelection(Population population, int tournamentSize) {
        Population tournament = new Population(population, tournamentSize);
        return tournament.getFittest(getInstanceProperties().getDueDate());
    }

    private void mutate(Individual individual, int numberOfMutations) {
        for (int i = 0; i < numberOfMutations; i++) {
            double p = (double) randomGenerator.nextInt(1000) / 1000;
            if (p <= mutationRate) {
                Collections.swap(individual.getTasks(),
                        randomGenerator.nextInt(individual.getTasks().size()),
                        randomGenerator.nextInt(individual.getTasks().size()));
            }
        }
    }

    //swap one task before d with one after d
    private void konradMutation(Individual individual,Individual best) {
        if (!individual.equals(best)) {
            int d = getInstanceProperties().getDueDate();
            int startIndex = 0;
            int startTime = 0;
            while (startTime < d) {
                startTime += individual.getTasks().get(startIndex).getProcTime();
                startIndex++;
            }

            Collections.swap(individual.getTasks(), randomGenerator.nextInt(startIndex),
                    randomGenerator.nextInt(individual.getTasks().size() - startIndex) + startIndex);
        }
    }
    private Individual notWorstMutation(Individual individual, int maxNumberOfMutations, int dueDate){
        int initialIndividualCost = initialBestIndividual.calculateFitness(dueDate) ;
        for(int i = 0;i<maxNumberOfMutations;i++) {
            int dueDateTaskIndex = 0;
            int currTotalTime = 0;
            for (int j = 0; j < individual.getTasks().size(); j++) {
                currTotalTime += individual.getTasks().get(j).getProcTime();
                if (currTotalTime >= dueDate) {
                    dueDateTaskIndex = j;
                    break;
                }
            }


            int tasksIndexToSwap = randomGenerator.nextInt(dueDateTaskIndex);
            for(int t = individual.getTasks().size()-1;t>dueDateTaskIndex;t--)
            {
                ArrayList<Task> individualProposal = new ArrayList<>(individual.getTasks());
                Collections.swap(individualProposal, tasksIndexToSwap, t);
                int newCost = CostCalculator.calculateCost(individualProposal, dueDate);
                if(newCost < initialIndividualCost) {
                    Individual newBestIndividual = new Individual(individualProposal);
                    initialBestIndividual = newBestIndividual;
                    return newBestIndividual;
                }

            }
        }
        return individual;
    }

    private Individual subParentCrossover(Individual individual1, Individual individual2) {
        if (Math.random() <= crossoverRate) {
            ArrayList<Task> newSolution = new ArrayList<>(individual1.getTasks().size());
            for (int s = 0; s < individual1.getTasks().size(); s++) {
                newSolution.add(null);
            }
            //1. Choose two parents P1 and P2.
            // 2. Randomly generate two integers j and k from the uniform [1, n], with j<k.
            // 3. Make child Ci, i = 1, 2 inherit the subsequence [j,... ,k] of its genes from P i.
            // 4. Fill the remaining empty genes of Ci, according to their order of appearance
            // in the second parent: if a gene is already inCi, reject it; else position it
            // in the first empty gene in Ci.

        // select two indices, such as i<j
        int i = randomGenerator.nextInt(individual1.getTasks().size());
        int j = randomGenerator.nextInt(individual1.getTasks().size());
        if (i > j) {
            int temp = i;
            i = j;
            j = temp;
        }
        // make child inherit the subsequence [i...j] of its genes from individual1
        for (int iter = i; iter <= j; iter++) {
            newSolution.set(iter, individual1.getTasks().get(iter));
        }

        // fill the remaining empty genes of the child with genes from individual2,
        // preserving their order
        for (int iter = 0; iter < individual1.getTasks().size(); iter++) {

            if (!newSolution.contains(individual2.getTasks().get(iter))) {

                for (int b = 0; b < individual1.getTasks().size(); b++) {

                    if (newSolution.get(b) == null) {
                        newSolution.set(b, individual2.getTasks().get(iter));
                        break;
                    }
                }

            }
        }

            return new Individual(newSolution);

        }
        else{
            Population parents = new Population();
            parents.getIndividuals().add(individual1);
            parents.getIndividuals().add(individual2);
            return parents.getFittest(getInstanceProperties().getDueDate());
        }
    }

    private void sortVnearD(Individual individual){
        if(Math.random()<=VsortRate) {
            int d = getInstanceProperties().getDueDate();
            int startTime = 0;
            int startIndex = 0;
            while (startTime < d) {
                startTime += individual.getTasks().get(startIndex).getProcTime();
                startIndex++;
            }
           // Collections.swap(individual.getTasks(),startIndex-1,startIndex);
            int startSortEarliness = randomGenerator.nextInt(startIndex);
            individual.getTasks().subList(startSortEarliness, startIndex).sort(new TaskEarlTimeComparator());
            int endSortIndex = startIndex + (randomGenerator.nextInt(getInstanceProperties().getN() - startIndex));
            individual.getTasks().subList(startIndex, endSortIndex).sort(new TaskTardTimeComparator());

        }
    }

    public Population getPopulation() {
        return population;
    }

    @Override
    public void step() {
        generateNextGeneration();
    }
}
