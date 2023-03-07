import java.io.*;
import java.net.*;
import java.util.Scanner;
public class Client {
    private Socket socket;
    private String name, serverName;
    private DataInputStream dis;
    private DataOutputStream dos;
    private BufferedReader kb;
    private Thread receiver, mainThr;
    public Client(String adr, int port) throws IOException {
        InetAddress ipAddress = InetAddress.getByName(adr);
        socket = new Socket(ipAddress, port);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
        kb = new BufferedReader(new InputStreamReader(System.in));
        receiver = new Thread(new Receiver());
        receiver.start();
    }
    private void socketClose() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run() {
        mainThr = Thread.currentThread();
        System.out.print("@name ");
        name = new Scanner(System.in).nextLine();
        try {
            dos.writeUTF(name);
            while (true) {
                String str;
                str = kb.readLine();
                if (socket.isClosed())
                    break;
                dos.writeUTF(str);
                dos.flush();
                if (str.equals("@quit")) {
                    socketClose();
                    break;
                }
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
    }
    private class Receiver implements Runnable {
        public void run() {
            try {
                serverName = dis.readUTF();
                while (true) {
                    String str;
                    str = dis.readUTF();
                    if (str.equals("@quit")) {
                        System.out.println("Server is still busy");
                        break;
                    }
                    System.out.println(serverName + ": " + str);
                }
            } catch (IOException e) {
                socketClose();
            } finally {
                socketClose();
            }
        }
    }
}