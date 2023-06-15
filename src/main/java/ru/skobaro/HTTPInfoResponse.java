package ru.skobaro;

import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;

public class HTTPInfoResponse extends HTTPResponse {
    public HTTPInfoResponse(int statuscode) {
        super(statuscode);
    }

    @Override
    public void readDataFromFile(File file) {
        throw new UnsupportedOperationException("Can't read file");
    }

    public void readInfoDataFromRequestHeaders(Map<String, String> requestHeaders) {
        addHeader("Content-Type", MIMETypes.get("txt"));
        StringBuilder responseDataBuilder = new StringBuilder();
        try (var scanner = new Scanner(new FileReader("src/main/resources/headInfo.txt"))) {
            while (scanner.hasNextLine()) {
                String[] headerInfo = scanner.nextLine().split(": ");
                if (requestHeaders.containsKey(headerInfo[0])) {
                    responseDataBuilder.append("%s: %s\n".formatted(headerInfo[0], headerInfo[1]));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        responseDataBuilder.append("\n");
        data = ArrayUtils.toObject(StandardCharsets.ISO_8859_1.encode(responseDataBuilder.toString()).array());
    }
}
