import java.io.IOException;
import java.util.Scanner;

public class Main {
    public Main() {
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        Scanner userchoice = new Scanner(System.in);  // Don't close this Scanner
        System.out.println("Starta (k)lient eller (s)erver? ");
        String val = userchoice.nextLine();

        switch (val) {
            case "k":
                System.out.println("Klient");
                Scanner ipval = new Scanner(System.in);

                // Get the IP address directly
                System.out.println("Skriv in serverns ipadress ");
                String ip = ipval.nextLine();

                try {
                    client.main(ip);  // Call the client's main method with the IP
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;

            case "s":
                System.out.println("Server");
                new Thread(() -> server.main((String[])null)).start(); // Start server in a new thread
                System.out.println("Startat ny tråd");
                System.out.println("Server skapad, kopplar...");
                Thread.sleep(2000L); // Wait for 2 seconds
                System.out.println("Startar klient");

                try {
                    client.main("localhost");  // Start client and connect to localhost
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                System.out.println("Server startad och kopplad. Välkommen!");
                break;

            case "e":
                System.out.println("Endast server");
                server.main((String[])null);  // Only start the server
                break;

            default:
                System.exit(0);  // Exit program if invalid input
        }
    }
}