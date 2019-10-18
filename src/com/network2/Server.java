package com.network2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		ServerSocket server = new ServerSocket(8080);
		System.out.println("服务器端启动成功！");
		// Socket接收ServerSocket,接收客户端的socket
		Socket socket = server.accept();
//		获取客户端socket的输入流
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		while (true) {
//			等待客户端socket的不为空的输入流
			String str = in.readLine();
			if (str == null) {
				break;
			}
			System.out.println("客户端发来消息：" + str);

		}
		in.close();
		socket.close();
		server.close();

	}

}
