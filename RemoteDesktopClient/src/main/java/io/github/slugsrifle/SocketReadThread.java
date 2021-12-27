package io.github.slugsrifle;

import static io.github.slugsrifle.Main.*;

import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketReadThread extends Thread {

	@Override
	public void run() {
		while (true) {
			int w = 0;
			int h = 0;
			try {
				socket = new Socket(ip, port);
				bis = new BufferedInputStream(socket.getInputStream());
				bos = new BufferedOutputStream(socket.getOutputStream());
				for (int i = 0; i < 4; ++i) {
					w <<= 8;
					w |= bis.read() & 0xff;
				}

				for (int i = 0; i < 4; ++i) {
					h <<= 8;
					h |= bis.read() & 0xff;
				}

				res = new Dimension(w, h);
				getInfo = true;

				bos.write(0);
				bos.flush();

				System.out.println(String.format("Width: %d, Height: %d\n", w, h));

				captureThread = new CaptureThread();
				captureThread.start();
				while (bis.read() != -1) {
					bis.read();
				}
			} catch (IOException e) {
				captureThread.exit();
				e.printStackTrace();
			}
		}
	}
}
