import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

class MainClient {
    public static void main(String[] args) throws IOException {
        System.out.print("Welcome, CLIENT, port № ");
        int port = new Scanner(System.in).nextInt();
        ArrayList<String> messageList = new ArrayList<>();
        new Client("localhost", port, messageList).run();
    }
}