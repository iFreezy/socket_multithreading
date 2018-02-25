package ru.httpSocket;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static int PORT = 2121;
    private static String FOLDER = "src/files";

    public static void main(String[] args) {
        File sourceDir = new File(FOLDER);

        System.out.println(sourceDir.getAbsolutePath());
        try (ServerSocket listener = new ServerSocket(PORT)) {
            while (true) {
                try (Socket socket = listener.accept();
                     OutputStream out = socket.getOutputStream()) {
                    for (String fileName : sourceDir.list()) {
                        // Преобразовываем строку, содержащую имя файла,
                        // в массив байт
                        byte[] name = fileName.getBytes("utf-8");
                        // Отправляем длину этого массива
                        out.write(name.length);
                        // Отправляем байты имени
                        out.write(name);

                        File file = new File(FOLDER + "/" + fileName);

                        // Получаем размер файла
                        long fileSize = file.length();
                        // Конвертируем его в массив байт
                        ByteBuffer buf = ByteBuffer.allocate(Long.BYTES);
                        buf.putLong(fileSize);
                        // И отправляем
                        out.write(buf.array());

                        try (FileInputStream in = new FileInputStream(file)) {
                            // Читаем файл блоками по килобайту
                            byte[] data = new byte[1024];
                            int read;
                            while ((read = in.read(data)) != -1) {
                                // И отправляем в сокет
                                out.write(data);
                            }
                        } catch (IOException exc) {
                            exc.printStackTrace();
                        }
                    }
                } catch (IOException exc) {
                    exc.printStackTrace();
                }
            }
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }
}