package com.lisz;

import java.net.ServerSocket;
import java.net.Socket;

public class Step3Server {
	private ServerSocket serverSocket;

	private IHandlerInterface httpHandler;

	public Step3Server(IHandlerInterface httpHandler) {
		this.httpHandler = httpHandler;
	}

	public void listen(int port) throws Exception {
		serverSocket = new ServerSocket(port);
		while (true) {
			final Socket socket = serverSocket.accept();
			handle(socket);
		}
	}

	private void handle(Socket socket) {
		new Thread(()->{
			try {
				doHandle(socket);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	private void doHandle(Socket socket) throws Exception {
		Request request = new Request(socket);
		Response response = new Response(socket);
		httpHandler.handle(request, response); // 具体怎么handle，看外面传入的lambda表达式
	}

	// 发送请求： curl -d'login=emma＆password=123'-X POST http://localhost:8080/
	public static void main(String[] args) throws Exception {
		final Step3Server step3Server = new Step3Server((req, resp) -> {
			System.out.println(req.getHeaders());
			try {
				resp.send("<html><body><h1>Greetings!</h1>\n" + req.getMethod() + "\n" + req.getBody() + "\n</body></html>");
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		step3Server.listen(8080);
	}
}
