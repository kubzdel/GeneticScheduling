import java.util.ArrayList;

public class Instance {
    private ArrayList<Task> tasks;
    private InstanceProperties instanceProperties;

    public Instance(ArrayList<Task> tasks, InstanceProperties instanceProperties){
        this.instanceProperties = instanceProperties;
        this.tasks = tasks;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public InstanceProperties getInstanceProperties() {
        return instanceProperties;
    }

    @Override
    public String toString() {
        return instanceProperties.toString();
    }
}
