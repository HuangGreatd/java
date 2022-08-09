package com.hlz.qqserver.service;

import com.hlz.qqcommon.Message;
import com.hlz.qqcommon.MessageType;
import com.hlz.util.Utility;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

public class SendNewsToAllService implements Runnable {


    //private Scanner scanner = new Scanner(System.in);
    @Override
    public void run() {
        //为了可以多次推送
        while (true) {
            System.out.println("请输入服务器要推送的消息[输出exit 表示退出推送服务]");
            String news = Utility.readString(1000);
            //构建一个消息  群发消息
            if ("exit".equals(news)){
                break;
            }
            Message message = new Message();
            message.setSender("服务器");
            message.setContent(news);
            message.setMesType(MessageType.MESSAGE_TO_ALL_MES);
            message.setSendTime(new Date().toString());
            System.out.println("服务器推送消息给所有人 说:" + news);

            //遍历所有在线用户 得到socket 并发送

            HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();
            Iterator<String> iterator = hm.keySet().iterator();
            while (iterator.hasNext()) {
                String onLineUserId = iterator.next().toString();
                ServerConnectClientThread serverConnectClientThread = hm.get(onLineUserId);
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    oos.writeObject(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
