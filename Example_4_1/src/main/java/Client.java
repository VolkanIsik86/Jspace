import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

public class Client {
    public static void main(String[] args) {
        try {
            RemoteSpace server = new RemoteSpace("tcp://localhost:31415/space?keep");
            server.put("volkan","123");
            Object[] respose = server.get(new FormalField(String.class));
            String resp = respose[0].toString();

            if(resp.equals("ok")){
                System.out.println("Connected");
            }else if(resp.equals("bye")){
                System.out.println("bye");
            }

            server.get(new ActualField("infinite"));
        }catch (Exception e){

        }
    }
}
