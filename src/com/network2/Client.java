package com.network2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
	public static void main(String[] args) throws IOException {
		Socket socket = new Socket("127.0.0.1", 8080);
		System.out.println("�ͻ��������ɹ���");
//		�ͻ��˵�������
		BufferedReader out = new BufferedReader(new InputStreamReader(System.in));
//		�ͻ��˵������
		PrintWriter pw = new PrintWriter(socket.getOutputStream());
		while (true) {
			String str = out.readLine();
			if ("".equals(str)) {
				break;
			}
//			ͨ��socket�������Ϣ���͵�����������
			pw.println(str);
			pw.flush();

		} // ����ѭ����ֱ������Ϊ��
		out.close();
		socket.close();
//		---------------------------------------------
	}
}
