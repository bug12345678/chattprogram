import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        Scanner userchoice = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Starta (k)lient eller (s)erver? ");
        String val = userchoice.nextLine();  // Read user input
        userchoice.close();

        switch (val){
            case "k":
                System.out.println("Klient");
                Scanner ipval = new Scanner(System.in);  // Create a Scanner object
                System.out.println("Skriv in serverns ipadress ");
                String ip = ipval.nextLine();  // Read user input
                ipval.close();

                client.main(ip);

                break;

            case "s":
                System.out.println("Server");
                new Thread(() -> {
                    server.main(null);
                }).start();
                System.out.println("Startat ny tråd");

                System.out.println("Server skapad, kopplar...");
                Thread.sleep(2000);
                System.out.println("Startar klient");
                client.main("localhost");

                System.out.println("Server startad och kopplad. Välkommen!");
                break;
            case "e":
                System.out.println("Endast server");
                server.main(null);
                break;
            default:
                System.exit(0);
                break;
        }
    }
}
