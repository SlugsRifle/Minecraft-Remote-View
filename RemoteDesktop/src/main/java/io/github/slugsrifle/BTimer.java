package io.github.slugsrifle;

public class BTimer {
	long time = 0;

	public void init() {
		time = System.currentTimeMillis();
	}
	public long time() {
		long c = System.currentTimeMillis();
		long r = c - time;
		time = c;
		return r;
	}
	public void info(String s) {
		long c = System.currentTimeMillis();
		long r = c - time;
		time = c;
		System.out.println(s + " :: " + r + "ms");
	}
}
