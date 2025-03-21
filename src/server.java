import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

public class server {
    private static final int PORT = 5000;
    private static final Set<SocketChannel> clients = new HashSet<>();
    private static final Map<SocketChannel, String> anvNamn = new HashMap<>();

    public static void main(String[] args) {
        try (Selector selector = Selector.open();
             ServerSocketChannel serverChannel = ServerSocketChannel.open()) {

            serverChannel.bind(new InetSocketAddress(PORT));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Server startad och väntar på anslutningar...");

            while (true) {
                selector.select(); // Väntar på händelser

                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    if (key.isAcceptable()) {
                        acceptClient(selector, serverChannel);
                    } else if (key.isReadable()) {
                        handleClientMessage(key);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void acceptClient(Selector selector, ServerSocketChannel serverChannel) throws IOException {
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
        clients.add(clientChannel);

        System.out.println("Ny klient ansluten: " + clientChannel.getRemoteAddress());
        sendMessage(clientChannel, "SERVER_EFTERFR_ANVNAMN");
        System.out.println("[SERVER] Efterfrågar användarnamn från klienten.");
    }

    private static void handleClientMessage(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = clientChannel.read(buffer);

        if (bytesRead == -1) {
            disconnectClient(clientChannel);
            key.cancel();
            return;
        }

        buffer.flip();
        String message = new String(buffer.array(), 0, buffer.limit()).trim();

        if (!anvNamn.containsKey(clientChannel)) {
            // Första meddelandet är användarnamnet
            anvNamn.put(clientChannel, message);
            System.out.println("Användarnamn registrerat: " + message);
            sendMessage(clientChannel, "Välkommen " + message + "!");
            broadcastMessage(message + " har anslutit till chatten.", clientChannel);
        } else {
            System.out.println(anvNamn.get(clientChannel) + ": " + message);
            broadcastMessage(anvNamn.get(clientChannel) + ": " + message, clientChannel);
        }
    }

    private static void disconnectClient(SocketChannel clientChannel) throws IOException {
        clients.remove(clientChannel);
        String username = anvNamn.getOrDefault(clientChannel, "En anonym användare");
        System.out.println(username + " kopplade från.");
        broadcastMessage(username + " har lämnat chatten.", clientChannel);
        clientChannel.close();
        anvNamn.remove(clientChannel);
    }

    private static void broadcastMessage(String message, SocketChannel sender) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(("SERVER: " + message).getBytes());

        for (SocketChannel client : clients) {
            if (client != sender) {
                buffer.rewind(); // Återställ buffertens position
                client.write(buffer);
            }
        }
    }

    private static void sendMessage(SocketChannel client, String message) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        client.write(buffer);
    }
}
