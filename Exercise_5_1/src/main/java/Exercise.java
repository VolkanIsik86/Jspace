import org.jspace.ActualField;
import org.jspace.SequentialSpace;
import org.jspace.Space;

public class Exercise {


    public static void main(String[] args) {
        SequentialSpace p0 = new SequentialSpace();
        SequentialSpace p1 = new SequentialSpace();
        SequentialSpace p2 = new SequentialSpace();

        try {
            p0.put("token");
            p1.put("token");

            new Thread(new A(p0)).start();
            new Thread(new B(p0,p1,p2)).start();
            new Thread(new C(p1)).start();
            new Thread(new D(p0,p1,p2)).start();

            p0.query(new ActualField("done")); // will never succeed

        }catch (Exception e){

        }
    }



}

class A implements Runnable{

    Space p0;

    public A(Space p0) {
        this.p0 = p0;
    }

    @Override
    public void run() {
        while (true){
            try{
                Object[] t = p0.get(new ActualField("token"));
                System.out.println("IN A got token");
                String token = t[0].toString();
                p0.put(token);
            }catch (Exception e){

            }

        }

    }
}

class C implements Runnable{

    Space p1;

    public C(Space p1) {
        this.p1 = p1;
    }

    @Override
    public void run() {
        while (true){
            try{
                Object[] t = p1.get(new ActualField("token"));
                System.out.println("IN C got token");
                String token = t[0].toString();
                p1.put(token);
            }catch (Exception e){

            }

        }

    }
}

class B implements Runnable{
    Space p0;
    Space p1;
    Space p2;

    public B(Space p0, Space p1, Space p2) {
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public void run() {
        while (true){
            try{
                Object[] t0 = p0.get(new ActualField("token"));
                Object[] t1 = p1.get(new ActualField("token"));

                System.out.println("IN B got both token");
                String token0 = t0[0].toString();
                String token1 = t1[0].toString();

                p2.put(token0);

            }catch (Exception e){

            }
        }
    }
}

class D implements Runnable{
    Space p0;
    Space p1;
    Space p2;

    public D(Space p0, Space p1, Space p2) {
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public void run() {
        while (true){
            try{

                Object[] t0 = p2.get(new ActualField("token"));

                System.out.println("IN D got token");

                String token0 = t0[0].toString();
                p0.put("token");
                p1.put("token");
            }catch (Exception e){

            }
        }
    }
}