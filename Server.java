import java.io.*;
import java.net.*;
import java.time.*;
import java.util.*;

public class Server {
    // socket that listens from incoming client connections 
    private ServerSocket serverSocket; 
    // stores the time each client successfully connects in order 
    private ArrayList<LocalDateTime> connectedTimes; 

    // opens the server on specified port 
    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port); 
        connectedTimes = new ArrayList<>(); 
    }

    // accepts and serves exactly the number sent in 
    public void serve(int clients) {
        ArrayList<Thread> threads = new ArrayList<>();

        for(int i = 0; i < clients; i++) {
            try {
                // waits for a client to connect 
                Socket client = serverSocket.accept();
                // stream to read the handshake and possibly reply 
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);

                // very first message must be 12345
                String key = in.readLine();

                // if handshake is wrong, must reject the client 
                if (!"12345".equals(key)) {
                    out.println("couldn't handshake");
                    out.flush();
                    client.close();
                    continue;
                }

                // if the handshake is valid, must record the time connected
                synchronized (connectedTimes) {
                    connectedTimes.add(LocalDateTime.now());
                }

                // starts a new thread to handle factorization requests
                Thread t = new Thread(() -> handleClient(client));
                threads.add(t); 
                t.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // waits for all client threads to finish 
        for(Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // handles one client after each successful handshake 
    private void handleClient(Socket client) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            
            // reads the number request from the client 
            String request = in.readLine(); 
            // parsing the int from a String 
            int n = Integer.parseInt(request);
            // number of positive factors 
            int factors = countFactors(n); 

            // sends the required response 
            out.println("The number " + n + " has " + factors + " factors");
           
            out.flush(); 
            client.close();
        } catch (Exception e) {
            try {
                // usually when the number is too big 
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                out.println("There was an exception on the server");
                out.flush(); 
                client.close();
            } catch (IOException ignored) {
            }
        }
    }

    public void disconnect() {
        try {
            if(serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}