import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {
    private static final String dumpPath = "dump.txt";
    private ServerSocket ssocket;
    private Socket socket;
    private String name, clientName;
    private DataInputStream dis;
    private DataOutputStream dos;
    private BufferedReader kb;
    private Thread sender;
    private ArrayList<String> messageList;
    public Server(int port, ArrayList<String> messageList) throws IOException {
        ssocket = new ServerSocket(port);
        this.messageList = messageList;
    }
    public void run() {
        System.out.print("@name ");
        name = new Scanner(System.in).nextLine();
        try {
            socket = ssocket.accept();
            System.out.println("Please enter your message");
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            sender = new Thread(new Sender());
            sender.start();
            clientName = dis.readUTF();
            try {
                while (true) {
                    String str;
                    str = dis.readUTF();
                    if (str.equals("@quit")) {
                        System.out.println("Client is still busy");
                        break;
                    }
                    System.out.println(clientName + ": " + str);
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").format(LocalDateTime.now()));
                    stringBuilder.append("  ");
                    stringBuilder.append(clientName).append(": ").append(str);
                    messageList.add(stringBuilder.toString());

                }
            } catch (SocketException e) {
                close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception c) {
            System.out.println(c.getMessage());
        } finally {
            close();
        }
    }
    private void close() {
        try {
            ssocket.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private class Sender implements Runnable {
        Sender() {
            kb = new BufferedReader(new InputStreamReader(System.in));
        }
        public void run() {
            try {
                dos.writeUTF(name);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (ssocket.isClosed() == false) {
                String str;
                try {
                    str = kb.readLine();
                    if (str != null && socket.isClosed() == false) {
                        dos.writeUTF(str);
                        dos.flush();
                        if (str.equals("@quit")) {
                            close();
                            break;
                        }
                        else if (str.equals("@dump")){
                            FileWriter fileWriter = new FileWriter(dumpPath);
                            for (String msg : messageList){
                                fileWriter.write(msg);
                                fileWriter.write("\n");
                            }
                            System.out.println("file dumped");
                            fileWriter.close();
                        }
                        else {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").format(LocalDateTime.now()));
                            stringBuilder.append("  ");
                            stringBuilder.append(str);
                            messageList.add(stringBuilder.toString());
                        }
                    }
                } catch (Exception e) {
                    if ("Socket closed".equals(e.getMessage())) {
                        close();
                        break;
                    }
                    e.printStackTrace();
                    close();
                }
            }
        }
    }
}