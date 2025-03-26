import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class client {
    public client() {
    }

    public static void main(String ip) throws IOException {
        // Use BufferedReader to read from console input instead of Scanner
        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

        // Ask for the username
        System.out.println("Välj ett användarnamn:");
        String anvnamn = consoleInput.readLine(); // Read the username
        System.out.println("[KLIENT] Användarnamn: " + anvnamn);

        // Set up socket connection
        Socket socket = new Socket(ip, 5000);
        System.out.println("[KLIENT] Ansluten till chattservern!");

        // Set up input/output streams for socket
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

        // Thread to listen for server messages
        new Thread(() -> {
            try {
                String serverMessage;
                while ((serverMessage = input.readLine()) != null) {


                    switch (serverMessage) {
                        case "SERVER_EFTERFR_ANVNAMN":
                            output.println(anvnamn);  // Send username to server
                            break;
                        case "SERVER_EXIT":
                            return;  // Exit the thread if the server asks
                        default:
                            System.out.println(serverMessage);
                    }
                }
                System.out.println("Server Closed");
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // Main loop to send user input to the server
        String userInput;
        while ((userInput = consoleInput.readLine()) != null) {
            output.println(anvnamn + ": " + userInput);  // Send message to the server
            if (userInput.equalsIgnoreCase("exit")) {
                break;
            }
        }

        socket.close();
        System.out.println("[KLIENT] Du har lämnat chatten.");
    }
}