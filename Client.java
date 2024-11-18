import java.util.*;
import java.net.*;
import java.io.*;

public class Client {
    private Socket sock;
    private PrintWriter outgoing;
    private BufferedReader incoming;

    public Client(String hostname, int port) throws IOException {
        this.sock = new Socket(hostname, port);
        this.incoming = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        this.outgoing = new PrintWriter(sock.getOutputStream(), true);
    }

    public Socket getSocket() {
        return sock;
    }

    public void handshake() {
        outgoing.println("12345");
        outgoing.flush();
    }

    public String request(String factorizeNumber) throws IOException {
        outgoing.println(factorizeNumber);
        String response = incoming.readLine();
        return response;
    }

    public void disconnect() {
        try {
            incoming.close();
            outgoing.close();
            sock.close();
        } catch (IOException e) {
            System.err.println("Error occured during disconnecting " + e.getMessage());
            System.exit(1);
        }
    }
}
