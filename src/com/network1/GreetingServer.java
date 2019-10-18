package com.network1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class GreetingServer extends Thread{
	private ServerSocket serverSocket;
	
	public GreetingServer(int port) throws IOException{
		serverSocket=new ServerSocket(port);
		serverSocket.setSoTimeout(10000);
	}
	
	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			try {
				System.out.println("等待远程客户端连接，端口号为："+serverSocket.getLocalPort()+"...");
				Socket server = serverSocket.accept();
				System.out.println("远程主机地址为："+server.getRemoteSocketAddress());
				DataInputStream in = new DataInputStream(server.getInputStream());
				System.out.println(in.readUTF());
				DataOutputStream out = new DataOutputStream(server.getOutputStream());
				out.writeUTF("我跟服务器端连接了，地址是"+server.getLocalSocketAddress()+"\nEnd!");
				server.close();
				
				
			}
			catch(SocketTimeoutException se) {
				System.out.println("Socket时间响应超时");
				
				break;
			}
			catch(IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}



	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int port = 8080;
		try {
			Thread thread = new GreetingServer(port);
			thread.start();
			
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

}

