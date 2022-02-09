package com.lisz;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Hello world!
 *
 */
public class RawHttpServer {
    public static void main( String[] args ) throws Exception{
        ServerSocket serverSocket = new ServerSocket(8080);
        while (true) {
            final Socket socket = serverSocket.accept(); // 阻塞方法, sleep，当前线程停止执行，控制权交给其他线程
            System.out.println("A socket is created");
            final DataInputStream in = new DataInputStream(socket.getInputStream());
            final BufferedReader br = new BufferedReader(new InputStreamReader(in));
            final StringBuilder requestBuilder = new StringBuilder();
            String line = null;
            while (!(line = br.readLine()).isEmpty()) {
                requestBuilder.append(line + '\n');
            }
            String request = requestBuilder.toString();
            System.out.println(request);

            final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bw.write("HTTP/1.1 200 ok \n\nHello World!\n"); // curl打印返回值的时候，只打印Hello World
            bw.flush();
            socket.close();
        }
    }
}
