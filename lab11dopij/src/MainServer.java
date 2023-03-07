import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class MainServer {
    public static void main(String[] args) throws IOException {
        System.out.print("Welcome, SERVER, port № ");
        int port = new Scanner(System.in).nextInt();
        ArrayList<String> messageList = new ArrayList<>();
        new Server(port, messageList).run();
    }
}