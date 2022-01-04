import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.Space;

import java.util.Arrays;

public class MergeSort {
    //N defines the number of "splitter" workers
    public static final int N = 2;

    //M defines the number of "merger" workers
    public static final int M = 2;

    public static void main(String[] args) {
        try {

            Space space = new SequentialSpace();

            // We place the array to be sorted in the tuple space
            int[] arr = new int[]{7, 6, 5, 4, 3, 2, 1};
            space.put("sort", arr, arr.length);

            // We add a lock for coordinating the merger workers
            space.put("lock");

            // We launch all workers
            for (int i = 0; i < N; i ++){
                new Thread(new splitter(space, i)).start();
            }
            for (int i = 0; i < M; i ++) {
                new Thread(new merger(space, i)).start();
            }

            // Here we wait for our result
            Object[] results = space.query(new ActualField("result"), new FormalField(Object.class));
            int[] result = (int[])results[1];
            System.out.println("RESULT: " + Arrays.toString(result));


        } catch (InterruptedException e) {}
    }
}

class splitter implements Runnable {
    private int me;
    private Space space;

    public splitter(Space space, int me) {
        this.space = space;
        this.me = me;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Object[] parts = space.get(new ActualField("sort"), new FormalField(Object.class), new FormalField(Integer.class));
                int[] arr = (int[])parts[1];
                int resultLength = (int)parts[2];
                System.out.println("Splitter " + me + " got " + Arrays.toString(arr) + "...");

                if (arr.length == 0) { // This should not happen

                    continue;

                } else if (arr.length == 1) {

                    space.put("sorted", arr, resultLength);

                } else {

                    int len = arr.length;
                    int[] a = Arrays.copyOfRange(arr, 0, len/2);
                    int[] b = Arrays.copyOfRange(arr, (len/2), len);
                    space.put("sort", a, resultLength);
                    space.put("sort", b, resultLength);

                }
            } catch (InterruptedException e) {}
        }
    }
}

class merger implements Runnable {
    private int me;
    private Space space;

    public merger(Space space, int me) {
        this.space = space;
        this.me = me;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // We use a lock to avoid deadlocks due to mutually waiting merger workers
                space.get(new ActualField("lock"));

                Object[] parts1 = space.get(new ActualField("sorted"), new FormalField(Object.class), new FormalField(Integer.class));
                int[] a = (int[])parts1[1];
                int resultLength = (int)parts1[2];
                System.out.println("Merger " + me + " got " + Arrays.toString(a) + "...");

                if (a.length == resultLength) {
                    space.put("result", a);
                    space.put("lock");
                } else {
                    Object[] parts2 = space.get(new ActualField("sorted"), new FormalField(Object.class), new FormalField(Integer.class));
                    int[] b = (int[])parts2[1];
                    System.out.println("Merger " + me + " got " + Arrays.toString(b) + "...");
                    space.put("lock");

                    // Standard merge of two ordered vectors a and b
                    int[] c = merge(a, b);
                    space.put("sorted", c, resultLength);
                }

            } catch (InterruptedException e) {}
        }
    }

    private int[] merge(int[] a, int[] b) {
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
