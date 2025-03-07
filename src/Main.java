import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Scanner userchoice = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Starta (k)lient eller (s)erver? ");
        String val = userchoice.nextLine();  // Read user input

        if (val.equals("k")){
            System.out.println("Klient");
            Scanner ipval = new Scanner(System.in);  // Create a Scanner object
            System.out.println("Skriv in serverns ipadress ");
            String ip = ipval.nextLine();  // Read user input


            client.main(ip);
        }
        if (val.equals("s")){
            System.out.println("Server");
            new Thread(() -> {
                server.main(null);
            }).start();
            System.out.println("Server skapad, kopplar...");
            Thread.sleep(2000);
            System.out.println("Startar klient");
            client.main("localhost");
            System.out.println("Server startad och kopplad. Välkommen!");
        }


    }
}