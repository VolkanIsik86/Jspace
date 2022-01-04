import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.util.Arrays;

public class Merger {
    public static void main(String[] args) {
        try{
            RemoteSpace sortSpace = new RemoteSpace("tcp://localhost:31415/sortSpace?keep");
            execute(sortSpace);
        }catch (Exception e){}
    }
    public static void execute(RemoteSpace sortSpace){
        while (true) {
            try {
                // We use a lock to avoid deadlocks due to mutually waiting merger workers
                sortSpace.get(new ActualField("lock"));

                Object[] parts1 = sortSpace.get(new ActualField("sorted"), new FormalField(Object.class), new FormalField(Integer.class));
                int[] a = (int[])parts1[1];
                int resultLength = (int)parts1[2];
                System.out.println("Merger got " + Arrays.toString(a) + "...");

                if (a.length == resultLength) {
                    sortSpace.put("result", a);
                    sortSpace.put("lock");
                } else {
                    Object[] parts2 = sortSpace.get(new ActualField("sorted"), new FormalField(Object.class), new FormalField(Integer.class));
                    int[] b = (int[])parts2[1];
                    System.out.println("Merger got " + Arrays.toString(b) + "...");
                    sortSpace.put("lock");

                    // Standard merge of two ordered vectors a and b
                    int[] c = merge(a, b);
                    sortSpace.put("sorted", c, resultLength);
                }

            } catch (InterruptedException e) {}
        }
    }
    private static int[] merge(int[] a, int[] b) {
        int[] c = new int[a.length + b.length];
        int i, j, k;
        i = j = k = 0;

        for (;;) {
            if (i == a.length) {
                for (; j < b.length; j ++) {
                    c[k] = b[j];
                    k ++;
                }
                break;
            }
            if (j == b.length) {
                for (; i < a.length; i ++) {
                    c[k] = a[i];
                    k++;
                }
                break;
            }
            if (a[i] <= b[j]) {
                c[k] = a[i];
                i ++;
            } else {
                c[k] = b[j];
                j ++;
            }
            k ++;
        }

        return c;
    }
}
