import org.jspace.FormalField;
import org.jspace.Space;

import java.util.HashMap;

public class IdentityProvider implements Runnable {
    Space space;
    HashMap<String,String> credentials;

    public IdentityProvider(Space space) {
        this.space = space;
        credentials = new HashMap<>();
        credentials.put("volkan","1234");
    }


    @Override
    public void run() {
        while (true){
            try {
                Object[] cred = space.get(new FormalField(String.class), new FormalField(String.class));
                String name = cred[0].toString();
                String pass = cred[1].toString();

                if(credentials.containsKey(name)){
                    if(credentials.get(name).equals(pass)){
                        space.put("if");
                    }else{
                        space.put("else");
                    }
                }else{
                    space.put("else");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
