import java.io.*;
import java.net.*;
import java.util.Scanner;

public class client {
    public static void main(String ip) throws IOException {
        Scanner namn = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Välj ett användarnamn:");
        String anvnamn = namn.nextLine();  // Read user input
        namn.close();

        Socket socket = new Socket(ip, 5000);
        System.out.println("[KLIENT] Ansluten till chattservern!");

        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));



        // Starta en tråd för att lyssna på inkommande meddelanden
        new Thread(() -> {
            try {
                String serverMessage;
                Boolean close = Boolean.FALSE;
                while ((serverMessage = input.readLine()) != null) {
                    switch (serverMessage){
                        case "SERVER_EFTERFR_ANVNAMN":
                            output.println(anvnamn);
                            break;
                        case "SERVER_EXIT":
                            close = true;
                            break;
                    }

                    System.out.println(serverMessage);
                }
                System.out.println("Server Closed");
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // Huvudtråd för att skicka meddelanden
        String userInput;
        while ((userInput = consoleInput.readLine()) != null) {
            output.println(anvnamn +": " + userInput);
            if (userInput.equalsIgnoreCase("exit")) {

                break;
            }
        }

        socket.close();
        System.out.println("[KLIENT] Du har lämnat chatten.");
    }
}
