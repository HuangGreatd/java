package com.hlz.qqmeau.service;

import com.hlz.qqcommon.Message;
import com.hlz.qqcommon.MessageType;

import java.awt.*;
import java.io.*;

//该类完成 文件传输服务
public class FileClientService {
    public void sendFileToOne(String src,String dest ,String senderId,String getterId){

        //读取src文件 必须存在 --》 封装到message 对象
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_FILE_MES);
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setSrc(src);
        message.setDest(dest);
        //System.out.println(message.);

        //需要对文件进行读取
        FileInputStream fileInputStream = null;
        //
        byte[] fileBytes = new byte[(int)new File(src).length()];

        try {
            fileInputStream = new FileInputStream(src);
            fileInputStream.read(fileBytes);//将 src文件 读入程序的字节数组
            //文件对应的字节数组设置message
            message.setFileBytes(fileBytes);

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (fileInputStream != null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //提示信息
        System.out.println("\n" + getterId + "给" + senderId + "发送文件" + src +"到" + dest);


        //发送
        try {
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
