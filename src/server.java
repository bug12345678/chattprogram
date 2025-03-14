import java.io.*;
import java.net.*;
import java.util.*;

public class server {
    private static Set<PrintWriter> clientOutputs = Collections.synchronizedSet(new HashSet<>());

    public static void main() throws IOException {
        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("[SERVER] Chattservern startad på port 5000...");

        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void close(){
                try {
                    ClientHandler.broadcastMessage("SERVER_EXIT");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    serverSocket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        while (true) {
            Socket clientSocket = serverSocket.accept(); // Väntar på en ny klient
            System.out.println("[SERVER] Ny klient ansluten: " + clientSocket.getInetAddress());

            // Skapa en ny tråd för att hantera klienten
            new ClientHandler(clientSocket).start();
        }

    }

    static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter output;
        private BufferedReader input;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);

                // Lägg till klientens output stream i listan
                synchronized (clientOutputs) {
                    clientOutputs.add(output);
                }

                output.println("[SERVER] Välkommen till chatten! Skriv 'exit' för att lämna.");

                String message;
                while ((message = input.readLine()) != null) {
                    if (message.equalsIgnoreCase("exit")) {
                        break;
                    }
                    System.out.println("[SERVER] Meddelande från klient: " + message);
                    broadcastMessage("[SERVER] Klient: " + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clientOutputs) {
                    clientOutputs.remove(output);
                }
                System.out.println("[SERVER] En klient har lämnat.");
            }
        }

        public static void broadcastMessage(String message) {
            synchronized (clientOutputs) {
                for (PrintWriter writer : clientOutputs) {
                    writer.println(message);
                }
            }
        }
    }
}