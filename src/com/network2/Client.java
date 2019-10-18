package com.network2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
	public static void main(String[] args) throws IOException {
		Socket socket = new Socket("127.0.0.1", 8080);
		System.out.println("客户端启动成功！");
//		客户端的输入流
		BufferedReader out = new BufferedReader(new InputStreamReader(System.in));
//		客户端的输出流
		PrintWriter pw = new PrintWriter(socket.getOutputStream());
		while (true) {
			String str = out.readLine();
			if ("".equals(str)) {
				break;
			}
//			通过socket对象把信息发送到服务器上面
			pw.println(str);
			pw.flush();

		} // 不断循环，直到输入为空
		out.close();
		socket.close();
//		---------------------------------------------
	}
}
