package ru.skobaro;

import java.io.IOException;
import java.net.*;
import java.nio.file.Path;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Server {
    private final int port;
    private final Path contentDirectory;

    public Server(int port, Path contentDirectory) {
        this.port = port;
        this.contentDirectory = contentDirectory;
    }

    public void start() {
        configureShutdownHook();

        try (var server = new ServerSocket(port)) {
            System.out.printf("[INFO] Server started. ip: %s port: %d%n", server.getInetAddress().getHostAddress(), server.getLocalPort());
            while (true) {
                Socket client = server.accept();
                new Thread(new ClientConnection(client, contentDirectory)).start();
            }
        } catch (IllegalArgumentException | IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("[INFO] Server shutdown");
        }
    }

    private void configureShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                System.out.println("[INFO] Server shutdown. Reason: SIGINT or SIGTERM")));
    }
}
