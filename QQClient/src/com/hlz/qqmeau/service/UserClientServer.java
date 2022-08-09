package com.hlz.qqmeau.service;

import com.hlz.qqcommon.Message;
import com.hlz.qqcommon.MessageType;
import com.hlz.qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

//该类完成用户登录验证 和 用户注册 等功能
public class UserClientServer {

    private User u = new User() ;//因为可能在 其他地方使用 user信息  所有做成成员属性
    //因为 socket 在其他地方也可能使用
    private Socket socket;

    //根据 userId 和 pwd 到服务器检验该用户是否合法
    public  boolean checkUser(String userId,String pwd) {
        boolean b = false;
        //创建User 对象
        u.setUserId(userId);
        u.setUserPwd(pwd);

        //连接到 服务端 发送u对象
        try {
            socket = new Socket("192.168.1.9", 9999);
            //得到ObjectOutPutStream 对象
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(u);//发送 User 对象

            //读取 从 服务端 回送 的Message 对象
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message ms = (Message) ois.readObject();
            if (ms.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCEED)){//登录成功

                //创建一个和服务器端保持通讯的线程 -》 c创建一个心累 ClientConnectServerThread
                ClientConnectServerThread clientConnectServerThread = new ClientConnectServerThread(socket);
                //启动客户端线程
                clientConnectServerThread.start();

                //为了客户端的扩展 将线程放入集合中管理
                ManageClientConnectServerThread.addClientConnectServerThread(userId,clientConnectServerThread);

                b = true;
            }else {
                //如果登录失败。。 我们就不能启动和服务器通信的线程，关闭socket
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



    return b;
    }

    //编写方法  退出客户端 并给服务端发送一个退出系统 message 对象
    public void logout(){
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
        message.setSender(u.getUserId());//一定要指明 是哪个 客户端

        //发送message
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
            System.out.println(u.getUserId()+"退出系统");
            System.exit(0);//结束进程
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 向服务器端 请求在线用户列表
    public void onlineFriendList(){
        //发生message 类型是 MESSAGE_GET_ONLINE_FRIEND

        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
        message.setSender(u.getUserId());

        //发送给服务器
        //应该得到当前线程的Socket 对应的 ObjectOutputStream 对象
        //ClientConnectServerThread clientConnectServerThread = ManageClientConnectServerThread.getClientConnectServerThread(u.getUserId());
        try {
            //等价于上 通过 userId 得到 Socket 线程对象
            //得到 userId 对应的线程对象 对应的 ObjectOutputStream 对象
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(u.getUserId()).getSocket().getOutputStream());

            oos.writeObject(message);//发送 message 对象 向服务端要在线用户列表

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}
