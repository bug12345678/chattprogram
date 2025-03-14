import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        Scanner userchoice = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Starta (k)lient eller (s)erver? ");
        String val = userchoice.nextLine();  // Read user input

        switch (val){
            case "k":
                System.out.println("Klient");
                Scanner ipval = new Scanner(System.in);  // Create a Scanner object
                System.out.println("Skriv in serverns ipadress ");
                String ip = ipval.nextLine();  // Read user input


                try {
                    client.main(ip);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            case "s":
                System.out.println("Server");
                new Thread(() -> {
                    try { // Error
                        server.main();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
                System.out.println("Startat ny tråd");

                System.out.println("Server skapad, kopplar...");
                Thread.sleep(2000);
                System.out.println("Startar klient");
                try {
                    client.main("localhost");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Server startad och kopplad. Välkommen!");
            case "e":
                System.out.println("Endast server");
                try {
                    server.main();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            default:
                System.exit(0);
        }
    }
}
