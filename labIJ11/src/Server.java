import java.io.*;
import java.net.*;
import java.util.Scanner;
public class Server {
    private ServerSocket ssocket;
    private Socket socket;
    private String name, clientName;
    private DataInputStream dis;
    //private byte[] data;
    //private String fileP = "C:\Users\79112\OneDrive\Рабочий стол\techprog\labIJ11\src\historyOFchat.txt/";
    private DataOutputStream dos;
    private BufferedReader kb;
    private Thread sender;
    public Server(int port) throws IOException {
        ssocket = new ServerSocket(port);
    }
    public void run() {
        System.out.print("@name ");
        name = new Scanner(System.in).nextLine();
        try {
            socket = ssocket.accept();
            System.out.println("Please enter your message");
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            //data = new byte[1024];
            //String msg;
            sender = new Thread(new Sender());
            sender.start();
            clientName = dis.readUTF();
            try {
                while (true) {
                    /*msg = scanner.nextLine();
                    if (msg.contains("@dump")) {
                        String[] dataString;

                    }
                    if (msg.contains("@quit")) {
                        System.out.println("Client is still busy");
                        break;
                    }*/
                    String str;
                    str = dis.readUTF();
                    if (str.equals("@quit")) {
                        System.out.println("Client is still busy");
                        break;
                    }
                    System.out.println(clientName + ": " + str);
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

    /*private void dumpf(String fileN) {
        try {
            File file = new File(fileP + fileN);
            dos.writeLong(file.length());
            dos.writeUTF(file.getName());
            FileInputStream ffile = new FileInputStream(file);
            dos.flush();
            ffile.close();
        } catch (IOException e) {
        }
    }
    public void writeFile(File file, String data) {
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }*/

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