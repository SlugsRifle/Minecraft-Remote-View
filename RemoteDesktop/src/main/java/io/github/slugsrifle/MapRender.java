package io.github.slugsrifle;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MapRender {
	ExecutorService es = Executors.newFixedThreadPool(2);

	BTimer b = new BTimer();

	private static final int size = 128;

	static byte[] buf = new byte[Main.getSize()];

	public void exit() {
		es.shutdownNow();
		while (!es.isShutdown());
	}

	public void render() {
		//b.init();
		ArrayList<Future<?>> l = new ArrayList<>();
		for (int i = 0; i < Main.getSize(); ++i) {
			l.add(es.submit(new RenderWorker(buf, i)));
		}
		l.stream().forEach(x -> {
			while (!x.isDone())
				;
		});
		//b.info("render");
	}

	class RenderWorker implements Runnable {
		byte[] buf;
		int c;
		int sx, sy;

		public RenderWorker(byte[] buf, int x) {
			this.buf = buf;
			c = x;			
			sx = x % (Main.res.width / size);
			sy = x / (Main.res.width / size);
		}
		
		public byte[] toSubBytes(int sx, int sy) {
			byte[] result = new byte[128 * 128];
			for(int y = 0; y < 128 && sy + y < Main.res.height ; ++y) {
				for(int x = 0; x < 128 && sx + x < Main.res.width; ++x) {
					result[x + 128 * y] = buf[sx + x + Main.res.width * (sy + y)];
				}
			}
			return result;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			MapDataSender.datas[c] = toSubBytes(size * sx, size * sy);
		}

	}
}
