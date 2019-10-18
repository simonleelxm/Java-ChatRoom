# 基于JAVA+Swing实现的多人局域网聊天室

### 设计思想

- UI界面采用Swing完成；

- 服务器：

  ​		在局域网下，利用Socket进行连线通信。当服务器启动时，因为要实现多线程聊天，创建一个集合把客户端线程装进集合里并创建一条Thread线程不断地等待客户端的连接，当有客户端连接时，创建一条客户端线程（参数为与客户端互连的Socket，由服务器控制），并将这条线程放到集合里，服务器端通过IO流反馈“上线用户”信息给客户端，持续接收。当服务器接收到CLOSE的消息则关闭客户端线程，并将此线程从集合里移除。

- 客户端：

  ​		当客户端启动时，与服务器的ServerSocket对接，同时开启一个持续接收消息的线程，使用命令@message的格式对接收的消息用@分割，进行命令判断。若主动断开连接则停止接收消息进程，同时反馈消息给服务器端。

### 主要类

##### `Server.java`类结构和主要功能

- 属性：
  - Swing组件：界面需求
  - ServerSocket：服务器端必须要有，对接Socket
  - ServerThread：只开一次就可以，持续接收客户端
  - ArrayList集合：作为容器装客户端线程
  - boolean isStart;：作为判定是否已经开启服务器的开关
- 方法：
  - main方法：创建类对象
  - 构造方法：UI界面的实现，添加各种事件（按钮，关闭窗口，输入框）
  - serveStart：启动服务器，在里面开启服务器线程
  - closeServer()：先遍历集合群发关闭命令(目的更新在线用户)，关闭各种IO流，Socket，关闭线程，移出集合
  - send()：发送消息，当满足条件时，调用群发消息方法向所有在线用户发送消息
  - sendServerMessage()：遍历客户端线程集合，给每一个在线客户端发送消息
- 内部类：
  - ServerThread：
    - 属性：ServerSocket、max(集合大小)
    - 方法：
      - 全参构造方法
      - run()：接收客户端的Socket，同时创建客户端线程
  - ClientThread：
    - 属性：socket、BufferedReader、PrintWriter、User
    - 方法：
      - 构造方法：反馈连接成功信息、
      - run()：不断接收客户端的消息，进行处理。
      - dispatcherMessage(String message)：转发消息

##### `Client.java`类结构和主要功能

- 属性：

  - Swing组件：界面需求
  - Socket：客户端必须要有
  - PrintWriter：输出流
  - BufferedRead：输入流
  - MessageThread：接收消息的线程
  - Map：存放在线用户	
  - isConnected：作为是否已经连接上服务器的开关

- 方法：

  - main方法：创建对象，初始化界面
  - 构造方法：定义图形化界面、添加各种事件(按钮、关闭窗口、输入框)
  - send()：获取输入框里的字符串，

- 内部类：

  

