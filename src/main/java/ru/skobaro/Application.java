package ru.skobaro;

import java.nio.file.Paths;

public class Application {
    public static void main(String[] args) {
        new Server(Integer.parseInt(args[0]), Paths.get(args[1])).start();
    }
}