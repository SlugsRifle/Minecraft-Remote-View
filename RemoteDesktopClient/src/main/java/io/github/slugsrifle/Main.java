package io.github.slugsrifle;

import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.Socket;

public class Main {

	public static Dimension res;
	public static boolean getInfo;
	public static String ip;
	public static int port;
	public static Socket socket;
	public static BufferedInputStream bis;
	public static BufferedOutputStream bos;
	public static CaptureThread captureThread;
	public static SocketReadThread socketReaderThead;

	public static void main(String[] args) {
		if (args.length == 2) {
			ip = args[0];
			port = Integer.parseInt(args[1]);
			socketReaderThead = new SocketReadThread();
			socketReaderThead.start();
		} else {
			System.out.println("<command> ip port");
		}
	}

}
