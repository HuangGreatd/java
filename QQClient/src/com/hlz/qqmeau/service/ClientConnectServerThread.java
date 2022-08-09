package com.hlz.qqmeau.service;

import com.hlz.qqcommon.Message;
import com.hlz.qqcommon.MessageType;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.management.MemoryType;
import java.net.Socket;

public class ClientConnectServerThread extends Thread{
    //该线程 需要持有Socket
    private Socket socket;

    //接收一个Socket 对象
    public ClientConnectServerThread(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        // Thread 需要在后台 和 服务器通讯 因此用 while 循环
        while (true){
            System.out.println("客户端线程等待读取");
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();//如果服务器没有发生Message对象 线程会柱塞在这里
                //message
                //判断 message 类型 做相应处理
                //如果督导的是 服务端返回的在线用户列表
                if (message.getMesType().equals(MessageType.MESSAGE_RET_ONLINE_FRIEND)){
                    //取出在线列表信息
                    //规定
                    String[] onlineUsers = message.getContent().split(" ");
                    System.out.println("\n在线用户列表如下");
                    for (int i = 0; i < onlineUsers.length; i++) {
                        System.out.println("用户"+onlineUsers[i]);
                    }
                }else if (message.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)){
                    //显示在客户端的控制台
                    System.out.println("\n"+message.getSender() + "对大家说:" +message.getContent());
                }else if (message.getMesType().equals(MessageType.MESSAGE_FILE_MES)){
                    System.out.println("\n" + message.getSender() + "给"+message.getGetter() + "发文件" + message.getSrc() + "到"+message.getDest() );

                    //取出message 的字节数组 通过文件输出流写到磁盘
                    FileOutputStream fileOutputStream = new FileOutputStream(message.getDest());
                    fileOutputStream.write(message.getFileBytes());
                    fileOutputStream.close();
                    System.out.println("保存文件成功");
                }
                else if (message.getMesType().equals(MessageType.MESSAGE_COMM_MES)){
                    //把从服务器端转发的消息显示到控制台
                    System.out.println("\n"+message.getSender() + "对" + message.getSender()+ "说" + message.getContent());
                }
                else {
                    System.out.println("是其他类型的message 暂不处理");
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    //为了更方便得到Socket

    public Socket getSocket() {
        return socket;
    }
}
