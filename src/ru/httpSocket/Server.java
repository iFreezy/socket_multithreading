package ru.httpSocket;

import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Server extends Thread{

    Socket socket;

    Server(Socket s) {
        this.socket = s;

        // запускаем новый поток
        setDaemon(true);
        setPriority(NORM_PRIORITY);
        setName("Server");
        start();
    }

    public void run() {
        try {
            // и оттуда же - поток данных от сервера к клиенту
            OutputStream os = socket.getOutputStream();
            // из сокета клиента берём поток входящих данных
            InputStream is = socket.getInputStream();
            System.out.println("Accepted connection : " + socket);

            // буффер данных в 64 килобайта
            byte buf[] = new byte[64 * 1024];
            // читаем 64кб от клиента, результат - кол-во реально принятых данных
            int r = is.read(buf);

            // создаём строку, содержащую полученую от клиента информацию
            String request = new String(buf, 0, r);
            System.out.println(request);

            // получаем путь до документа (см. ниже ф-ю "getPath")
            String path = getPath(request);

            // если из запроса не удалось выделить путь, то
            // возвращаем "400 Bad Request"
            if (path == null) {
                // первая строка ответа
                String response = "HTTP/1.1 400 Bad Request\n";

                // дата в GMT
                DateFormat df = DateFormat.getTimeInstance();
                df.setTimeZone(TimeZone.getTimeZone("GMT"));
                response = response + "Date: " + df.format(new Date()) + "\n";

                // остальные заголовки
                response = response
                        + "Connection: close\n"
                        + "Server: SimpleWEBServer\n"
                        + "Pragma: no-cache\n\n";

                // выводим данные:
                os.write(response.getBytes());

                // завершаем соединение
                socket.close();

                // выход
                return;
            }


            // send file
            File sourceDir = new File(path);

            // если по указанному пути файл не найден
            // то выводим ошибку "404 Not Found"
            if (!sourceDir.exists()) {
                // первая строка ответа
                String response = "HTTP/1.1 404 Not Found\n";

                // дата в GMT
                DateFormat df = DateFormat.getTimeInstance();
                df.setTimeZone(TimeZone.getTimeZone("GMT"));
                response = response + "Date: " + df.format(new Date()) + "\n";

                // остальные заголовки
                response = response
                        + "Content-Type: text/plain\n"
                        + "Connection: close\n"
                        + "Server: SimpleWEBServer\n"
                        + "Pragma: no-cache\n\n";

                // и гневное сообщение
                response = response + "File " + path + " not found!";

                // выводим данные:
                os.write(response.getBytes());

                // завершаем соединение
                socket.close();

                // выход
                return;
            }

            byte[] mybytearray = new byte[(int) sourceDir.length()];
            System.out.println("Sending " + path + "(" + mybytearray.length + " bytes)");

            // создаём ответ

            // первая строка ответа
            String response = "HTTP/1.1 200 OK\n";

            // дата создания в GMT
            DateFormat df = DateFormat.getTimeInstance();
            df.setTimeZone(TimeZone.getTimeZone("GMT"));

            // время последней модификации файла в GMT
            response = response + "Last-Modified: " + df.format(new Date(sourceDir.lastModified())) + "\n";

            // длина файла
            response = response + "Content-Length: " + sourceDir.length() + "\n";

            // строка с MIME кодировкой
            response = response + "Content-Type: " + "text/html" + "\n";

            // остальные заголовки
            response = response
                    + "Connection: close\n"
                    + "Server: SimpleWEBServer\n\n";

            // выводим заголовок:
            os.write(response.getBytes());

            // и сам файл:

            try (FileInputStream in = new FileInputStream(sourceDir)) {
                // Читаем файл блоками по килобайту
                byte[] data = new byte[1024];
                while ((in.read(data)) != -1) {
                    // И отправляем в сокет
                    os.write(data);
                }
            } catch (IOException exc) {
                exc.printStackTrace();
            }

            // завершаем соединение
            socket.close();
            System.out.println("Done.");

        } catch (Exception e) {
            System.out.println("init error: " + e);
        } // вывод исключений
    }

    // "вырезает" из HTTP заголовка URI ресурса и конвертирует его в filepath
    // URI берётся только для GET и POST запросов, иначе возвращается null
    private String getPath(String header) {
        // ищем URI, указанный в HTTP запросе
        // URI ищется только для методов POST и GET, иначе возвращается null
        String URI = extract(header, "GET ", " "), path;
        if (URI == null) URI = extract(header, "POST ", " ");
        if (URI == null) return null;

        return "src/files" + URI;
    }

    // "вырезает" из строки str часть, находящуюся между строками start и end
    // если строки end нет, то берётся строка после start
    // если кусок не найден, возвращается null
    // для поиска берётся строка до "\n\n" или "\r\n\r\n", если таковые присутствуют
    private String extract(String str, String start, String end) {
        int s = str.indexOf("\n\n", 0), e;
        if (s < 0) s = str.indexOf("\r\n\r\n", 0);
        if (s > 0) str = str.substring(0, s);
        s = str.indexOf(start, 0) + start.length();
        if (s < start.length()) return null;
        e = str.indexOf(end, s);
        if (e < 0) e = str.length();
        return (str.substring(s, e)).trim();
    }

}