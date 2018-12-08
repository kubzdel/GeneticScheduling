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
}
