//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class server {
    private static final int PORT = 5000;
    private static final Set<SocketChannel> clients = new HashSet();

    public server() {
    }

    public static void main(String[] args) {
        try {
            Selector selector = Selector.open();

            try {
                ServerSocketChannel serverChannel = ServerSocketChannel.open();

                try {
                    serverChannel.bind(new InetSocketAddress(5000));
                    serverChannel.configureBlocking(false);
                    serverChannel.register(selector, 16);

                    while(true) {
                        selector.select();
                        Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

                        while(keyIterator.hasNext()) {
                            SelectionKey key = (SelectionKey)keyIterator.next();
                            keyIterator.remove();
                            if (key.isAcceptable()) {
                                acceptClient(selector, serverChannel);
                            } else if (key.isReadable()) {
                                readAndBroadcastMessage(key);
                            }
                        }
                    }
                } catch (Throwable var7) {
                    if (serverChannel != null) {
                        try {
                            serverChannel.close();
                        } catch (Throwable var6) {
                            var7.addSuppressed(var6);
                        }
                    }

                    throw var7;
                }
            } catch (Throwable var8) {
                if (selector != null) {
                    try {
                        selector.close();
                    } catch (Throwable var5) {
                        var8.addSuppressed(var5);
                    }
                }

                throw var8;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void acceptClient(Selector selector, ServerSocketChannel serverChannel) throws IOException {
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, 1);
        clients.add(clientChannel);
        System.out.println("Ny klient ansluten: " + String.valueOf(clientChannel.getRemoteAddress()));
        sendMessage(clientChannel, "SERVER_EFTERFR_ANVNAMN\n");
        broadcastMessage("En ny användare har anslutit!\n", clientChannel);
    }

    private static void readAndBroadcastMessage(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel)key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        try {
        int bytesRead = clientChannel.read(buffer);
        if (bytesRead == -1) {
            clients.remove(clientChannel);
            System.out.println("Klient kopplade från: " + String.valueOf(clientChannel.getRemoteAddress()));
            broadcastMessage("En användare har lämnat chatten.", clientChannel);
            clientChannel.close();
            key.cancel();
        } else {
            buffer.flip();
            String message = new String(buffer.array(), 0, buffer.limit());
            broadcastMessage(message, clientChannel);
        } } catch (IOException e) {

            System.out.println("[SERVER] Klient avslutades abrupt.");
            clients.remove(clientChannel);
            clientChannel.close();
            key.cancel();

        }
    }

    private static void broadcastMessage(String message, SocketChannel sender) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap((message).getBytes());

        for(SocketChannel client : clients) {
            if (client != sender) {
                client.write(buffer.duplicate());
            }
        }

    }

    private static void sendMessage(SocketChannel client, String message) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        client.write(buffer);
    }
}
