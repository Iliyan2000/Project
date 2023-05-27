package bg.sofia.uni.fmi.mjt.dungeons.client;

import bg.sofia.uni.fmi.mjt.dungeons.exceptions.ConnectionLostException;
import bg.sofia.uni.fmi.mjt.dungeons.exceptions.ServerNotRespondingToIOException;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    static final char TERMINAL_CHARACTER = (char) 0xffff;

    private final int port;

    public Client(int port) {
        this.port = port;
    }

    public synchronized void start() {

        try (SocketChannel socketChannel = SocketChannel.open();
             BufferedReader reader = new BufferedReader(Channels.newReader(socketChannel, StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(Channels.newWriter(socketChannel, StandardCharsets.UTF_8), true);
             Scanner scanner = new Scanner(System.in)) {

            connect(socketChannel);
            System.out.println(Label.CONNECTION.get());

            var serverHandler = new ServerHandler(System.out, reader);
            var consoleHandler = new ConsoleHandler(scanner, writer);

            serverHandler.start();
            consoleHandler.start();
            this.wait();

            serverHandler.interrupt();
            consoleHandler.interrupt();
            System.out.println(Label.DISCONNECTION.get());

        } catch (IOException | InterruptedException e) {
            exceptionHandler(e);
        }
    }

    private void connect(SocketChannel channel) throws IOException {
        channel.connect(new InetSocketAddress(Label.HOST.get(), port));
    }

    private String getServerInput(BufferedReader reader) throws IOException {
        StringBuilder result = new StringBuilder(System.lineSeparator());

        char readCharacter = (char) reader.read();
        while (readCharacter != TERMINAL_CHARACTER) {
            result.append(readCharacter);
            readCharacter = (char) reader.read();
        }

        return result.toString();
    }

    private void writeServerOutput(PrintWriter writer, String output) throws IOException {
        writer.println(output);
    }

    private synchronized String getClientInput(Scanner scanner) {
        String response = scanner.nextLine();

        if (response.equals(Label.QUIT.get())) {
            this.notify();
        }

        return response;
    }

    private void writeClientOutput(PrintStream out, String output) {
        out.print(output);
        out.print(Label.CONSOLE_INPUT_SYMBOL.get());
    }

    private synchronized void exceptionHandler(Exception e) {
        try (var writer = new PrintWriter(new FileWriter(Label.EXCEPTION_LOG_FILE_NAME.get()))) {
            e.printStackTrace(writer);
            System.out.println(Label.IO_EXCEPTION_CLIENT.get());
        } catch (IOException ignored) {
        } finally {
            this.notify();
        }
    }

    private class ServerHandler extends Thread {
        private final PrintStream out;
        private final BufferedReader reader;

        ServerHandler(PrintStream out, BufferedReader reader) {
            this.out = out;
            this.reader = reader;
        }

        @Override
        public void run() {
            while (true) {
                String response = null;
                try {
                    response = getServerInput(reader);

                } catch (IOException e) {
                    if (!Thread.currentThread().isInterrupted()) {
                        exceptionHandler(new ServerNotRespondingToIOException(Label.CONNECTION_LOST.get(), e));
                    }
                    return;
                }

                writeClientOutput(out, response);
            }
        }
    }

    private class ConsoleHandler extends Thread {
        private final Scanner scanner;
        private final PrintWriter writer;

        ConsoleHandler(Scanner scanner, PrintWriter writer) {
            this.scanner = scanner;
            this.writer = writer;
        }

        @Override
        public void run() {
            String response = getClientInput(scanner);
            while (!response.equals(Label.QUIT.get())) {

                try {
                    writeServerOutput(writer, response);
                    response = getClientInput(scanner);

                } catch (IOException | IllegalStateException e) {
                    if (!Thread.currentThread().isInterrupted()) {
                        exceptionHandler(new ServerNotRespondingToIOException(Label.CONNECTION_LOST.get(), e));
                    }
                    return;
                }
            }
        }
    }
}