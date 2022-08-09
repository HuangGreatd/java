package com.hlz.qqmeau.view;

import com.hlz.qqmeau.service.FileClientService;
import com.hlz.qqmeau.service.MessageClientService;
import com.hlz.qqmeau.service.UserClientServer;
import com.hlz.qqmeau.util.Utility;

public class QQView {
    private boolean loop = true;
    private String key = "";
    private UserClientServer userClientServer = new UserClientServer();//对象用于登录 | 注册用户
    private MessageClientService messageClientService = new MessageClientService();//用户私聊 群里
    private FileClientService fileClientService = new FileClientService();//该对象用于传输文件
    public static void main(String[] args) {
        new QQView().mainMeau();
        System.out.println("退出客户端");
    }
    private void mainMeau(){
        while (loop){
            System.out.println("============欢迎登录网络通信系统=============");
            System.out.println("\t\t 1 登录系统");
            System.out.println("\t\t 9 退出系统");
            key = Utility.readString(1);

            //根据用户输入不同 进行不同逻辑判断
            switch (key){
                case "1":
                    System.out.print("请输入登录账号");
                    String userId = Utility.readString(50);
                    System.out.print("请输入登录密码");
                    String pwd = Utility.readString(50);
                    //需要到服务器检验用户信息
                    //编写一个类 UserClientServer
                    if (userClientServer.checkUser(userId,pwd)){
                        System.out.println("==============欢迎用户（"+ userId+"）==========");
                        //进入二级菜单
                        while (loop){
                                System.out.println("\n======网络通讯系统二级菜单(用户"+ userId +")======");
                                System.out.println("\t\t 1 显示在线用户列表");
                                System.out.println("\t\t 2 群发消息");
                                System.out.println("\t\t 3 私发消息");
                                System.out.println("\t\t 4 发送文件");
                                System.out.println("\t\t 9 退出系统");
                                key = Utility.readString(1);
                            switch (key){
                                case "1":
                                    //方法获取在线用户列表
                                    userClientServer.onlineFriendList();
                                    break;
                                case "2":
                                    System.out.println("请输入相对大家说的话:");
                                    String s = Utility.readString(100);
                                    //调用方法，将消息封装成message对象
                                    messageClientService.sendMessageToAll(s,userId);
                                    break;
                                case "3":
                                    System.out.print("请输入想聊天的用户号(在线用户):");
                                    String getterId = Utility.readString(40);
                                    System.out.print("请输入想说的话:");
                                    String content = Utility.readString(100);
                                    //编写方法 将私聊信息发送给服务端
                                    messageClientService.sendMessageToOne(content,userId,getterId);
                                    System.out.println("3");
                                    break;
                                case "4":
                                    System.out.println("请输入你想发送的对象!(在线)");
                                     getterId = Utility.readString(50);
                                    System.out.println("你要发送文件的完整路径(如d:\\xx.jpg)");
                                    String src = Utility.readString(100);
                                    System.out.println("请输入把文件发送到对方的路径(如d:\\xx.jpg)");
                                    String dest = Utility.readString(100);
                                    fileClientService.sendFileToOne(src,dest,userId,getterId);
                                    break;
                                case "9":
                                    //调用方法 给服务器退出系统的message
                                    userClientServer.logout();
                                    loop = false;
                                    break;

                            }
                        }
                    }else {
                        System.out.println("登录失败");
                    }
            }
        }
    }
}
