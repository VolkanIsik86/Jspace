import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.Space;

import java.util.Scanner;

public class Client2 {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        try {
            RemoteSpace chat = new RemoteSpace("tcp://localhost:31415/chat?keep");
            System.out.println("Enter name:");
            String name = input.nextLine();
            System.out.print(name);
            System.out.println("\n");
            chat.put(name);
            new Thread(new clientwaiter2(chat,name)).start();
            while (true){
                String message = input.nextLine();
                chat.put(name,message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
class clientwaiter2 implements Runnable{
    Space space;
    String name;
    public clientwaiter2(Space space,String name){
        this.space = space;
        this.name = name;
    }

    @Override
    public void run() {
        while (true){
            try{
                Object[] t =  space.get(new FormalField(String.class), new FormalField(String.class), new ActualField(name));
                System.out.println(t[0] + " : " + t[1]);
            }catch (Exception e){

            }
        }
    }
}
