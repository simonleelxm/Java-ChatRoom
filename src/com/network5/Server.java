package com.network5;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
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

public class Server {
	private JFrame frame;
	private JTextArea contentArea;
	private JTextField txt_message;
	private JTextField txt_max;
	private JTextField txt_port;
	private JButton btn_start;
	private JButton btn_stop;
	private JButton btn_send;
	private JPanel northPanel;
	private JPanel southPanel;
	private JScrollPane rightPanel;
	private JScrollPane leftPanel;
	private JSplitPane centerSplit;
	private JList userList;
	private DefaultListModel listModel;

	private ServerSocket serverSocket;
	private ServerThread serverThread;
	private ArrayList<ClientThread> clients;

	private boolean isStart = false;

	public static void main(String[] args) {
		new Server();
	}

	// 消息发送方法
	public void send() {
		if (!isStart) {
			JOptionPane.showMessageDialog(frame, "服务器还没启动，不能发送消息！", "错误", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (clients.size() == 0) {
			JOptionPane.showMessageDialog(frame, "没有用户在线，不能发送消息！", "错误", JOptionPane.ERROR_MESSAGE);
			return;
		}
		String message = txt_message.getText().trim();
		if (message == null || "".equals(message)) {
			JOptionPane.showMessageDialog(frame, "消息不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
			return;
		}
		sendServerMessage(message);
		contentArea.append("服务器说：" + txt_message.getText() + "\r\n");
		txt_message.setText(null);

	}

	// 服务器构造方法
	public Server() {
		frame = new JFrame("Chat Server");
		contentArea = new JTextArea();
		contentArea.setEditable(false);
		contentArea.setForeground(Color.blue);

		txt_message = new JTextField();
		txt_max = new JTextField("30");
		txt_port = new JTextField("6666");

		btn_send = new JButton("发送");
		btn_start = new JButton("启动");
		btn_stop = new JButton("停止");
		btn_stop.setEnabled(false);

		listModel = new DefaultListModel();
		userList = new JList(listModel);

		southPanel = new JPanel(new BorderLayout());
		southPanel.setBorder(new TitledBorder("写消息"));
		southPanel.add(txt_message, "Center");
		southPanel.add(btn_send, "East");

		leftPanel = new JScrollPane(userList);
		leftPanel.setBorder(new TitledBorder("在线用户"));

		rightPanel = new JScrollPane(contentArea);
		rightPanel.setBorder(new TitledBorder("聊天框"));

		centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
		centerSplit.setDividerLocation(100);

		northPanel = new JPanel();
		northPanel.setLayout(new GridLayout());
		northPanel.add(new JLabel("人数上限"));
		northPanel.add(txt_max);
		northPanel.add(new JLabel("端口号"));
		northPanel.add(txt_port);
		northPanel.add(btn_start);
		northPanel.add(btn_stop);
		northPanel.setBorder(new TitledBorder("配置信息"));

		frame.setLayout(new BorderLayout());
		frame.add(southPanel, "South");
		frame.add(centerSplit, "Center");
		frame.add(northPanel, "North");
		frame.setSize(600, 400);
		frame.setVisible(true);
		int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
		frame.setLocation((screen_width - frame.getWidth()) / 2, (screen_height - frame.getHeight()) / 2);

		// 关闭窗口事件
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (isStart) {
//					closeServer();
				}
				System.exit(0);
			}
		});

		// 文本框按回车发送事件
		txt_message.addKeyListener(new KeyListener() {

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

		// 单击发送按钮事件
		btn_send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				send();

			}
		});

