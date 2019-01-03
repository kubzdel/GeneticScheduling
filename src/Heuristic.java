import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

abstract class Heuristic {
    protected Instance instance;

    Heuristic(InstanceProperties instanceProperties, ArrayList<Task> tasks)
    {
        instance = new Instance(tasks, instanceProperties);
    }

    // method performed in each algorithm iteration
    public abstract void step();

    public InstanceProperties getInstanceProperties()
    {
        return instance.getInstanceProperties();
    }
}

class GeneticAlgorithm extends Heuristic
{
    private Population population;
    private double mutationRate = .001f;
    private boolean elitism = true;
    private int elitismOffset = 0;
    private double crossoverRate = 0.2f;
    private int tournamentSize = 10;
    private int populationSize = 20;
    private Random randomGenerator;

    GeneticAlgorithm(InstanceProperties instanceProperties, ArrayList<Task> tasks,
                     long seed, PopulationGenerator populationGenerator, int populationSize) {
        super(instanceProperties, tasks);
        population = new Population(
                populationGenerator.generatePopulation(tasks, populationSize, instanceProperties.getDueDate()));
        randomGenerator = new Random(seed);
    }

    GeneticAlgorithm(InstanceProperties instanceProperties, ArrayList<Task> tasks,
                     PopulationGenerator populationGenerator, int populationSize) {
        this(instanceProperties, tasks, 0, populationGenerator, populationSize);
        this.populationSize = populationSize;
    }

    GeneticAlgorithm(InstanceProperties instanceProperties, ArrayList<Task> tasks, long seed) {
        this(instanceProperties, tasks, seed, new VShapePopulationGenerator(), 20);
        tournamentSize = Math.max ( (int) (.1f*tasks.size()), 3);
    }

    public void generateNewPopulation(PopulationGenerator populationGenerator, int populationSize)
    {
        population = new Population(
                populationGenerator.generatePopulation(instance.getTasks(), populationSize, getInstanceProperties().getDueDate())
        );
        this.populationSize = populationSize;
    }

    public void generateNextGeneration()
    {
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
            Individual newIndiv = crossover(indiv1, indiv2);

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
            double p = (double)randomGenerator.nextInt(1000) / 1000;
            if (p  <= mutationRate) {
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


    public Population getPopulation()
    {
        return population;
    }

    @Override
    public void step() {
        generateNextGeneration();
    }
}
