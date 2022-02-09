package com.lisz;

import lombok.Data;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpParser;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class Request {
	private static final Pattern PATTERN = Pattern.compile("(GET|PUT|POST|DELETE|OPTION|TRACE|HEAD)");
	private String body;
	private String method;
	private Map<String, String> headers;

	public Request(Socket socket) throws Exception {
		// DataInutStream -> primititives(char, float)
		final DataInputStream in = new DataInputStream(socket.getInputStream());
		final BufferedReader br = new BufferedReader(new InputStreamReader(in));

		// GET /path HTTP/1.1
		final String line = HttpParser.readLine(in,"UTF-8");
		final Matcher matcher = PATTERN.matcher(line);
		matcher.find();
		final String method = matcher.group();

		final Header[] headers = HttpParser.parseHeaders(in, "UTF-8");
		final Map<String, String> headerMap = new HashMap<>();
		for (Header header : headers) {
			headerMap.put(header.getName(), header.getValue());
		}
		StringBuilder sb = new StringBuilder();
		final char[] buf = new char[1024];
		while (in.available() > 0) {
			int available = Math.min(in.available(), buf.length);
			br.read(buf, 0, available);
			sb.append(buf);
		}

		this.body = sb.toString().trim(); // 剪掉不必要的'\0'，否则客户端的curl命令会报错
		this.method = method;
		this.headers = headerMap;
	}
}
