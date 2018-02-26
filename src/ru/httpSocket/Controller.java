package ru.httpSocket;

import java.io.IOException;
import java.net.ServerSocket;

public class Controller extends Thread {

    public final static int SOCKET_PORT = 13267;

//    public Controller() throws IOException{
//        try (ServerSocket servsock = new ServerSocket(SOCKET_PORT)) {
//
//            while (true) {
//                System.out.println("Waiting...");
//                new Server(servsock.accept());
//            }
//        }
//    }

    public void run() {
        try (ServerSocket servsock = new ServerSocket(SOCKET_PORT)) {

            while (true) {
                System.out.println("Waiting...");
                new Server(servsock.accept());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
