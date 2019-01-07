import java.util.ArrayList;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Individual {
    private ArrayList<Task> tasks;

    public Individual(ArrayList<Task> tasks)
    {
        this.tasks = tasks;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public Task getRandomTask()
    {
        return tasks.get(new Random().nextInt(tasks.size()));
    }

    public Task getTaskFromIndex(int index)
    {
        return tasks.get(index);
    }

    public int calculateFitness(int dueDate)
    {
        return CostCalculator.calculateCost(this, dueDate);
    }

    @Override
    public String toString() {
        return tasks
                .stream()
                .map(x -> Integer.toString(x.getId()))
                .limit(20)
                .collect(Collectors.joining(" "));
    }
}
