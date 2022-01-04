import org.jspace.*;

import java.util.ArrayList;
import java.util.List;

public class Server {
    public static void main(String[] args) {
        SpaceRepository lobby = new SpaceRepository();
        PileSpace chat = new PileSpace();
        lobby.add("chat",chat);
        lobby.addGate("tcp://localhost:31415/?keep");
        List<String> names = new ArrayList<>();

        new Thread(new butler(chat,names)).start();
        new Thread(new waiter(chat,names)).start();

        try {
            chat.query(new ActualField("done")); // will never succeed
        } catch (InterruptedException e) {}
    }

}
class butler implements Runnable{
    private Space space;
    private List<String> names;
    public butler(Space space,List<String> names){
        this.space = space;
        this.names = names;
    }

    @Override
    public void run() {
        while (true){
            try {
                Object[] t = space.get(new FormalField(String.class));
                String name = (String) t[0];
                System.out.println(name);
                names.add(name);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class waiter implements Runnable{
    private Space space;
    public List<String> names;
    public waiter(Space space,List<String> names){
        this.names = names;
        this.space = space;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Object[] t  = space.get(new FormalField(String.class), new FormalField(String.class));
                String name = t[0].toString();
                String message = t[1].toString();
                System.out.println(name + ": " + message);

                for (String user:names) {
                    space.put(name,message,user);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
}