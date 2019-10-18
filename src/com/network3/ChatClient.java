package com.network3;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient extends Frame {
	TextArea ta = new TextArea();
	TextField tf = new TextField();
	Socket s = null;

//	----------------------------------------------------
	public void launchFrame() {
		this.add(ta, BorderLayout.CENTER);
		this.add(tf, BorderLayout.SOUTH);
		tf.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					String sSend = tf.getText();
					if (sSend.trim().length() == 0) {
						System.out.println("输入为空！");
						return;
					}
					ChatClient.this.sendMessage(sSend);
					tf.setText("");
					ta.append("Me：" + sSend + "\n");
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		});

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		setBounds(300, 300, 300, 400);
		setVisible(true);
		ta.requestFocus();
	}
//	---------------------------------

//	构造方法
	public ChatClient() throws UnknownHostException, IOException {
		// TODO Auto-generated constructor stub
		s = new Socket("192.168.100.166", 9999);
		launchFrame();

	}

	// 把消息发送给服务端
	public void sendMessage(String str) throws IOException {
		DataOutputStream dos = new DataOutputStream(s.getOutputStream());
		dos.writeUTF(str);
	}

//	断开连接
	public void disConnect() throws IOException {
		if (s != null) {
			s.close();
		}
	}

	class ReceiveThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (s == null) {
				return;
			}
			try {
				DataInputStream dis = new DataInputStream(s.getInputStream());
				String str = dis.readUTF();
				while (str != null && str.length() != 0) {
					ChatClient.this.ta.append(str + "\n");
					str = dis.readUTF();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args) throws UnknownHostException, IOException {
		ChatClient cc = new ChatClient();
	}

}
