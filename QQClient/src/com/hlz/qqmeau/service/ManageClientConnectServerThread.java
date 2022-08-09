package com.hlz.qqmeau.service;

import java.util.HashMap;

//该类 管理 客户端 连接到 服务器端的线程 的类
public class ManageClientConnectServerThread {
    //把多个线程放入一个HashMap集合 key 就是用户id value 就是线程
    private static HashMap<String,ClientConnectServerThread> hm = new HashMap<>();

    //将某个线程加入到集合
    public static void addClientConnectServerThread(String userId,ClientConnectServerThread clientConnectServerThread){
        hm.put(userId, clientConnectServerThread);
    }

    //通过userId 可以得到对应的线程
    public static ClientConnectServerThread getClientConnectServerThread(String userId){
        return hm.get(userId);
    }
}
