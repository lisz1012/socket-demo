package com.lisz;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Response {
	private final Socket socket;
	private int status;
	private static Map<Integer, String> codeMap;

	public Response(Socket socket) {
		this.socket = socket;
		codeMap = new HashMap<>();
		codeMap.put(200, "OK");
	}

	public void send(String msg) throws Exception {
		// 这里遵守了Http协议的返回格式，是可以成为Http Server的关键！！
		String resp = "HTTP/1.1 " + status + " " + codeMap.get(status) + "\n\n" + msg;
		sendRaw(resp);
	}

	public void sendRaw(String msg) throws Exception{
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		bw.write(msg);
		bw.flush();
		socket.close();
	}
}
