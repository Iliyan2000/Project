package bg.sofia.uni.fmi.mjt.dungeons.server;

import bg.sofia.uni.fmi.mjt.dungeons.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.dungeons.command.commandexecutor.CommandExecutor;
import bg.sofia.uni.fmi.mjt.dungeons.command.answer.ReceiversAnswer;
import bg.sofia.uni.fmi.mjt.dungeons.command.answer.SenderReceivers;
import bg.sofia.uni.fmi.mjt.dungeons.exceptions.ClientException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class Server {
    private static final String FIELD_START_MESSAGE = "failed to start server";
    private static final String EXCEPTION_LOG_FILE_NAME = "server_exception_log.txt";
    private static final String EXCEPTION_HANDLED = "Exception handled caused by: ";

    private static final char TERMINAL_CHARACTER = (char) 0xffff;
    private static final int BUFFER_SIZE = 1024;
    private static final String HOST = "localhost";

    private final CommandExecutor commandExecutor;

    private final int port;
    private boolean isServerWorking;

    private ByteBuffer buffer;
    private Selector selector;

    public Server(int port, CommandExecutor commandExecutor) {
        this.port = port;
        this.commandExecutor = commandExecutor;
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            selector = Selector.open();
            configureServerSocketChannel(serverSocketChannel, selector);
            commandExecutor.getConnector().setSelector(selector);
            this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
            isServerWorking = true;
            while (isServerWorking) {
                try {

                    int readyChannels = selector.select();
                    if (readyChannels == 0) {
                        writeOutput(commandExecutor.minionAttack());
                        continue;
                    }

                    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        if (key.isReadable()) {

                            SocketChannel channel = (SocketChannel) key.channel();
                            String channelName = channel.getRemoteAddress().toString();

                            try {
                                String clientInput = getClientInput(channel);

                                if (clientInput == null) {
                                    ReceiversAnswer refreshedField = commandExecutor.removePlayer(channelName);
                                    writeOutput(new SenderReceivers(null, refreshedField));
                                    continue;
                                }

                                SenderReceivers reply = commandExecutor
                                        .execute(CommandCreator.newCommand(clientInput), channelName);
                                writeOutput(reply);

                            } catch (IllegalArgumentException e) {
                                writeClientOutput(channel, e.getMessage());
                            } catch (Exception e) {
                                throw new ClientException(channelName, e);
                            }

                        } else if (key.isAcceptable()) {
                            if (commandExecutor.isLimitReached()) {
                                key.cancel();
                                continue;
                            }

                            SocketChannel newPlayerChannel = accept(selector, key);
                            ReceiversAnswer refreshedField = commandExecutor
                                    .addPlayer(newPlayerChannel.getRemoteAddress().toString());
                            writeOutput(new SenderReceivers(null, refreshedField));
                        }

                        keyIterator.remove();
                    }
                } catch (IOException | ClientException e) {
                    exceptionHandler(e);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(FIELD_START_MESSAGE, e);
        }
    }

    public void stop() {
        this.isServerWorking = false;
        if (selector.isOpen()) {
            selector.wakeup();
        }
    }

    private void configureServerSocketChannel(ServerSocketChannel channel, Selector selector) throws IOException {
        channel.bind(new InetSocketAddress(HOST, this.port));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private String getClientInput(SocketChannel clientChannel) throws IOException {
        buffer.clear();

        try {
            clientChannel.read(buffer);
        } catch (SocketException e) {
            clientChannel.close();
            return null;
        }

        buffer.flip();

        byte[] clientInputBytes = new byte[buffer.remaining()];
        buffer.get(clientInputBytes);

        return new String(clientInputBytes, StandardCharsets.UTF_8).trim();
    }

    private void writeClientOutput(SocketChannel clientChannel, String output) throws IOException {
        output += TERMINAL_CHARACTER;

        buffer.clear();
        buffer.put(output.getBytes());
        buffer.flip();

        clientChannel.write(buffer);
    }

    private void writeOutput(SenderReceivers output) throws IOException {
        Iterator<SelectionKey> keyIterator = selector.keys().iterator();
        while (keyIterator.hasNext()) {

            SocketChannel channel = null;
            try {
                channel = (SocketChannel) keyIterator.next().channel();
            } catch (ClassCastException e) {
                continue;
            }

            if (channel.isConnected()) {
                String channelName = channel.getRemoteAddress().toString();

                if (output.sender() != null && output.sender().ID().equals(channelName)) {
                    writeClientOutput(channel, output.sender().answer());

                } if (output.receivers() != null && output.receivers().IDs().contains(channelName)) {
                    writeClientOutput(channel, output.receivers().answer());
                }
            }
        }
    }

    private SocketChannel accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();

        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);

        return accept;
    }

    private void exceptionHandler(Exception e) {

        try (var writer = new PrintWriter(new FileWriter(EXCEPTION_LOG_FILE_NAME))) {
            e.printStackTrace(writer);
            System.out.println(EXCEPTION_HANDLED + e.getMessage());
        } catch (IOException ignored) {
        }

    }
}