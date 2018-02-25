package ru.httpSocket;

import java.io.*;
import java.net.ServerSocket;

public class SimpleFileServer {

    public final static int SOCKET_PORT = 13267;

    public static void main(String[] args) throws IOException {
        try (ServerSocket servsock = new ServerSocket(SOCKET_PORT)) {
            new Controller(servsock);
            while (true) {
                System.out.println("Waiting...");
                new Server(servsock.accept());
            }
        }
    }

}
