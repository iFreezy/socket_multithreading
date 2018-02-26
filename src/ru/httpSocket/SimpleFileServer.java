package ru.httpSocket;

import java.io.*;
import java.util.Scanner;

public class SimpleFileServer {

    public static void main(String[] args) {
        new Controller().start();
        System.out.println("Started");
        while (true) {
            Scanner in = new Scanner(System.in);
            String text = in.next();
            System.out.println("echo: " + text);
            if (text.equals("stop")) {
                Thread.currentThread().interrupt();
                System.exit(0);
            }
        }
    }

}
