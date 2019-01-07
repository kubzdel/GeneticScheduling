import java.util.Comparator;

class TaskEarlinessComparator implements Comparator<Task> {
    @Override
    public int compare(Task first, Task second) {
        if(first.getEarlinessP() == second.getEarlinessP())
            return 0;
        return first.getEarlinessP() > second.getEarlinessP() ? 1 : -1;
    }
}

class TaskTardinessComparator implements Comparator<Task> {
    @Override
    public int compare(Task first, Task second) {
        if(first.getTardinessP() == second.getTardinessP())
            return 0;
        return first.getTardinessP() > second.getTardinessP() ? 1 : -1;
    }
}