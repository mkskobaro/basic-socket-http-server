package ru.skobaro;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

@AllArgsConstructor
public class HTTPRequestHandler {
    private final HTTPRequest httpRequest;
    private final OutputStream os;
    private final Path serverContentFolder;

    public void handle() throws IOException {
        File located = Path.of(serverContentFolder.toAbsolutePath().toString(), httpRequest.getUri().getPath()).toFile();
        try {
            if (httpRequest.getUri().getQuery() != null && httpRequest.getUri().getQuery().contains("info=yes")) {
                HTTPInfoResponse httpResponse = new HTTPInfoResponse(200);
                httpResponse.readInfoDataFromRequestHeaders(httpRequest.getHeaders());
                os.write(httpResponse.getResponse().array());
            } else {
                if (!located.exists() || !located.isFile()) {
                    throw new FileNotFoundException("File not found");
                }
                HTTPResponse httpResponse = new HTTPResponse(200);
                httpResponse.readDataFromFile(located);
                os.write(httpResponse.getResponse().array());
            }
        } catch (SecurityException e) {
            HTTPResponse httpResponse = new HTTPResponse(403);
            httpResponse.addHeader("Content-Type", "text/plain");
            httpResponse.setData(ArrayUtils.toObject("Cannot access. Forbidden\r\n".getBytes()));
            os.write(httpResponse.getResponse().array());
        } catch (FileNotFoundException e) {
            HTTPResponse httpResponse = new HTTPResponse(404);
            httpResponse.addHeader("Content-Type", "text/plain");
            httpResponse.setData(ArrayUtils.toObject((e.getMessage() + "\r\n").getBytes()));
            os.write(httpResponse.getResponse().array());
        }
    }
}
