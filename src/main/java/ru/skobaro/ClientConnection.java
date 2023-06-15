package ru.skobaro;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ArrayUtils;
import ru.skobaro.exceptions.HTTPRequestException;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;

@AllArgsConstructor
public class ClientConnection implements Runnable {
    private final Socket client;
    private final Path serverContentFolder;

    @SneakyThrows
    @Override
    public void run() {
        System.out.printf("[INFO] Client connected. %s:%d%n", client.getInetAddress().getHostAddress(), client.getPort());
        try {
            new HTTPRequestHandler(new HTTPRequest(client.getInputStream()), client.getOutputStream(), serverContentFolder).handle();
        } catch (HTTPRequestException e) {
            HTTPResponse httpResponse = new HTTPResponse(501);
            httpResponse.addHeader("Content-Type", "text/plain");
            httpResponse.setData(ArrayUtils.toObject(((e.getMessage()) + "\r\n").getBytes()));
            client.getOutputStream().write(httpResponse.getResponse().array());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            client.close();
            System.out.printf("[INFO] Client disconnected. %s:%d%n", client.getInetAddress().getHostAddress(), client.getPort());
        }
    }
}
