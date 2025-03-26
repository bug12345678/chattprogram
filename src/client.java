import java.io.*;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class client {
    public static void main(String ip) {
        int port = 5000;
        String anvnamn = "";
        Scanner anvnamnin = new Scanner(System.in);
        try {
            System.out.println("[KLIENT] Ska läsa in användarnamnet.");
            anvnamn = anvnamnin.nextLine();
        } catch (NoSuchElementException e) {
            System.out.println("Fel: Ingen input hittades!");
        }

        try (Socket socket = new Socket(ip, port);
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) {




            System.out.println("[KLIENT] Ansluten till chattservern!");

            // Vänta på serverns meddelanden
            while (true) {
                System.out.println("[KLIENT] Ska läsa input.redline().");
                String serverMessage = input.readLine();
                System.out.println("[KLIENT] Har läst input.redline().");

                System.out.println("[KlIENT] Servern säger: " + serverMessage);
                System.out.println("[KLIENT] Ett Meddelande har mottagits och skrivits ut.");
                if (serverMessage.equals("SERVER_EFTERFR_ANVNAMN")) {
                    System.out.print("Skriv ditt användarnamn: ");
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
