package ru.skobaro;

import java.io.FilterInputStream;
import java.io.InputStream;

public class UncloseableInputStream extends FilterInputStream {
    protected UncloseableInputStream(InputStream in) {
        super(in);
    }

    @Override
    public void close() {
    }
}
