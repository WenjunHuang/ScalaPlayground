package reordering;

public class PossibleReorderingJava {
    public int x = 0, y = 0;
    public int a = 0, b = 0;

    public String run() throws InterruptedException {
        Thread one = new Thread(() -> {
            a = 1;
            x = b;
        });
        Thread other = new Thread(() -> {
            b = 1;
            y = a;
        });
        one.start();
        other.start();
        one.join();
        other.join();
        return "(" + x + "," + y + ")";
    }

    public static void main(String[] args)throws InterruptedException {
        while (true) {
            PossibleReorderingJava obj = new PossibleReorderingJava();
            String result = obj.run();
            if (result == "(0,0)")
                break;
        }
    }
}
