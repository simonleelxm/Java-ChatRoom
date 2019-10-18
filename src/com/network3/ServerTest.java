package com.network3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class ServerTest {
	ServerSocket serverSocket = null;// ����һ��ServerSocket����

	Collection clients = new ArrayList();// ����һ��������װ�ͻ���

	public ServerTest(int port) throws IOException {
		serverSocket = new ServerSocket(port);
	}

//	�ɹ�����ͻ�������
	public void startServer() throws IOException {
		System.out.println("������������...");
		while (true) {
			// �����Ƿ��пͻ������ӣ���ǰһֱ������״̬
			Socket socket = serverSocket.accept();
			clients.add(new ClientProcess(socket));
		}
	}

	class ClientProcess implements Runnable {

		Socket socket = null;

		public ClientProcess(Socket socket) {
			this.socket = socket;
			(new Thread(this)).start();
			// ���캯������socket�������߳�
		}

//		������Ϣ���ͻ���
		public void sendMessage(String str) throws IOException {
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			dos.writeUTF(str);

		}

//		��ȡ�ͻ��˷�������Ϣ
		public String getMessage() throws IOException {
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			String str = dis.readUTF();
			return str;
		}

//		�ͷ�socket���Ƴ��Ͽ��Ŀͻ���
		public void destroy() {
			try {
				if (socket != null) {
					socket.close();
					clients.remove(this);
					System.out.println("A Client out!");
					System.out.println("�ͻ���������" + clients.size() + "\n");
				} else {
					System.out.println("�ͻ��˲����ڣ�");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

//		���������Ƿ�����Ϣ����
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				DataInputStream dis = new DataInputStream(socket.getInputStream());
				String str = null;

				while (true) {
					// û���յ���Ϣ����ɶ���
					str = dis.readUTF();
					System.out.println(socket.getInetAddress() + "--" + socket.getPort() + "��" + str);
					// ��Ϣ��������ͻ�����
					for (Iterator it = clients.iterator(); it.hasNext();) {
						ClientProcess cp = (ClientProcess) it.next();
						if (this != cp) {
							cp.sendMessage(socket.getInetAddress() + "--" + socket.getPort() + "��" + str);

						}
					}
				}
			} catch (Exception e) {
				System.out.println("client quit!");
				this.destroy();
			}
		}

	}

	public static void main(String[] args) throws IOException {
		ServerTest st = new ServerTest(8888);
		st.startServer();
	}
}
