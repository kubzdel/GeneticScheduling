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
    }

    public void generateNextGeneration()
    {
        Population newPopulation = new Population(population.getIndividuals());
        if (elitism) {
            newPopulation.getIndividuals().set(0, population.getFittest(getInstanceProperties().getDueDate()));
            elitismOffset = 1;
        } else {
            elitismOffset = 0;
        }
        for (int i = elitismOffset; i < tournamentSize; i++) {
            Individual indiv1 = tournamentSelection(population, tournamentSize);
            Individual indiv2 = tournamentSelection(population, tournamentSize);
            Individual newIndiv = crossover(indiv1, indiv2);
            mutate(newIndiv);
            newPopulation.getIndividuals().add(i, newIndiv);
        }
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
}
