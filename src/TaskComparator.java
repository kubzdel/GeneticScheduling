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

class TaskEarlTardComparator implements  Comparator<Task>{
    @Override
    public int compare(Task t1, Task t2) {
        double t1penalty =t1.getEarlinessP()/(double)t1.getTardinessP();
        double t2penalty = t2.getEarlinessP()/(double)t2.getTardinessP();
        if(t1penalty<t2penalty) {
            return 1;
        }
        else if(t1penalty==t2penalty)return 0;

        else {
            return -1;
        }
}
}

class TaskTardTimeComparator implements Comparator<Task> {
    @Override
    public int compare(Task t1, Task t2) {
        double t1penalty;
        t1penalty = t1.getProcTime()/(double)t1.getTardinessP();

        double t2penalty;
        t2penalty = t2.getProcTime()/(double)t2.getTardinessP();

        if(t1penalty>t2penalty) {
            return 1;
        }
        else if(t1penalty==t2penalty) {
            return 0;
        }
        else
            return -1;
    }
}

class TaskEarlTimeComparator implements Comparator<Task> {
    @Override
    public int compare(Task t1, Task t2) {
        double t1penalty = t1.getProcTime() / ((double) t1.getEarlinessP());
        double t2penalty = t2.getProcTime() / ((double) t2.getEarlinessP());

        if (t1penalty < t2penalty) {
            return 1;
        } else if (t1penalty == t2penalty) return 0;
        else {
            return -1;
        }
    }
}