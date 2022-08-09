package com.hlz.qqserver.service;

import com.hlz.qqcommon.Message;
import com.hlz.qqcommon.MessageType;
import com.hlz.qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

//这是服务器 在监听9999 等待客户端的连接 保持 通信
public class QQServer {
    private ServerSocket ss = null;

    //创建一个集合，存放多个用户，如果时这些用户 就是合法的
    //也可以使用 ConcurrentHashMap 可以处理并发集合 没有线程安全问题
    // HashMap 没有处理线程安全 因此在多线程情况下时不安全的
    private static ConcurrentHashMap<String,User> validUsers = new ConcurrentHashMap<>();
    static {//静态代码块 初始化 validUsers
        validUsers.put("100",new User("100","123456"));
        validUsers.put("200",new User("200","123456"));
        validUsers.put("300",new User("300","123456"));
        validUsers.put("大圣",new User("大圣","123456"));
        validUsers.put("唐僧",new User("唐僧","123456"));
        validUsers.put("八戒",new User("八戒","123456"));

    }

    //验证用户是否有效方法
    private boolean checkUser(String userId,String passwd){
        User user = validUsers.get(userId);
        if (user == null){//说明userId没有在集合中
            System.out.println("说明userId没有在集合中");
            return false;
        }
        if (!user.getUserPwd().equals(passwd)){//id正确 但是密码错误
            System.out.println("密码不正确");
            return false;
        }
        return true;
    }

    public QQServer() {

        try {
            //注意 端口可以写在配置文件
            System.out.println("服务器在9999端口监听");
            //启动推送新闻的线程
            new Thread(new SendNewsToAllService()).start();
            ss = new ServerSocket(9999);
            while (true) {//当和某个客户端建立连接后 会继续监听 监听时循环的
                Socket socket = ss.accept(); //如果没有客户端连接 就会阻塞
                //得到socket 关联的 对象输入流
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                User u =(User) ois.readObject();//读取客户端发送的User对象
                //创建一个Message对象 准备回复客户端
                Message message = new Message();

                //对象的输出流
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                //验证
                if (checkUser(u.getUserId(), u.getUserPwd())){//登录通过

                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);

                    //将message对象 发送给 客户端
                    oos.writeObject(message);
                    //创建一个线程 和 客户端保持通信 该线程需要socket 对象
                    ServerConnectClientThread serverConnectClientThread = new ServerConnectClientThread(socket, u.getUserId());
                    //启动线程
                    serverConnectClientThread.start();
                }else {//登录失败
                    message.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                    oos.writeObject(message);
                    //关闭socket
                    socket.close();
                }

            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            //如果服务端退出了while循环 说明服务器段 不在监听 需要关闭资源
            try {
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
