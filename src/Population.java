import java.util.ArrayList;
import java.util.Collections;

public class Population {
    private ArrayList<Individual> individuals;

    public Population()
    {
        // just an empty population used when generating new ones
        individuals = new ArrayList<>();
    }

    public Population(ArrayList<Individual> population)
    {
        this.individuals = population;
    }

    public Population(ArrayList<Individual> population, int populationSize)
    {
        ArrayList<Individual> tempPopulation = new ArrayList<>(population);
        Collections.shuffle(tempPopulation);
        individuals = new ArrayList<>(tempPopulation.subList(0, populationSize));
    }

    public Population(Population population, int populationSize)
    {
        this(population.individuals, populationSize);
    }

    public Individual getFittest(int dueDate)
    {
        Individual fittestIndividual = individuals.get(0);
        int cost = CostCalculator.calculateCost(fittestIndividual, dueDate);
        for(Individual individual: individuals)
        {
            int newCost = individual.calculateFitness(dueDate);
            if(newCost < cost)
            {
                cost = newCost;
                fittestIndividual = individual;
            }
        }
        return fittestIndividual;
    }

    public ArrayList<Individual> getIndividuals() {
        return individuals;
    }
}
