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
				System.out.println("�ȴ�Զ�̿ͻ������ӣ��˿ں�Ϊ��"+serverSocket.getLocalPort()+"...");
				Socket server = serverSocket.accept();
				System.out.println("Զ��������ַΪ��"+server.getRemoteSocketAddress());
				DataInputStream in = new DataInputStream(server.getInputStream());
				System.out.println(in.readUTF());
				DataOutputStream out = new DataOutputStream(server.getOutputStream());
				out.writeUTF("�Ҹ��������������ˣ���ַ��"+server.getLocalSocketAddress()+"\nEnd!");
				server.close();
				
				
			}
			catch(SocketTimeoutException se) {
				System.out.println("Socketʱ����Ӧ��ʱ");
				
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

