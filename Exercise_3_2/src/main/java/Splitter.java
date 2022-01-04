import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.util.Arrays;

public class Splitter {
    public static void main(String[] args) {
        try{
            RemoteSpace sortSpace = new RemoteSpace("tcp://localhost:31415/sortSpace?keep");
            split(sortSpace);
        }catch (Exception e){}
    }
    public static void split(RemoteSpace sortSpace){
        while (true) {
            try {
                Object[] parts = sortSpace.get(new ActualField("sort"), new FormalField(Object.class), new FormalField(Integer.class));
                int[] arr = (int[])parts[1];
                int resultLength = (int)parts[2];
                System.out.println("Splitter got " + Arrays.toString(arr) + "...");

                if (arr.length == 0) { // This should not happen

                    continue;

                } else if (arr.length == 1) {

                    sortSpace.put("sorted", arr, resultLength);

                } else {

                    int len = arr.length;
                    int[] a = Arrays.copyOfRange(arr, 0, len/2);
                    int[] b = Arrays.copyOfRange(arr, (len/2), len);
                    sortSpace.put("sort", a, resultLength);
                    sortSpace.put("sort", b, resultLength);

                }
            } catch (InterruptedException e) {}
        }
    }
}
