package com.webserver.core;

import java.io.RandomAccessFile;

/**
 * ClassName: ShowAllUserDemo
 * Function:  将user.dat文件中的每个用户信息读取出来并输出到控制台
 * Date:      2019/11/14 8:21
 * @author     Kenny
 * version    V1.0
 */
public class ShowAllUserDemo {
    public static void main(String[] args) throws Exception {
        RandomAccessFile raf = new RandomAccessFile("user.dat", "r");
        for (int i = 0; i < raf.length() / 100; i++) {
            //读取用户名
            //1读取32个字节
            byte[] data = new byte[32];
            raf.read(data);
            //2将其还原为字符串,trim是为了去除后面的留白部分
            String userName = new String(data,"UTF-8").trim();
            //读取密码
            raf.read(data);
            String passWord = new String(data,"UTF-8").trim();
            //读取昵称
            raf.read(data);
            String nickName = new String(data, "UTF-8").trim();
            //读取年龄
            int age = raf.readInt();
            System.out.println(userName + ", " + passWord + ", " + age);

            System.out.println("pos:" + raf.getFilePointer());
        }
        raf.close();

    }
}
