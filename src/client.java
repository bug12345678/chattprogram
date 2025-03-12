import java.io.*;
import java.net.*;

public class client {
    public static void main(String ip) throws IOException {
        Socket socket = new Socket(ip, 5000);
        System.out.println("Ansluten till chattservern!");

        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

        // Starta en tråd för att lyssna på inkommande meddelanden
        new Thread(() -> {
            try {
                String serverMessage;
                while ((serverMessage = input.readLine()) != null) {
                    System.out.println(serverMessage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // Huvudtråd för att skicka meddelanden
        String userInput;
        while ((userInput = consoleInput.readLine()) != null) {
            output.println(userInput);
            if (userInput.equalsIgnoreCase("exit")) {
                break;
            }
        }

        socket.close();
        System.out.println("Du har lämnat chatten.");
    }
}
