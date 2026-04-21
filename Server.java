import java.io.*;
import java.net.*;
import java.time.*;
import java.util.*;

public class Server {
    private ServerSocket serverSocket; 
    private ArrayList<LocalDateTime> connectedTimes; 

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port); 
        connectedTimes = new ArrayList<>(); 
    }

    public void serve(int clients) {
        ArrayList<Thread> threads = new ArrayList<>();

        for(int i = 0; i < clients; i++) {
            try {
                Socket client = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);

                String key = in.readLine();

                if (!"12345".equals(key)) {
                    out.println("couldn't handshake");
                    out.flush();
                    client.close();
                    continue;
                }

                synchronized (connectedTimes) {
                    connectedTimes.add(LocalDateTime.now());
                }

                Thread t = new Thread(() -> handleClient(client));
                threads.add(t); 
                t.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for(Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}