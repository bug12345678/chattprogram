import java.io.*;
import java.net.*;


public class server {
    public static void main(String[] args) {

        try {
            // Skapa en server socket på port 12345
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Servern väntar på anslutningar...");

            // Vänta på att en klient ansluter
            Socket socket = serverSocket.accept();
            System.out.println("En klient anslöt!");

            // Skapa in- och utström för att läsa och skriva data
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Läs data från klienten
            String message = in.readLine();
            System.out.println("Meddelande från klient: " + message);

            // Skicka svar till klienten
            out.println("Meddelandet mottogs!");

            // Stäng anslutningen
            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
