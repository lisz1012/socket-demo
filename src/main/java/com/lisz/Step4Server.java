package com.lisz;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Set;

public class Step4Server {
	private ServerSocketChannel ssc;

	public static void main(String[] args) throws Exception{
		final Step4Server server = new Step4Server();
		server.listen(8080);
	}

	private void listen(int port) throws Exception {
		ssc = ServerSocketChannel.open();
		ssc.bind(new InetSocketAddress(port));
		// false = Reactive
		ssc.configureBlocking(false);
		final Selector selector = Selector.open();
		ssc.register(selector, ssc.validOps(), null);
		ByteBuffer buf = ByteBuffer.allocateDirect(16*1024);
		while (selector.select(200) > 0) {
			final Set<SelectionKey> keys = selector.selectedKeys();
			for (SelectionKey key : keys) {
				if (key.isAcceptable()) {
					final SocketChannel channel = ssc.accept(); // ssc.configureBlocking(false); 以防万一channel可能是null
					if (channel == null) continue;
					// kernel -> mmap -> channel -> user(buffer)
					channel.configureBlocking(false);
					channel.register(selector, SelectionKey.OP_READ);
				} else if (key.isReadable()) {
					final SocketChannel channel = (SocketChannel) key.channel();
					/*_ _ _ _ _ _ _ _
								|
						        P(position) position 清零，O(1)的操作 */
					buf.clear();
					channel.read(buf);
					String request = new String(buf.array());
					System.out.println(request);
					// 按说是要有一些逻辑的，比如万一16k不够怎么办
					buf.clear(); // 准备往这个16k的buffer里面写
					buf.put("HTTP/1.1 200 ok\n\nHello NIO!!".getBytes());
					// 数据写出的时候跟上面的方向正好相反：kernel <- mmap <- channel <- user(buffer) 从position开始读
					// The limit is set to the current position and then the position is set to zero.
					buf.flip();
					channel.write(buf);
					channel.close();
				} else if (key.isWritable()) {

				}
			}
		}
	}
}
