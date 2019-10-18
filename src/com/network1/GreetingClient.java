package com.network1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class GreetingClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String serverName = "ALJS-20180826YQ";
		int port=8080;
		try {
			System.out.println("连接主机："+serverName+", 端口号："+port);
			Socket client =new Socket(serverName,port);
			System.out.println("远程服务器地址："+client.getRemoteSocketAddress());
			OutputStream outToServer = client.getOutputStream();
			DataOutputStream out = new DataOutputStream(outToServer);
			out.writeUTF("Hello From"+client.getLocalSocketAddress());
			
			InputStream inFromServer = client.getInputStream();
			DataInputStream in = new DataInputStream(inFromServer);
			System.out.println("服务器相应："+in.readUTF());
			client.close();
			
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		
//		------------------------------------
	}

}
