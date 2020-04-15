package com.webserver.servlet;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * ClassName: RegServlet
 * Function:  用于处理注册业务
 * Date:      2019/11/6 15:32
 * @author     Kenny
 * version    V1.0
 */
public class RegServlet extends HttpServlet{
    @Override
    public void service(HttpRequest request, HttpResponse response) {
        /*
         * 注册流程:
         * 1:通过request获取用户提交的注册信息
         * 2:将用户信息写入文件user.dat中
         * 3:设置response对应的注册结果页面
         */
        System.out.println("RegServlet:开始注册...");
        //1
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String nickname = request.getParameter("nickname");
        int age = Integer.parseInt(
                request.getParameter("age")
        );
        System.out.println(username+","+password+","+nickname+","+age);
        /*
         * 2
         * 其中用户名，密码，昵称为字符串，年龄为整数
         * 因此我们每条记录占100字节
         * 用户名，密码，昵称各站32字节，年龄固定4字节。
         *
         */
        try (
                RandomAccessFile raf
                        = new RandomAccessFile("user.dat","rw")
        ){
            raf.seek(raf.length());
            //写用户名
            byte[] data = username.getBytes("UTF-8");
            data = Arrays.copyOf(data, 32);
            raf.write(data);

            //写密码
            data = password.getBytes("UTF-8");
            data = Arrays.copyOf(data, 32);
            raf.write(data);

            //写昵称
            data = nickname.getBytes("UTF-8");
            data = Arrays.copyOf(data, 32);
            raf.write(data);

            //写年龄
            raf.writeInt(age);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //3响应用户注册成功
        forward("myweb/reg_success.html", request, response);
        System.out.println("RegServlet:注册完毕!");

    }
}
