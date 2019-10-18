package com.network4;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client extends JFrame implements ActionListener {
	JPanel jp1 = null;
	JTextArea jta1 = null;
	JTextField jtf = null;
	JButton jb = null;
	JScrollPane jsp1 = null;
	PrintWriter pw = null;

	public static void main(String[] args) {
		new Client();
	}

	public Client() {
		jta1 = new JTextArea();// �ı���
		jsp1 = new JScrollPane(jta1);// �������
		jtf = new JTextField(10);// �ı���

		// ע����̻س��¼�
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

		jb = new JButton("����");
		jb.addActionListener(this);
		jp1 = new JPanel();
		jp1.add(jtf);
		jp1.add(jb);
		this.add(jsp1, BorderLayout.CENTER);
		this.add(jp1, BorderLayout.SOUTH);
		this.setTitle("Chat Client");
		this.setSize(300, 200);
		this.setVisible(true);
		// ���շ������˵���Ϣ
		try {
			Socket s = new Socket("192.168.100.34", 9999);
			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			pw = new PrintWriter(s.getOutputStream(), true);
			while (true) {
				String info = in.readLine();
				String str = null;
				if (jta1.getText() == null || "".equals(jta1.getText())) {
					str = "����88:" + info;

				} else {
					str = jta1.getText() + "\r\n����88:" + info;
				}
				jta1.setText(str);
				jta1.setCaretPosition(jta1.getDocument().getLength());
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

//	������Ϣ����������
	private void sendMessage() {
		// TODO Auto-generated method stub
		String info = jtf.getText();
		pw.println(info);
		pw.flush();
		jtf.setText("");
		if (jta1.getText() == null || "".equals(jta1.getText())) {
			jta1.append("����:" + info);
		} else {
			jta1.append("\r\n����:" + info);
		}
		jta1.setCaretPosition(jta1.getDocument().getLength());// ���ù������Զ�����
	}
}
