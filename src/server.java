import java.io.*;
import java.net.*;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.nio.*;

public class server {    private static final int PORT = 5000;
    Dictionary<SocketChannel, String> anvNamn = new Hashtable<>();
    private static final Set<SocketChannel> clients = new HashSet<>();

    public static void main(String[] args) {
        try (Selector selector = Selector.open();
             ServerSocketChannel serverChannel = ServerSocketChannel.open()) {

            serverChannel.bind(new InetSocketAddress(PORT));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                selector.select(); // Väntar på händelser

                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    if (key.isAcceptable()) {
                        acceptClient(selector, serverChannel);
                    } else if (key.isReadable()) {
                        readAndBroadcastMessage(key);
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
        broadcastMessage("En ny användare har anslutit!\n", clientChannel);
    }

    private static void readAndBroadcastMessage(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = clientChannel.read(buffer);

        if (bytesRead == -1) {
            clients.remove(clientChannel);
            System.out.println("Klient kopplade från: " + clientChannel.getRemoteAddress());
            broadcastMessage("En användare har lämnat chatten.", clientChannel);
            clientChannel.close();
            key.cancel();
            return;
        }

        buffer.flip();
        String message = new String(buffer.array(), 0, buffer.limit());
        System.out.println("Meddelande från klient " + message);

        broadcastMessage(message, clientChannel);
    }

    private static void broadcastMessage(String message, SocketChannel sender) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(("Klient säger: " + message).getBytes());

        for (SocketChannel client : clients) {
            if (client != sender) { // Skicka till alla förutom avsändaren
                client.write(buffer.duplicate());
            }
        }
    }

    private static void sendMessage(SocketChannel client, String message) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        client.write(buffer);
    }
}