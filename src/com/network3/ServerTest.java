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
	ServerSocket serverSocket = null;// 创建一个ServerSocket对象

	Collection clients = new ArrayList();// 创建一个集合来装客户端

	public ServerTest(int port) throws IOException {
		serverSocket = new ServerSocket(port);
	}

//	可供多个客户端连接
	public void startServer() throws IOException {
		System.out.println("服务器已启动...");
		while (true) {
			// 侦听是否有客户端连接，此前一直是阻塞状态
			Socket socket = serverSocket.accept();
			clients.add(new ClientProcess(socket));
		}
	}

	class ClientProcess implements Runnable {

		Socket socket = null;

		public ClientProcess(Socket socket) {
			this.socket = socket;
			(new Thread(this)).start();
			// 构造函数创建socket并启动线程
		}

//		发送消息给客户端
		public void sendMessage(String str) throws IOException {
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			dos.writeUTF(str);

		}

//		获取客户端发来的消息
		public String getMessage() throws IOException {
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			String str = dis.readUTF();
			return str;
		}

//		释放socket并移除断开的客户端
		public void destroy() {
			try {
				if (socket != null) {
					socket.close();
					clients.remove(this);
					System.out.println("A Client out!");
					System.out.println("客户端数量：" + clients.size() + "\n");
				} else {
					System.out.println("客户端不存在！");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

//		继续监听是否有消息发来
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				DataInputStream dis = new DataInputStream(socket.getInputStream());
				String str = null;

				while (true) {
					// 没有收到消息会造成堵塞
					str = dis.readUTF();
					System.out.println(socket.getInetAddress() + "--" + socket.getPort() + "：" + str);
					// 消息共享到多个客户端上
					for (Iterator it = clients.iterator(); it.hasNext();) {
						ClientProcess cp = (ClientProcess) it.next();
						if (this != cp) {
							cp.sendMessage(socket.getInetAddress() + "--" + socket.getPort() + "：" + str);

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
