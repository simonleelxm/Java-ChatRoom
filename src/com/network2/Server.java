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
		System.out.println("�������������ɹ���");
		// Socket����ServerSocket,���տͻ��˵�socket
		Socket socket = server.accept();
//		��ȡ�ͻ���socket��������
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		while (true) {
//			�ȴ��ͻ���socket�Ĳ�Ϊ�յ�������
			String str = in.readLine();
			if (str == null) {
				break;
			}
			System.out.println("�ͻ��˷�����Ϣ��" + str);

		}
		in.close();
		socket.close();
		server.close();

	}

}
