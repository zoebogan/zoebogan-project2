import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.concurrent.*;
import java.util.*;

public class Server {
    private ServerSocket sock;
    private ArrayList<LocalDateTime> connectionTimes; 
    
    public Server(int port) throws IOException {
        sock = new ServerSocket(port);
        connectionTimes = new ArrayList<>();
    }

    public void serve(int activeClients) {
        try {
            for (int i = 0; i < activeClients; i++) {
                Socket clientSocket = sock.accept();
                ClientHandler client = new ClientHandler(clientSocket);
                client.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler extends Thread {
        private Socket sock;
        private PrintWriter outgoing;
        private BufferedReader incoming;
        
        public ClientHandler(Socket sock) {
            this.sock = sock;
        }
        
        public void run() {
            try {
                outgoing = new PrintWriter(sock.getOutputStream(), true);
                incoming = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                String passcode = incoming.readLine();
                if (passcode.equals("12345")) {
                    connectionTimes.add(LocalDateTime.now());
                    String message = incoming.readLine();
                    
                    try {
                        int number = Integer.parseInt(message);
                        String response = factorize(number);
                        outgoing.println(response);
                    } catch (NumberFormatException e) {
                        outgoing.println("There was an exception on the server");
                    }
                } else {
                    outgoing.println("couldn't handshake");
                }
                sock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public ArrayList<LocalDateTime> getConnectedTimes() {
        return connectionTimes;
    }

    public String factorize(int number) {
        int count = 0;
        for (int i = 1; i <= number; i++) {
            if (number % i == 0) {
                count++;
            }
        }
        return "The number " + number + " has " + count + " factors";
    }

    public void disconnect() {
        try {
            sock.close();
            System.out.println("Server stopped");
        } catch (IOException e) {
            System.err.println("Error stopping server");
            e.printStackTrace();
        }
    }
}