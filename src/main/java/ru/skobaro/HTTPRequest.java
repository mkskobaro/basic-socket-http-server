package ru.skobaro;

import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import ru.skobaro.exceptions.HTTPRequestException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Getter
public class HTTPRequest {
    private final String method;
    private final URI uri;
    private final String version;
    private final Map<String, String> headers = new HashMap<>();
    private Byte[] data = new Byte[0];

    HTTPRequest(InputStream stream) throws HTTPRequestException {
        ArrayList<Byte> bytes = new ArrayList<>();
        List<Byte> crlfDoubled = Arrays.asList((byte) 13, (byte) 10, (byte) 13, (byte) 10);
        int read;
        try (var input = new BufferedInputStream(new UncloseableInputStream(stream))) {
            while (Collections.indexOfSubList(bytes, crlfDoubled) == -1) {
                read = input.read();
                if (read == -1) continue;
                bytes.add((byte) read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String request = StandardCharsets.ISO_8859_1.decode(
                ByteBuffer.wrap(ArrayUtils.toPrimitive(
                        bytes.toArray(new Byte[0])))).toString();

        if (!request.contains("\r\n")) {
            throw new HTTPRequestException("Line endings should be <CRLF>");
        }

        String[] requestPieces = request.split("\r\n");
        if (requestPieces.length < 1) {
            throw new HTTPRequestException("Empty message");
        }

        String[] startingLine = requestPieces[0].split(" ");
        if (startingLine.length != 3) {
            throw new HTTPRequestException("Bad starting line of request. Starting line:" + requestPieces[0]);
        }

        uri = URI.create(startingLine[1]);
        method = startingLine[0];
        version = startingLine[2];

        if (!method.equals("GET")) {
            throw new HTTPRequestException("Unsupported HTTP method. Method: " + method);
        }
        if (!version.equals("HTTP/1.0") && !version.equals("HTTP/1.1")) {
            throw new HTTPRequestException("Unsupported HTTP version. Version: " + version);
        }

        for (int i = 1; i < requestPieces.length; i++) {
            if (requestPieces[i].equals("") && requestPieces.length > i + 1) {
                var parsedData = new ArrayList<Byte>();
                for (int j = i + 1; j < requestPieces.length; j++) {
                    for (var eachByte : requestPieces[j].getBytes()) {
                        parsedData.add(eachByte);
                    }
                    parsedData.add((byte) '\r');
                    parsedData.add((byte) '\n');
                }
                data = parsedData.toArray(data);
                return;
            }

            String[] header = requestPieces[i].split(": ");
            if (header.length != 2) {
                throw new HTTPRequestException("Bad header syntax. Header: " + requestPieces[i]);
            }
            headers.put(header[0], header[1]);
        }
    }
}
