import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

import java.util.Arrays;

public class Server {
    public static void main(String[] args) {
        SpaceRepository mergeSort = new SpaceRepository();
        SequentialSpace sortSpace = new SequentialSpace();
        mergeSort.add("sortSpace",sortSpace);
        mergeSort.addGate("tcp://localhost:31415/?keep");
        int[] arr = new int[]{7, 6, 5, 4, 3, 2, 1};
        try {
            sortSpace.put("sort",arr,arr.length);
            sortSpace.put("lock");
            Object[] results = sortSpace.query(new ActualField("result"), new FormalField(Object.class));
            int[] result = (int[])results[1];
            System.out.println("RESULT: " + Arrays.toString(result));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
