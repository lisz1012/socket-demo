package com.lisz;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.function.Function;

/**
 * Hello world!
 *
 */
public class Step2Server {
    private ServerSocket serverSocket;
    private Function<String, String> handler;

    public Step2Server(Function<String, String> handler) {
        this.handler = handler;
    }

    // Pending queue
    public void listen(int port) throws Exception{
    	serverSocket = new ServerSocket(port);
        while (true) {
            final Socket socket = serverSocket.accept(); // 阻塞方法, sleep，当前线程停止执行，控制权交给其他线程
            handle(socket);
        }
    }

    private void handle(Socket socket) {
        new Thread(() -> {
            try {
                doHandle(socket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void doHandle(Socket socket) throws Exception {
        try {
            String request = getRequest(socket);

            final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            final String response = handler.apply(request);
            bw.write(response);
            bw.flush();

            socket.close();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private String getRequest(Socket socket) throws Exception {
        System.out.println("A socket is created");
        final DataInputStream in = new DataInputStream(socket.getInputStream());
        final BufferedReader br = new BufferedReader(new InputStreamReader(in));
        final StringBuilder requestBuilder = new StringBuilder();
        String line = null;
        while (true) {
            line = br.readLine();
            if (line == null || line.isEmpty()) break;
            requestBuilder.append(line + '\n');
        }
        String request = requestBuilder.toString();
        System.out.println(request);
        return request;
    }

    public static void main( String[] args ) throws Exception{
        final Step2Server step1Server = new Step2Server(request -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "HTTP/1.1 201 ok\n\nGood!\n";
        });
        step1Server.listen(8080);
    }
}
