import org.jspace.ActualField;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;

public class Waiter {

    public static void main(String[] args) {
        SpaceRepository diningRepository = new SpaceRepository();
        SequentialSpace board = new SequentialSpace();
        diningRepository.add("board",board);
        diningRepository.addGate("tcp://localhost:31415/?keep");
        Object[] request;
        int num = 0;

        arrangeForks(board);

        while (true){
            try {
                request = board.get(new ActualField("number"));
                board.put("number","result",num);
                System.out.println("Philosopher " + num + " has entered the board.");
                num++;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
    // waiter prepares the board with forks.

        public static void arrangeForks(SequentialSpace board) {
            System.out.println("Waiter putting forks on the table...");

            for (int i = 0; i < 3; i ++) {
                try {
                    board.put("fork", i);
                    System.out.println("Waiter put fork " + i + " on the table.");
                } catch (InterruptedException e) {}
            }

            for (int i = 0; i < 2; i ++) {
                try {
                    board.put("lock");
                    System.out.println("Waiter put lock on the table.");
                } catch (InterruptedException e) {}
            }

            System.out.println("Waiter done.");
        }
}