		// 单击启动服务器按钮事件
		btn_start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (isStart) {
					JOptionPane.showMessageDialog(frame, "服务器已处于启动状态，不要重复启动！", "错误", JOptionPane.ERROR_MESSAGE);
					return;

				}
				int max;
				int port;
				try {
					try {
						max = Integer.parseInt(txt_max.getText());
					} catch (Exception e1) {
						throw new Exception("人数上限为正整数！");
					}
					if (max <= 0) {
						throw new Exception("人数上限为正整数！");
					}
					try {
						port = Integer.parseInt(txt_port.getText());
					} catch (Exception e1) {
						throw new Exception("端口号为正整数！");
					}
					if (port <= 0) {
						throw new Exception("端口号 为正整数！");
					}
					serverStart(max, port);
					contentArea.append("服务器已成功启动!人数上限：" + max + ",端口：" + port + "\r\n");
					JOptionPane.showMessageDialog(frame, "服务器成功启动!");
					btn_start.setEnabled(false);
					txt_max.setEnabled(false);
					txt_port.setEnabled(false);
					btn_stop.setEnabled(true);
				} catch (Exception exc) {
					JOptionPane.showMessageDialog(frame, exc.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				}

			}
		});

		// 单击停止服务器按钮事件
		btn_stop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (!isStart) {
					JOptionPane.showMessageDialog(frame, "服务器还未启动，无需停止！", "错误", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					closeServer();
					btn_start.setEnabled(true);
					txt_max.setEnabled(true);
					txt_port.setEnabled(true);
					btn_stop.setEnabled(false);
					contentArea.append("服务器成功停止!\r\n");
					JOptionPane.showMessageDialog(frame, "服务器成功停止！");
				} catch (Exception exc) {
					JOptionPane.showMessageDialog(frame, "停止服务器发生异常！", "错误", JOptionPane.ERROR_MESSAGE);
				}

			}
		});

	}

	// 启动服务器
	public void serverStart(int max, int port) throws IOException {
		try {
			clients = new ArrayList<ClientThread>();
			serverSocket = new ServerSocket(port);
			serverThread = new ServerThread(serverSocket, max);
			serverThread.start();
			isStart = true;

		} catch (BindException e) {
			isStart = false;
			System.out.println("端口号被占用,请更换端口号");

		} catch (IOException e1) {
			e1.printStackTrace();
			isStart = false;
			System.out.println("服务器启动异常");
		}
	}

	// 关闭服务器
	@SuppressWarnings("deprecation")
	public void closeServer() {
		try {
			if (serverThread != null) {
				serverThread.stop();
			}
			for (int i = clients.size() - 1; i >= 0; i--) {
				// 给所有在线用户发送关闭命令
				clients.get(i).getWriter().println("CLOSE");
				clients.get(i).getWriter().flush();
				// 释放资源
				clients.get(i).stop();// 停止此条为客户端服务的线程
				clients.get(i).reader.close();
				clients.get(i).writer.close();
				clients.get(i).socket.close();
				clients.remove(i);
			}
			if (serverSocket != null) {
				serverSocket.close();// 关闭服务器端连接
			}
			listModel.removeAllElements();// 清空用户列表
			isStart = false;

		} catch (IOException e) {
			e.printStackTrace();
			isStart = true;
		}
	}

	// 群发服务器消息
	public void sendServerMessage(String message) {
		for (int i = clients.size() - 1; i >= 0; i--) {
			clients.get(i).getWriter().println("服务器说：" + message + "(多人发送)");
			clients.get(i).getWriter().flush();
		}
	}

	// 服务器线程
	class ServerThread extends Thread {
		private ServerSocket serverSocket;
		private int max;// 最大在线用户

		public ServerThread(ServerSocket ss, int max) {
			this.serverSocket = ss;
			this.max = max;
		}

		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				try {
					Socket socket = serverSocket.accept();
					if (clients.size() == max) {
						BufferedReader r = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						PrintWriter w = new PrintWriter(socket.getOutputStream());
						// 接收客户端的基本用户信息
						String inf = r.readLine();
						StringTokenizer st = new StringTokenizer(inf, "@");
						User user = new User(st.nextToken(), st.nextToken());
						// 反馈连接信息
						w.println("MAX@服务器：对不起，" + user.getName() + user.getIp() + "，服务器在线人数已达上限，请稍后尝试连接！");
						w.flush();
						// 释放资源
						r.close();
						w.close();
						socket.close();
						continue;

					}
					ClientThread client = new ClientThread(socket);
					client.start();
					System.out.println("测试代码！！！");
					clients.add(client);
					listModel.addElement(client.getUser().getName());// 更新在线列表
					contentArea.append(client.getUser().getName() + client.getUser().getIp() + "上线!\r\n");

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	// 一个客户端的线程
	class ClientThread extends Thread {
		private Socket socket;
		private BufferedReader reader;
		private PrintWriter writer;
		private User user;

		public Socket getSocket() {
			return socket;
		}

		public void setSocket(Socket socket) {
			this.socket = socket;
		}

		public BufferedReader getReader() {
			return reader;
		}

		public void setReader(BufferedReader reader) {
			this.reader = reader;
		}

		public PrintWriter getWriter() {
			return writer;
		}

		public void setWriter(PrintWriter writer) {
			this.writer = writer;
		}

		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}

		// 客户端线程的构造方法
		public ClientThread(Socket socket) {
			try {
				this.socket = socket;
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new PrintWriter(socket.getOutputStream());
				// 获取客户端的基本用户信息
				String info = reader.readLine();
				StringTokenizer st = new StringTokenizer(info, "@");
				user = new User(st.nextToken(), st.nextToken());
				// 反馈连接成功信息
				writer.println(user.getName() + user.getIp() + "与服务器连接成功");
				// 反馈当前在线用户信息
				if (clients.size() > 0) {
					String temp = "";
					for (int i = clients.size() - 1; i >= 0; i--) {
						temp += (clients.get(i).getUser().getName() + "/" + clients.get(i).getUser().getIp()) + "@";
					}
					writer.println("USERLIST@" + clients.size() + "@" + temp);
					writer.flush();

				}
				// 向所有在线用户发送该用户上线命令
				for (int i = clients.size() - 1; i >= 0; i--) {
					clients.get(i).getWriter().println("ADD@" + user.getName() + user.getIp());
					clients.get(i).getWriter().flush();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// 不断接收客户端的消息，进行处理
		@Override
		@SuppressWarnings("deprecation")
		public void run() {
			String message = null;
			while (true) {
				try {
					message = reader.readLine();
					if (message.equals("CLOSE")) {
						contentArea.append(this.getUser().getName() + this.getUser().getIp() + "下线!\r\n");
						// 关闭资源
						reader.close();
						writer.close();
						socket.close();
						// 向所有在线用户发送离线消息
						for (int i = clients.size() - 1; i >= 0; i--) {
							clients.get(i).getWriter().println("DELETE@" + user.getName());
							clients.get(i).getWriter().flush();
						}
						// 更新在线列表
						System.out.println(user.getName());
						listModel.removeElement(user.getName());// 更新在线列表

						// 删除此条客户端进程
						for (int i = clients.size() - 1; i >= 0; i--) {
							if (clients.get(i).getUser() == user) {
								ClientThread temp = clients.get(i);
								clients.remove(i);
								temp.stop();
								return;
							}
						}
					} else {
						dispatcherMessage(message);// 转发消息
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		// 转发消息
		public void dispatcherMessage(String message) {
			StringTokenizer stringTokenizer = new StringTokenizer(message, "@");
			String source = stringTokenizer.nextToken();
			System.out.println(source);
			String owner = stringTokenizer.nextToken();
			System.out.println(owner);
			String content = stringTokenizer.nextToken();
			System.out.println(content);
			message = source + "说：" + content;
			contentArea.append(message + "\r\n");
			if (owner.equals("ALL")) {// 群发
				for (int i = clients.size() - 1; i >= 0; i--) {
					clients.get(i).getWriter().println(message + "(多人发送)");
					clients.get(i).getWriter().flush();
				}
			}

		}

	}

}
