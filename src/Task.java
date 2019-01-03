public class Task {
    private int procTime;
    private int id;
    private int earlinessP;
    private int tardinessP;
    public Task(int id, int procTime, int earlinessP, int tardinessP) {
        this.id = id;
        this.procTime = procTime;
        this.earlinessP = earlinessP;
        this.tardinessP = tardinessP;
    }

    public int getProcTime() {
        return procTime;
    }

    public int getId() {
        return id;
    }

    public int getEarlinessP() {
        return earlinessP;
    }

    public int getTardinessP() {
        return tardinessP;
    }
}
