import java.io.*;
import java.net.Socket;

public class client {
    public static void main(String ip) {
        String serverIP = "127.0.0.1"; // Ändra om servern körs på en annan IP
        int port = 5000;

        try (Socket socket = new Socket(serverIP, port);
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("[KLIENT] Ansluten till chattservern!");

            // Vänta på serverns meddelanden
            String serverMessage;
            while ((serverMessage = input.readLine()) != null) {
                System.out.println("[SERVER] " + serverMessage);

                if (serverMessage.equals("SERVER_EFTERFR_ANVNAMN")) {
                    System.out.print("Skriv ditt användarnamn: ");
                    String anvnamn = consoleInput.readLine();
                    System.out.println("[KLIENT] Skickar användarnamn: " + anvnamn);
                    output.println(anvnamn);  // Skicka användarnamnet till servern
                    output.flush();
                } else {
                    // Skriv ut övriga meddelanden
                    System.out.println(serverMessage);
                }
            }
        } catch (IOException e) {
            System.err.println("[KLIENT] Anslutningen till servern förlorades.");
        }
    }
}
