package io.github.slugsrifle;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

import static io.github.slugsrifle.Main.*;

public class ServerSocketThread extends Thread {
	Socket client;
	BufferedInputStream bis;
	BufferedOutputStream bos;
	boolean run = true;
	byte[] buf = new byte[res.width * res.height];
	int recv = 0;
	int recv_ALL = 0;

	public void exit() {
		run = false;
		try {
			serverSocket.close();
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (run) {
			try {
				client = serverSocket.accept();
				logger.info("Client Connected");
				bis = new BufferedInputStream(client.getInputStream());
				bos = new BufferedOutputStream(client.getOutputStream());
				for (int i = 0; i < 4; ++i) {
					bos.write((res.width >> (8 * (3 - i)) & 0xff));
				}
				for (int i = 0; i < 4; ++i) {
					bos.write((res.height >> (8 * (3 - i)) & 0xff));
				}
				bos.flush();
				bis.read();
				logger.info("Screen Data Sended");
				while (true) {
					recv = bis.read(buf, recv_ALL, buf.length - recv_ALL);
					recv_ALL += recv;
					if (recv_ALL == buf.length) {
						//System.out.println(recv_ALL + " " + buf[0]);
						MapRender.buf = buf;
						mr.render();
						mds.sendPacket();
						recv_ALL = 0;
						//b.info("Render :: ");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}
