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

	private MessageThread messageThread;// 负责接收消息的线程
	private Map<String, User> onLineUsers = new HashMap<String, User>();

	public static void main(String[] args) {
		new Client();
	}

	public Client() {
		btn_connect = new JButton("连接");
		btn_disconnect = new JButton("断开");
		btn_send = new JButton("发送");
		txt_port = new JTextField("6666");
		txt_hostIp = new JTextField("127.0.0.1");
		txt_name = new JTextField("龙龙88");
		textField = new JTextField();

		northPanel = new JPanel();
		northPanel.setLayout(new GridLayout());
		northPanel.add(new JLabel("端口"));
		northPanel.add(txt_port);
		northPanel.add(new JLabel("服务器IP"));
		northPanel.add(txt_hostIp);
		northPanel.add(new JLabel("姓名"));
		northPanel.add(txt_name);
		northPanel.add(btn_connect);
		northPanel.add(btn_disconnect);
		northPanel.setBorder(new TitledBorder("连接信息"));

		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setForeground(Color.blue);
		rightScroll = new JScrollPane(textArea);
		rightScroll.setBorder(new TitledBorder("消息显示区"));

		listModel = new DefaultListModel();
		userList = new JList(listModel);
		leftScroll = new JScrollPane(userList);
		leftScroll.setBorder(new TitledBorder("在线用户"));

		centerPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftScroll, rightScroll);
		centerPanel.setDividerLocation(100);

		southPanel = new JPanel(new BorderLayout());
		southPanel.setBorder(new TitledBorder("写消息"));
		southPanel.add(textField, "Center");
		southPanel.add(btn_send, "East");

		frame = new JFrame("客户机");
		frame.setLayout(new BorderLayout());
		frame.add(northPanel, "North");
		frame.add(southPanel, "South");
		frame.add(centerPanel, "Center");
		frame.setSize(600, 500);
		int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
		frame.setLocation((screen_width - frame.getWidth()) / 2, (screen_height - frame.getHeight()) / 2);

		frame.setVisible(true);

		// 添家文本框键盘按钮
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

		// 添加连接按钮事件
		btn_connect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int port;
				if (isConnected) {
					JOptionPane.showMessageDialog(frame, "已处于连接上状态，不要重复连接!", "错误", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					try {
						port = Integer.parseInt(txt_port.getText().trim());
					} catch (NumberFormatException e2) {
						throw new Exception("端口号不符合要求!端口为整数!");
					}
					String hostIp = txt_hostIp.getText().trim();
					String name = txt_name.getText().trim();
					if (name.equals("") || hostIp.equals("")) {
						throw new Exception("姓名、服务器IP不能为空!");
					}
					boolean flag = connectServer(port, hostIp, name);
					if (flag == false) {
						throw new Exception("与服务器连接失败!");
					}
					frame.setTitle(name);

				} catch (Exception exc) {
					JOptionPane.showMessageDialog(frame, exc.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		// 添加断开按钮事件
		btn_disconnect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (!isConnected) {
					JOptionPane.showMessageDialog(frame, "已处于断开状态，不要重复断开!", "错误", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					boolean flag = closeConnection();// 断开接连
					if (flag == false) {
						throw new Exception("断开连接发生异常！");
					}
					JOptionPane.showMessageDialog(frame, "成功断开!");
					listModel.removeAllElements();

				} catch (Exception exc) {
					JOptionPane.showMessageDialog(frame, exc.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		// 添加发送按钮事件
		btn_send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				send();
			}
		});

		// 关闭窗口事件
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
			JOptionPane.showMessageDialog(frame, "还没有连接服务器，无法发送消息！", "错误", JOptionPane.ERROR_MESSAGE);
			return;
		}
		String message = textField.getText().trim();
		if (message == null || message.equals("")) {
			JOptionPane.showMessageDialog(frame, "消息不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
			return;
		}
		sendMessage(frame.getTitle() + "@" + "ALL" + "@" + message);
		textField.setText(null);

	}

	// 连接服务器
	public boolean connectServer(int port, String hostIp, String name) {
		try {
			socket = new Socket(hostIp, port);
			writer = new PrintWriter(socket.getOutputStream());
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// 发送客户端的基本信息
			sendMessage(name + "@" + socket.getLocalAddress().toString());
			textArea.append("与端口号为：" + port + "    IP地址为：" + hostIp + "   的服务器连接成功!" + "\r\n");
			// 开启接收消息的线程
			messageThread = new MessageThread(reader, textArea);
			messageThread.start();
			isConnected = true;// 已经连接上了
//			
//			JOptionPane.showMessageDialog(frame, "成功连接!");
			return true;
		} catch (IOException e) {
			textArea.append("与端口号为：" + port + "    IP地址为：" + hostIp + "   的服务器连接失败!" + "\r\n");
			isConnected = false;// 未连接上
			return false;

		}
	}

	// 发送消息
	public void sendMessage(String message) {
		writer.println(message);
		writer.flush();

	}

	@SuppressWarnings("deprecation")
	// 客户端主动断开连接
	public synchronized boolean closeConnection() {
		try {
			sendMessage("CLOSE");// 发送断开连接命令给服务器
			messageThread.stop();// 停止接受消息线程
			// 释放资源
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

	// 不断接收消息的线程
	class MessageThread extends Thread {
		private BufferedReader read;
		private JTextArea jta;

		public MessageThread(BufferedReader read, JTextArea jta) {
			this.read = read;
			this.jta = jta;
		}

		// 被动关闭连接
		public synchronized void closeCon() throws Exception {
			// 清空用户列表
			listModel.removeAllElements();
			// 关闭资源
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
					String command = stringTokenizer.nextToken();// 命令
					if (command.equals("CLOSE"))// 服务器已关闭命令
					{
						textArea.append("服务器已关闭!\r\n");
						closeCon();// 被动的关闭连接
						return;// 结束线程
					} else if (command.equals("ADD")) {// 有用户上线更新在线列表
						String username = "";
						String userIp = "";
						if ((username = stringTokenizer.nextToken()) != null
								&& (userIp = stringTokenizer.nextToken()) != null) {
							User user = new User(username, userIp);
							onLineUsers.put(username, user);
							listModel.addElement(username);
						}
					} else if (command.equals("DELETE")) {// 有用户下线更新列表
						String username = stringTokenizer.nextToken();
						User user = onLineUsers.get(username);
						onLineUsers.remove(user);
						listModel.removeElement(username);

					} else if (command.equals("USERLIST")) {// 加载用户列表
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
					} else if (command.equals("MAX")) { // 人数已上限
						textArea.append(stringTokenizer.nextToken() + stringTokenizer.nextToken() + "\r\n");
						closeCon();// 被动的关闭连接
						JOptionPane.showMessageDialog(frame, "服务器缓冲区已满！", "错误", JOptionPane.ERROR_MESSAGE);
						return;// 结束线程

					} else { // 普通消息
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
