package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    public static void main(String[] args){
        randomizedTest();
        testThreeAddThreeRemove();
    }

    public static void randomizedTest(){
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> M = new BuggyAList<>();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                M.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size1 = L.size(), size2 = M.size();
                System.out.println("size1: " + size1);
                System.out.println("size2: " + size2);
            }else if(operationNumber == 2){
                // getLast
                if(L.size() > 0){
                    System.out.println("L.getLast(" + L.getLast() + ")");
                }
                if(M.size() > 0){
                    System.out.println("M.getLast(" + M.getLast() + ")");
                }
            }else{
                //removeLast
                if(L.size() > 0){
                    System.out.println("M.removeLast(" + L.removeLast() + ")");
                }
                if(M.size() > 0){
                    System.out.println("M.removeLast(" + M.removeLast() + ")");
                }
            }
        }
    }
    public static void testThreeAddThreeRemove(){
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> M = new BuggyAList<>();
        L.addLast(3);M.addLast(3);
        L.addLast(2);M.addLast(2);
        L.addLast(5);M.addLast(5);
        assertEquals(L.size(), M.size());

        assertEquals(L.removeLast(), M.removeLast());
        assertEquals(L.removeLast(), M.removeLast());
        assertEquals(L.removeLast(), M.removeLast());
    }
}
