import java.util.ArrayList;

public class Instance {
    private Population population;
    private InstanceProperties instanceProperties;

    public Instance(ArrayList<Individual> population, InstanceProperties instanceProperties){
        this.instanceProperties = instanceProperties;
        this.population = new Population(population);
    }

    public Population getPopulation() {
        return population;
    }

    public InstanceProperties getInstanceProperties() {
        return instanceProperties;
    }

    @Override
    public String toString() {
        return instanceProperties.toString();
    }
}
