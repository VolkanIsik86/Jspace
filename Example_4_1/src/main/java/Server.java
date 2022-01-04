import org.jspace.FormalField;
import org.jspace.QueueSpace;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

public class Server {
    public static void main(String[] args) {
        SpaceRepository server = new SpaceRepository();
        QueueSpace space = new QueueSpace();
        server.add("space",space);
        QueueSpace credentialSpace = new QueueSpace();
        server.add("credentialSpace",credentialSpace);
        server.addGate("tcp://localhost:31415/?keep");

        new Thread(new IdentityProvider(credentialSpace)).start();

        while (true){
            try {
                Object[] userCredentials = space.get(new FormalField(String.class), new FormalField(String.class));
                String name = userCredentials[0].toString();
                String pass = userCredentials[1].toString();

                credentialSpace.put(name,pass);

                Object[] t = credentialSpace.get(new FormalField(String.class));
                String response = t[0].toString();

                if(response.equals("if")){
                    space.put("ok");
                }else{
                    space.put("bye");
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
