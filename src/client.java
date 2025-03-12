import java.io.*;
import java.net.*;

public class client {
    public static void main(String ip) {
        System.out.println("Klient startad");
        try {
            // Skapa en socket och anslut till servern på IP localhost och port 12345
            Socket socket = new Socket(ip, 5000); // Ändra IP-adressen till serverns IP

            // Skapa in- och utström för att skicka och ta emot data
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Skicka ett meddelande till servern
            out.println("Hej, server!");

            // Läs serverns svar
            String response = in.readLine();
            System.out.println("Svar från server: " + response);

            // Stäng anslutningen
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
