import java.util.ArrayList;

public class Instance {
    private Population population;
    private InstanceProperties instanceProperties;

    public Instance(ArrayList<Individual> population, InstanceProperties instanceProperties){
        int sumP = population
                .get(0)
                .getTasks()
                .stream()
                .mapToInt(Task::getProcTime)
                .sum();
        int dueDate = (int)(sumP * instanceProperties.getH());
        instanceProperties.setSumP(sumP);
        instanceProperties.setDueDate(dueDate);
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
