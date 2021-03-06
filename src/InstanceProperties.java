public class InstanceProperties
{
    private int n;
    private int k;
    private double h;
    private int sumP;
    private int dueDate;

    public InstanceProperties(int n, int k, double h)
    {
        this.n = n;
        this.k = k;
        this.h = h;
    }

    public double getH() {
        return h;
    }

    public int getK() {
        return k;
    }

    public int getN() {
        return n;
    }

    @Override
    public String toString() {
        return String.format("n = %d, k = %d, h = %f, due date = %d, sum = %d", n, k, h, dueDate, sumP);
    }

    public int getDueDate() {
        return dueDate;
    }

    public int getSumP() {
        return sumP;
    }

    public void setSumPAndDueDate(int sum)
    {
        sumP = sum;
        dueDate = (int)(sum * h);
    }
}
