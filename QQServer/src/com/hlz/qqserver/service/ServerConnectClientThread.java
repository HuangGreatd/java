package com.hlz.qqserver.service;

import com.hlz.qqcommon.Message;
import com.hlz.qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

//该类的一个对象 和某个客户端 保持通讯
public class ServerConnectClientThread extends Thread {
    private Socket socket;
    private String userId;//连接到服务端的客户端id

    public ServerConnectClientThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {//线程处于run状态 可以发送、接收消息
        while (true){
            System.out.println("服务端和客户端保持通讯"+ userId +"读取数据...");
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message =(Message) ois.readObject();
                //后会使用message 根据message 的类型 做相应的业务处理
                if (message.getMesType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)) {
                    //客户端要在线用户列表
                    System.out.println(message.getSender() + " 要在线用户列表");
                    String onlineUser = ManageClientThreads.getOnlineUser();
                    //返回message
                    //构建一个Message 对象 返回给客户端
                    Message message2 = new Message();
                    message2.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                    message2.setContent(onlineUser);
                    message2.setGetter(message.getSender());
                    //写入数据通道socket 返回给客户端
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message2);

                }else if (message.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)){
                    //便利管理线程的集合 把所有的线程的socket 都得到 把 message 进行转发 即可
                    HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();
                    Iterator<String> iterator = hm.keySet().iterator();
                    while (iterator.hasNext()){
                        //取出在线用户的id
                        String onlineUserId = iterator.next().toString();
                        if (!onlineUserId.equals(message.getSender())){//排除群发消息的用户
                            //进行转发
                            ObjectOutputStream oos = new ObjectOutputStream(hm.get(onlineUserId).getSocket().getOutputStream());

                            oos.writeObject(message);//转发
                        }
                    }
                }else if (message.getMesType().equals(MessageType.MESSAGE_FILE_MES)){//文件消息
                    //根据getterid 获取对应的线程 进行message 对象转发
                    ServerConnectClientThread serverConnectClientThread = ManageClientThreads.getServerConnectClientThread(message.getGetter());
                    ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    //转发
                    oos.writeObject(message);
                }
                else if (message.getMesType().equals(MessageType.MESSAGE_CLIENT_EXIT)){//客户端推出
                    System.out.println(message.getSender()+"退出");
                    //将集合 中 对应的客户端移除
                    ManageClientThreads.removeServerConnectClientThread(message.getSender());

                    socket.close();//关闭连接
                    //退出线程
                    break;
                }else if (message.getMesType().equals(MessageType.MESSAGE_COMM_MES)){
                    //根据message 获取 getterid 然后在得到对应的线程
                    ServerConnectClientThread serverConnectClientThread = ManageClientThreads.getServerConnectClientThread(message.getGetter());
                    //得到对应的socket 对应的输出流 将 message对象转发给指定的客户端
                    ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());

                    oos.writeObject(message);//转发 如果客户不在线 可以保存到数据库 可以实现离线留言
                }
                else {
                    System.out.println("其他类型的message 暂不处理");
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

}
