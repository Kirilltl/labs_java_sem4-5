import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    private static final String dumpPath = "dump.txt";
    private ArrayList<String> messageList;
    private Socket socket;
    private String name, serverName;
    private DataInputStream dis;
    private DataOutputStream dos;
    private BufferedReader kb;
    private Thread receiver, mainThr;
    public Client(String adr, int port, ArrayList<String> messageList) throws IOException {
        this.messageList = messageList;
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
                String date = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm:ss").format(LocalDateTime.now());
                String line = date + " " + str;
                messageList.add(line);
                dos.flush();
                if (str.equals("@quit")) {
                    socketClose();
                    break;
                }
                else if (str.equals("@dump")) {
                    FileWriter fileWriter = new FileWriter(dumpPath);
                    for (String message : messageList){
                        fileWriter.write(message);
                        fileWriter.write("\n");
                    }
                    System.out.println("file dumped");
                    fileWriter.close();
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
                    String date = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm:ss").format(LocalDateTime.now());
                    String line = date + " " + serverName + ":" + " " + str;
                    messageList.add(line);
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