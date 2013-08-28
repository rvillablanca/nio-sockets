package cl.rvillablanca;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class EchoServer {

    private ServerSocketChannel server;
    private Selector selector;
    private SelectionKey selection;

    private EchoServer() throws IOException {
        selector = Selector.open();
        server = ServerSocketChannel.open();
        server.configureBlocking(false);
        server.bind(new InetSocketAddress(4444));
        server.register(selector, SelectionKey.OP_ACCEPT);
        start();
    }

    private void start() throws IOException {
        while (true) {
            selector.select();
            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            while (keyIterator.hasNext()) {
                selection = keyIterator.next();
                keyIterator.remove();
                if (!selection.isValid()) {
                    invalid();
                } else if (selection.isAcceptable()) {
                    createClient();
                } else if (selection.isReadable()) {
                    read();
                } else if (selection.isWritable()) {
                    write();
                }
            }
        }
    }

    private void invalid() throws IOException {
        logln("Invalid selection");
        selection.channel().close();
        selection.cancel();
    }

    private void createClient() throws IOException {
        SocketChannel client = server.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }

    private void read() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        SocketChannel client = (SocketChannel) selection.channel();
        logln("Incoming:");
        int readed = client.read(buffer);
        if (readed == -1) {
            logln("Connection closed by client");
            client.close();
            selection.cancel();
            return;
        }
        while (readed != -1) {
            client.read(buffer);
            buffer.flip();
            log(new String(buffer.array(), "UTF-8"));
            buffer.clear();
        }
        selection.interestOps(SelectionKey.OP_WRITE);
    }

    private void write() {
    }

    private static void logln(String s) {
        System.out.println(s);
    }

    private static void log(String s) {
        System.out.print(s);
    }

    public static void main(String[] args) throws IOException {
        new EchoServer();
    }
}
