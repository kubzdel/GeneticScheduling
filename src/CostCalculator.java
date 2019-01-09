import java.util.ArrayList;

import static java.lang.Integer.max;

public class CostCalculator {
    public static int calculateCost(Individual individual, int dueDate)
    {
        return calculateCost(individual.getTasks(), dueDate);
    }
    public static int calculateCost(ArrayList<Task> tasks, int dueDate)
    {
        int currentCost = 0;
        int currentLength = 0;
        for(Task task: tasks){
            currentCost += max(0, dueDate - (currentLength + task.getProcTime()) ) * task.getEarlinessP() +
                    max(0, (currentLength + task.getProcTime())- dueDate) * task.getTardinessP();
            currentLength += task.getProcTime();
        }
        return currentCost;
    }
}
