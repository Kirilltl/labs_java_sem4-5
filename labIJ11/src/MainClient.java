import java.io.IOException;
import java.util.Scanner;
public class MainClient {
    public static void main(String[] args) throws IOException {
        System.out.print("Welcome, CLIENT, port № ");
        int port = new Scanner(System.in).nextInt();
        new Client("localhost", port).run();
    }
}