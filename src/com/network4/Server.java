package com.network4;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Server extends Frame implements ActionListener {
	JPanel jp1 = null;
	JTextArea jta = null;
	JTextField jtf = null;
	JButton jb = null;
	JScrollPane jsp = null;
	PrintWriter pw = null;

	public static void main(String[] args) {
		new Server();
	}

	public Server() {
		jta = new JTextArea();
		jsp = new JScrollPane(jta);
		jtf = new JTextField(10);
		// 注册回车事件
		jtf.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					sendMessage();
				}
			}
		});
		jb = new JButton("发送");
		jb.addActionListener(this);

		jp1 = new JPanel();
		jp1.add(jtf);
		jp1.add(jb);
		this.add(jsp, BorderLayout.CENTER);
		this.add(jp1, BorderLayout.SOUTH);
		this.setTitle("Chat Server");
		this.setSize(300, 200);
		this.setVisible(true);

		// 接收客户端传过来的消息
		try {
			ServerSocket ss = new ServerSocket(9999);
			Socket s = ss.accept();
			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			pw = new PrintWriter(s.getOutputStream(), true);
			while (true) {
				String info = in.readLine();
				String str;
				if (jta.getText() == null || "".equals(jta.getText())) {
					str = "肥龙:" + info;
				} else {
					str = jta.getText() + "\r\n肥龙:" + info;
				}
				jta.setText(str);
				jta.setCaretPosition(jta.getDocument().getLength());// 把滚动条拖到最底端

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == jb) {
			sendMessage();
		}
	}

	// 发送消息方法
	public void sendMessage() {
		String info = jtf.getText();
		pw.println(info);
		pw.flush();
		jtf.setText("");
		if (jta.getText() == null || "".equals(jta.getText())) {
			jta.append("龙龙88:" + info);
		} else {
			jta.append("\r\n龙龙88:" + info);
		}
		jta.setCaretPosition(jta.getDocument().getLength());
	}

}
