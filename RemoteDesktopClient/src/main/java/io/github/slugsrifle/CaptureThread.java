package io.github.slugsrifle;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static io.github.slugsrifle.Main.*;

public class CaptureThread extends Thread {

	static private Renderer renderer;
	private Robot r;
	private Rectangle rec;
	private boolean run = true;
	public BTimer b = new BTimer();

	public CaptureThread() {
		try {
			r = new Robot();
			rec = new Rectangle(res.width, res.height);
			if (renderer == null) {
				renderer = new Renderer(res.width, res.height);
			}
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		run = true;
		super.start();
	}

	public void exit() {
		run = false;
	}

	@Override
	public void run() {
		try {
			b.init();
			while (run) {
				if (getInfo) {
					// System.out.println("op");
					b.info("Render");
					BufferedImage b = r.createScreenCapture(rec);
					byte[] buf = renderer.render(b);
					bos.write(buf, 0, buf.length);
					bos.flush();
					Thread.sleep(50);
				}
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
