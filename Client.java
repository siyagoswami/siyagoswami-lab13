import java.io.*; 
import java.net.*; 

public class Client {
    // actual network connection to the server 
    private Socket socket; 
    // PrintWrtier to send text lines to the server 
    private PrintWriter out; 
    // BufferedReader to receive text lines from the server 
    private BufferedReader in; 

    // connects this client to the given host and port 
    public Client(String host, int port) throws IOException {
        // TCP connection to the server 
        socket = new Socket(host, port);
        
        // create output stream to send messages to the server
        out = new PrintWriter(socket.getOutputStream(), true); 

        // create input stream to read messages from the server
        in = new BufferedReader(new InputStreamReader(socket.getInputStream())); 

    }   

    // returns socket to see if client connected correctly 
    public Socket getSocket() {
        return socket; 
    }

    // sends required handshake code to the server 
    // required message must be 12345 
    public void handshake() {
        out.println("12345");
        out.flush(); 
    }

    // sends a number request to the server and reads the response 
    public String request(String number) throws IOException {
        // send the request line to the server 
        out.println(number); 
        out.flush(); 

        // waiting for a line back from the server
        return in.readLine();
    }

    // closes everything associated with this client 
    public void disconnect() throws IOException {
        if(in != null) {
            in.close(); 
        } 

        if(out != null) {
            out.close(); 
        }

        if(socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}