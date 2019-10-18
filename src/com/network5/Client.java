package com.network5;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class Client {
	private JFrame frame;
	private JList userList;
	private JTextField txt_port;
	private JTextField txt_hostIp;
	private JTextField txt_name;
	private JTextField textField;
	private JButton btn_connect;
	private JButton btn_disconnect;
	private JButton btn_send;
	private JTextArea textArea;
	private JPanel northPanel;
	private JPanel southPanel;
	private JScrollPane rightScroll;
	private JScrollPane leftScroll;
	private JSplitPane centerPanel;

	private DefaultListModel listModel;
	private boolean isConnected = false;

	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;

	private MessageThread messageThread;// ���������Ϣ���߳�
	private Map<String, User> onLineUsers = new HashMap<String, User>();

	public static void main(String[] args) {
		new Client();
	}

	public Client() {
		btn_connect = new JButton("����");
		btn_disconnect = new JButton("�Ͽ�");
		btn_send = new JButton("����");
		txt_port = new JTextField("6666");
		txt_hostIp = new JTextField("127.0.0.1");
		txt_name = new JTextField("����88");
		textField = new JTextField();

		northPanel = new JPanel();
		northPanel.setLayout(new GridLayout());
		northPanel.add(new JLabel("�˿�"));
		northPanel.add(txt_port);
		northPanel.add(new JLabel("������IP"));
		northPanel.add(txt_hostIp);
		northPanel.add(new JLabel("����"));
		northPanel.add(txt_name);
		northPanel.add(btn_connect);
		northPanel.add(btn_disconnect);
		northPanel.setBorder(new TitledBorder("������Ϣ"));

		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setForeground(Color.blue);
		rightScroll = new JScrollPane(textArea);
		rightScroll.setBorder(new TitledBorder("��Ϣ��ʾ��"));

		listModel = new DefaultListModel();
		userList = new JList(listModel);
		leftScroll = new JScrollPane(userList);
		leftScroll.setBorder(new TitledBorder("�����û�"));

		centerPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftScroll, rightScroll);
		centerPanel.setDividerLocation(100);

		southPanel = new JPanel(new BorderLayout());
		southPanel.setBorder(new TitledBorder("д��Ϣ"));
		southPanel.add(textField, "Center");
		southPanel.add(btn_send, "East");

		frame = new JFrame("�ͻ���");
		frame.setLayout(new BorderLayout());
		frame.add(northPanel, "North");
		frame.add(southPanel, "South");
		frame.add(centerPanel, "Center");
		frame.setSize(600, 500);
		int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
		frame.setLocation((screen_width - frame.getWidth()) / 2, (screen_height - frame.getHeight()) / 2);

		frame.setVisible(true);

		// ����ı�����̰�ť
		textField.addKeyListener(new KeyListener() {

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
					send();
				}
			}
		});

		// ������Ӱ�ť�¼�
		btn_connect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int port;
				if (isConnected) {
					JOptionPane.showMessageDialog(frame, "�Ѵ���������״̬����Ҫ�ظ�����!", "����", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					try {
						port = Integer.parseInt(txt_port.getText().trim());
					} catch (NumberFormatException e2) {
						throw new Exception("�˿ںŲ�����Ҫ��!�˿�Ϊ����!");
					}
					String hostIp = txt_hostIp.getText().trim();
					String name = txt_name.getText().trim();
					if (name.equals("") || hostIp.equals("")) {
						throw new Exception("������������IP����Ϊ��!");
					}
					boolean flag = connectServer(port, hostIp, name);
					if (flag == false) {
						throw new Exception("�����������ʧ��!");
					}
					frame.setTitle(name);

				} catch (Exception exc) {
					JOptionPane.showMessageDialog(frame, exc.getMessage(), "����", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		// ��ӶϿ���ť�¼�
		btn_disconnect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (!isConnected) {
					JOptionPane.showMessageDialog(frame, "�Ѵ��ڶϿ�״̬����Ҫ�ظ��Ͽ�!", "����", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					boolean flag = closeConnection();// �Ͽ�����
					if (flag == false) {
						throw new Exception("�Ͽ����ӷ����쳣��");
					}
					JOptionPane.showMessageDialog(frame, "�ɹ��Ͽ�!");
					listModel.removeAllElements();

				} catch (Exception exc) {
					JOptionPane.showMessageDialog(frame, exc.getMessage(), "����", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		// ��ӷ��Ͱ�ť�¼�
		btn_send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				send();
			}
		});

		// �رմ����¼�
		frame.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				if (isConnected) {
					closeConnection();
				}
				System.exit(0);
			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}
		});

	}

	public void send() {
		if (!isConnected) {
			JOptionPane.showMessageDialog(frame, "��û�����ӷ��������޷�������Ϣ��", "����", JOptionPane.ERROR_MESSAGE);
			return;
		}
		String message = textField.getText().trim();
		if (message == null || message.equals("")) {
			JOptionPane.showMessageDialog(frame, "��Ϣ����Ϊ�գ�", "����", JOptionPane.ERROR_MESSAGE);
			return;
		}
		sendMessage(frame.getTitle() + "@" + "ALL" + "@" + message);
		textField.setText(null);

	}

	// ���ӷ�����
	public boolean connectServer(int port, String hostIp, String name) {
		try {
			socket = new Socket(hostIp, port);
			writer = new PrintWriter(socket.getOutputStream());
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// ���Ϳͻ��˵Ļ�����Ϣ
			sendMessage(name + "@" + socket.getLocalAddress().toString());
			textArea.append("��˿ں�Ϊ��" + port + "    IP��ַΪ��" + hostIp + "   �ķ��������ӳɹ�!" + "\r\n");
			// ����������Ϣ���߳�
			messageThread = new MessageThread(reader, textArea);
			messageThread.start();
			isConnected = true;// �Ѿ���������
//			
//			JOptionPane.showMessageDialog(frame, "�ɹ�����!");
			return true;
		} catch (IOException e) {
			textArea.append("��˿ں�Ϊ��" + port + "    IP��ַΪ��" + hostIp + "   �ķ���������ʧ��!" + "\r\n");
			isConnected = false;// δ������
			return false;

		}
	}

	// ������Ϣ
	public void sendMessage(String message) {
		writer.println(message);
		writer.flush();

	}

	@SuppressWarnings("deprecation")
	// �ͻ��������Ͽ�����
	public synchronized boolean closeConnection() {
		try {
			sendMessage("CLOSE");// ���ͶϿ����������������
			messageThread.stop();// ֹͣ������Ϣ�߳�
			// �ͷ���Դ
			if (reader != null) {
				reader.close();
			}
			if (writer != null) {
				writer.close();
			}
			if (socket != null) {
				socket.close();
			}
			isConnected = false;

			return true;
		} catch (IOException e1) {
			e1.printStackTrace();
			isConnected = true;
			return false;
		}

	}

	// ���Ͻ�����Ϣ���߳�
	class MessageThread extends Thread {
		private BufferedReader read;
		private JTextArea jta;

		public MessageThread(BufferedReader read, JTextArea jta) {
			this.read = read;
			this.jta = jta;
		}

		// �����ر�����
		public synchronized void closeCon() throws Exception {
			// ����û��б�
			listModel.removeAllElements();
			// �ر���Դ
			if (reader != null) {
				reader.close();
			}
			if (writer != null) {
				writer.close();
			}
			if (socket != null) {
				socket.close();
			}
			isConnected = false;
		}

		@Override
		public void run() {
			String message = "";
			while (true) {
				try {
					message = reader.readLine();
					StringTokenizer stringTokenizer = new StringTokenizer(message, "/@");
					String command = stringTokenizer.nextToken();// ����
					if (command.equals("CLOSE"))// �������ѹر�����
					{
						textArea.append("�������ѹر�!\r\n");
						closeCon();// �����Ĺر�����
						return;// �����߳�
					} else if (command.equals("ADD")) {// ���û����߸��������б�
						String username = "";
						String userIp = "";
						if ((username = stringTokenizer.nextToken()) != null
								&& (userIp = stringTokenizer.nextToken()) != null) {
							User user = new User(username, userIp);
							onLineUsers.put(username, user);
							listModel.addElement(username);
						}
					} else if (command.equals("DELETE")) {// ���û����߸����б�
						String username = stringTokenizer.nextToken();
						User user = onLineUsers.get(username);
						onLineUsers.remove(user);
						listModel.removeElement(username);

					} else if (command.equals("USERLIST")) {// �����û��б�
						int size = Integer.parseInt(stringTokenizer.nextToken());
						String username = null;
						String userIp = null;
						for (int i = 0; i < size; i++) {
							username = stringTokenizer.nextToken();
							userIp = stringTokenizer.nextToken();
							User user = new User(username, userIp);
							onLineUsers.put(username, user);
							listModel.addElement(username);
						}
					} else if (command.equals("MAX")) { // ����������
						textArea.append(stringTokenizer.nextToken() + stringTokenizer.nextToken() + "\r\n");
						closeCon();// �����Ĺر�����
						JOptionPane.showMessageDialog(frame, "������������������", "����", JOptionPane.ERROR_MESSAGE);
						return;// �����߳�

					} else { // ��ͨ��Ϣ
						textArea.append(message + "\r\n");
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}

			}
		}

	}

}
