package com.hlz.qqmeau.service;

import com.hlz.qqcommon.Message;
import com.hlz.qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;

//该对象和服务端提供和消息相关服务方法
public class MessageClientService {
    public void sendMessageToOne(String content,String sendId,String getterId){
        //构建 message
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_COMM_MES);//普通的聊天消息
        message.setSender(sendId);
        message.setGetter(getterId);
        message.setContent(content);
        message.setSendTime(new java.util.Date().toString());//发送时间 设置到message对象
        System.out.println(sendId + "对" +getterId +"说" + content);
        try {
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(sendId).getSocket().getOutputStream());

            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //
    public void sendMessageToAll(String content,String senderId){
        //构建 message
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_TO_ALL_MES);//普通的聊天消息
        message.setSender(senderId);
        message.setContent(content);
        message.setSendTime(new java.util.Date().toString());//发送时间 设置到message对象
        System.out.println(senderId + "对大家说" + content);
        try {
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());

            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
