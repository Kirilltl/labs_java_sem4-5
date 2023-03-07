import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerTCP {
    private final List<ConnectionThread> connections = Collections.synchronizedList(new ArrayList<ConnectionThread>());
    private ServerSocket server;
    public ServerTCP(String port) {
        try {
            server = new ServerSocket(Integer.parseInt(port));
            System.out.println("Сервер запущен.");

            while (true) {
                System.out.println("Ожидание клиентов.");
                Socket socket = server.accept();
                System.out.println("Клиент с портом " + socket.getPort() + " подключен.");
                ConnectionThread con = new ConnectionThread(socket);
                connections.add(con);
                con.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeAll();
        }
    }

    private void closeAll() {
        try {
            server.close();

            synchronized (connections) {
                for (ConnectionThread connection : connections) {
                    (connection).close();
                }
            }
        } catch (Exception e) {
            System.out.println("Doh', error");
        }
    }

    private class ConnectionThread extends Thread {
        private BufferedReader in;
        private PrintWriter out;
        private Socket socket;
        private List<String> blockList = new ArrayList<String>();
        private String name = "";
        ConnectionThread(Socket socket) {
            this.socket = socket;

            try {
                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

            } catch (IOException e) {
                e.printStackTrace();
                close();
            }
        }

        @Override
        public void run() {
            try {
                name = in.readLine();
                synchronized (connections) {
                    for (ConnectionThread connection : connections) {
                        if (!(connection.name.equals(this.name)))
                            connection.out.println(name + " cames now");
                    }
                }
                String str;
                while (true) {
                    str = in.readLine();
                    if (str.equals("@exit")) break;

                    synchronized (connections) {
                        if (str.contains("@blockuser")) {
                            String[] nameUser = str.split(" ");
                            if (nameUser.length < 2) continue;
                            blockList.add(nameUser[1]);
                            continue;
                        }
                        if (str.contains("@unblockuser")) {
                            String[] nameUser = str.split(" ");
                            if (nameUser.length < 2) continue;
                            blockList.remove(nameUser[1]);

                            continue;
                        }
                        if (str.contains("@senduser")) { //внимание костыли
                            String[] data = str.split(" ");
                            if (data.length <= 2) continue;
                            str = str.substring(data[0].length() + data[1].length() + 2);
                            for (ConnectionThread connection : connections) {
                                if (connection.name.equals(data[1]) && !connection.blockList.contains(this.name))
                                    connection.out.println(name + ": " + str);
                            }
                        } else {
                            for (ConnectionThread connection : connections) {
                                if (!connection.name.equals(this.name) && !connection.blockList.contains(this.name))
                                    connection.out.println(name + ": " + str);
                            }
                        }
                    }
                }
                synchronized (connections) {
                    for (ConnectionThread connection : connections) {
                        if (!(connection.name.equals(this.name)))
                            connection.out.println(name + " has left");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close();
            }
        }

        void close() {
            try {
                in.close();
                out.close();
                socket.close();
                connections.remove(this);
                if (connections.size() == 0) {
                    ServerTCP.this.closeAll();
                    System.exit(0);
                }
            } catch (Exception e) {
                System.out.println("Doh', error");
            }
        }
    }
}