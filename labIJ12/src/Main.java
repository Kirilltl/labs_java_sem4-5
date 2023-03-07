import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Работать как Клиент или Сервер? server / client");
        while (true) {
            char answer = Character.toLowerCase(in.nextLine().charAt(0));
            if (answer == 's') {
                new ServerTCP(args[0]);
                break;
            } else if (answer == 'c') {
                new ClientTCP(args[0], args[1]);
                break;
            } else {
                System.out.println("Doh', error");
            }
        }
    }
}