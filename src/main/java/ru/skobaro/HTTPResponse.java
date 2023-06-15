package ru.skobaro;

import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class HTTPResponse {
    private static final HashMap<Integer, String> statusTexts = new HashMap<>();
    protected static final LinkedHashMap<String, String> MIMETypes = new LinkedHashMap<>();

    static {
        statusTexts.put(200, "OK");
        statusTexts.put(400, "Bad Request");
        statusTexts.put(404, "Not Found");
        statusTexts.put(405, "Method Not Allowed");
        statusTexts.put(501, "Not Implemented");
    }

    static {
        MIMETypes.put("txt", "text/plain");

        MIMETypes.put("htm", "text/html");
        MIMETypes.put("html", "text/html");
        MIMETypes.put("css", "text/css");
        MIMETypes.put("js", "text/javascript");

        MIMETypes.put("jpg", "image/jpeg");
        MIMETypes.put("png", "image/x-png");
        MIMETypes.put("gif", "image/gif");
        MIMETypes.put("ico", "image/x-icon");
    }

    private final int statuscode;
    private final String statustext;
    private final LinkedHashMap<String, String> headers = new LinkedHashMap<>();
    @Setter
    protected Byte[] data;

    public HTTPResponse(int statuscode) {
        this.statuscode = statuscode;
        statustext = statusTexts.get(statuscode);
    }

    public void addHeader(String header, String value) {
        headers.put(header, value);
    }

    public void readDataFromFile(File file) throws IOException {
        addHeader("Content-Type", MIMETypes.get(FilenameUtils.getExtension(file.getName())));
        data = ArrayUtils.toObject(Files.readAllBytes(file.toPath()));
    }

    public ByteBuffer getResponse() {
        Charset iso88591 = StandardCharsets.ISO_8859_1;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss O");
        headers.put("Date", dateFormatter.format(ZonedDateTime.now()));
        headers.put("Server", "bhttpd");
        if (data != null) {
            headers.put("Content-Length", String.valueOf(data.length));
        }

        StringBuilder responseBuilder = new StringBuilder();
        String version = "HTTP/1.0";
        responseBuilder.append("%s %d %s\r\n".formatted(version, statuscode, statustext));
        for (var header : headers.entrySet()) {
            responseBuilder.append("%s: %s\r\n".formatted(header.getKey(), header.getValue()));
        }
        responseBuilder.append("\r\n");
        ByteBuffer bytes = iso88591.encode(responseBuilder.toString());

        return ByteBuffer.allocate(bytes.capacity() + data.length).put(bytes).put(ArrayUtils.toPrimitive(data));
    }
}
