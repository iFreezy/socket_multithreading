package ru.httpSocket;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

public class Controller extends Thread {

    ServerSocket serverSocket;

    Controller(ServerSocket ssocket) {
        // запускаем новый поток
        this.serverSocket = ssocket;
        setDaemon(true);
        setPriority(NORM_PRIORITY);
        setName("Controller");
        start();
    }

    public void run() {
        while (true) {
            Scanner in = new Scanner(System.in);
            System.out.println("echo: " + in.next());
            if (in.next() == "stop") {
                try {
                    Thread.currentThread().interrupt();
                    serverSocket.close();
                    System.out.println("Thread name: " + Thread.currentThread().getName());
                    System.exit(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

}
