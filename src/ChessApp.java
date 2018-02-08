import Engine.Engine;
import com.sun.org.apache.xpath.internal.SourceTree;

public class ChessApp {

    Engine e;

    public ChessApp() {
        //wait 10 seconds to start the profiler
        System.out.println("Starting in ");
        for (int i = 10; i > 0; i--) {
            System.out.println(i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        System.out.println("Starting...");
        e = new Engine();
    }

    public static void main(String[] args) {
        new ChessApp();
    }
}
