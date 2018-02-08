import Engine.Engine;

public class ChessApp {

    Engine e;

    public ChessApp() {
        //wait 5 seconds to initialize the profiler
        try {
            Thread.sleep(5000l);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        e = new Engine();
    }

    public static void main(String[] args) {
        new ChessApp();
    }
}
