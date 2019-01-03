import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

abstract class Heuristic {
    private Instance instance;

    Heuristic(InstanceProperties instanceProperties, ArrayList<Individual> population)
    {
        instance = new Instance(population, instanceProperties);
    }

    public Population getPopulation()
    {
        return instance.getPopulation();
    }

    public InstanceProperties getInstanceProperties()
    {
        return instance.getInstanceProperties();
    }
}

class GeneticAlgorithm extends Heuristic
{
    private double mutationRate = .001f;
    private boolean elitism = true;
    private int elitismOffset = 0;
    private double crossoverRate = 0.8f;
    private int tournamentSize = 10;
    private Random randomGenerator;

    GeneticAlgorithm(InstanceProperties instanceProperties, ArrayList<Individual> population, long seed) {
        super(instanceProperties, population);
        randomGenerator = new Random(seed);
    }

    GeneticAlgorithm(InstanceProperties instanceProperties, ArrayList<Individual> population) {
        this(instanceProperties, population, 0);
        tournamentSize = Math.max ( (int) (.1f*population.size()), 3);
    }

    public void generateNextGeneration()
    {
        Population newPopulation = new Population(getPopulation().getIndividuals());
        if (elitism) {
            newPopulation.getIndividuals().set(0, getPopulation().getFittest(getInstanceProperties().getDueDate()));
            elitismOffset = 1;
        } else {
            elitismOffset = 0;
        }
        for (int i = elitismOffset; i < tournamentSize; i++) {
            Individual indiv1 = tournamentSelection(getPopulation(), tournamentSize);
            Individual indiv2 = tournamentSelection(getPopulation(), tournamentSize);
            Individual newIndiv = crossover(indiv1, indiv2);
            newPopulation.getIndividuals().add(i, newIndiv);
        }
    }

    private Individual tournamentSelection(Population population, int tournamentSize) {
        Population tournament = new Population(getPopulation(), tournamentSize);
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



}
