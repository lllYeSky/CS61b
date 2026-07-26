package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        AList<Integer> N = new AList<Integer>();
        SLList<Integer>[] array = (SLList<Integer>[]) new SLList[10];
        AList<Double> ti = new AList<Double>();
        AList<Integer> opc = new AList<Integer>();
        int[] x = {1000, 2000, 4000, 8000, 16000, 32000, 64000, 128000};
        for(int i = 0; i<8; i++) {
            opc.addLast(10000);
            N.addLast(x[i]);
            array[i] = new SLList<>();
            for (int j = 1; j <= x[i]; j++) {
                array[i].addLast(1);
            }
            Stopwatch sw = new Stopwatch();
            for (int j = 1; j <= 10000; j++) {
                array[i].getLast();
            }
            double timeInSeconds = sw.elapsedTime();
            ti.addLast(timeInSeconds);
        }
        TimeSLList.printTimingTable(N, ti, opc);
    }

}
