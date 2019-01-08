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
    private Population population;
    private double mutationRate = .001f;
    private boolean elitism = true;
    private int elitismOffset = 0;
    private double crossoverRate = 0.6f;
    private int tournamentSize = 5;
    private int populationSize = 500;
    private Random randomGenerator;

    GeneticAlgorithm(InstanceProperties instanceProperties, ArrayList<Task> tasks,
                     long seed, PopulationGenerator populationGenerator, int populationSize) {
        super(instanceProperties, tasks);
        population = new Population(
                populationGenerator.generatePopulation(tasks, populationSize, instanceProperties.getDueDate()));
        randomGenerator = new Random(seed);
        tournamentSize = Math.max((int) (.1f * tasks.size()), 15);
    }

    GeneticAlgorithm(InstanceProperties instanceProperties, ArrayList<Task> tasks,
                     PopulationGenerator populationGenerator, int populationSize) {
        this(instanceProperties, tasks, 0, populationGenerator, populationSize);
        this.populationSize = populationSize;
    }

    GeneticAlgorithm(InstanceProperties instanceProperties, ArrayList<Task> tasks, long seed) {
        this(instanceProperties, tasks, seed, new VShapePopulationGenerator(), 500);
    }

    public void generateNewPopulation(PopulationGenerator populationGenerator, int populationSize) {
        population = new Population(
                populationGenerator.generatePopulation(instance.getTasks(), populationSize, getInstanceProperties().getDueDate())
        );
        this.populationSize = populationSize;
    }

    public void generateNextGeneration() {
        Population newPopulation = new Population();

        elitism = randomGenerator.nextBoolean();
        if (elitism) {
            newPopulation.getIndividuals().add(population.getFittest(getInstanceProperties().getDueDate()));
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
            mutate(newIndiv);

            // add it to a new population
            newPopulation.getIndividuals().add(i, newIndiv);
        }
        population = newPopulation;
    }

    private Individual tournamentSelection(Population population, int tournamentSize) {
        Population tournament = new Population(population, tournamentSize);
        for (int i = 0; i < tournamentSize; i++) {
            int randomId = (int) (Math.random() * population.getIndividuals().size());
            tournament.getIndividuals().add(i, population.getIndividuals().get(randomId));
        }
        return tournament.getFittest(getInstanceProperties().getDueDate());
    }

    private void mutate(Individual individual) {
        for (int i = 0; i < getInstanceProperties().getN(); i++) {
            double p = (double) randomGenerator.nextInt(1000) / 1000;
            if (p <= mutationRate) {
                Collections.swap(individual.getTasks(),
                        randomGenerator.nextInt(individual.getTasks().size()),
                        randomGenerator.nextInt(individual.getTasks().size()));
            }
        }
    }

    private Individual crossover(Individual individual1, Individual individual2) {
        ArrayList<Task> newSolution = new ArrayList<>(individual1.getTasks().size());
        for (int i = 0; i < individual1.getTasks().size(); i++) {
            if (Math.random() <= crossoverRate) {
                newSolution.add(i, individual1.getRandomTask());
            } else {
                newSolution.add(i, individual2.getRandomTask());
            }
        }
        return new Individual(newSolution);
    }

    private Individual subParentCrossover(Individual individual1, Individual individual2) {
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

    private boolean contains(Task[] array, Task t) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null)
                if (array[i].equals(t)) return true;
        }
        return false;
    }

    public Population getPopulation() {
        return population;
    }

    @Override
    public void step() {
        generateNextGeneration();
    }
}
