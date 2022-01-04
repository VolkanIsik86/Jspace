import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.io.IOException;

public class Philosopher {
    public static void main(String[] args) {
        try {
            int me, left, right;
            RemoteSpace board = new RemoteSpace("tcp://localhost:31415/board?keep");
            board.put("number");
            Object[] response = board.get(new ActualField("number"), new ActualField("result"), new FormalField(Integer.class));
            me = (Integer) response[2];
            left = me;
            right = (me + 1) % 3;

            eat(board,me,left,right);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void eat(RemoteSpace board, int me, int left, int right){
        // The philosopher enters his endless life cycle.
        while (true) {
            try {
                // Get lock
                board.get(new ActualField("lock"));

                // Wait until the left fork is ready (get the corresponding tuple).

                board.get(new ActualField("fork"), new ActualField(left));
                System.out.println("Philosopher " + me + " got left fork");

                // Wait until the right fork is ready (get the corresponding tuple).
                board.get(new ActualField("fork"), new ActualField(right));
                System.out.println("Philosopher " + me + " got right fork");

                // Lunch time.
                System.out.println("Philosopher " + me + " is eating...");

                // Return the forks (put the corresponding tuples).
                board.put("fork", left);
                board.put("fork", right);
                System.out.println("Philosopher " + me + " put both forks on the table");
                board.put("lock");

            } catch (InterruptedException e) {}
        }
    }
}
