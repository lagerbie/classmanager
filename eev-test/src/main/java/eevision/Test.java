package eevision;

/**
 * @author fabrice
 */
public class Test {

    public static void main(String[] args) {
        System.out.println("out: " + args[0]);
        System.err.println("err: " + args[1]);

        long initTime = System.currentTimeMillis();
        long time = System.currentTimeMillis();

        while (time - initTime < 50000) {
            System.out.println("out: " + time);
            System.err.println("err: " + time);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            time = System.currentTimeMillis();
        }
    }

}
