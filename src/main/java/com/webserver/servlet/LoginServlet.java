package com.webserver.servlet;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

import java.io.RandomAccessFile;

/**
 * ClassName: LoginServlet
 * Function:  处理登录业务
 * Date:      2019/11/6 15:37
 * @author     Kenny
 * version    V1.0
 */
public class LoginServlet extends HttpServlet{
    @Override
    public void service(HttpRequest request, HttpResponse response) {
        System.out.println("开始登录...");
        //1获取用户登录信息
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        //2登录验证
        try (
                RandomAccessFile raf
                        = new RandomAccessFile("user.dat","r");
        ){
            //默认表示登录失败
            boolean check = false;

            for(int i=0;i<raf.length()/100;i++) {
                //现将指针移动到该条记录的开始位置
                raf.seek(i*100);
                //读取用户名
                byte[] data = new byte[32];
                raf.read(data);
                String name = new String(data,"UTF-8").trim();
                //是否为此用户
                if(name.equals(username)) {
                    //读取密码
                    raf.read(data);
                    String pwd = new String(data,"UTF-8").trim();
                    if(pwd.equals(password)) {
                        //登录成功
                        forward("myweb/login_success.html", request, response);
                        check = true;
                    }
                    break;
                }
            }
            //判断是否登录成功
            if(!check) {
                forward("myweb/login_fail.html", request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("登录完毕!");
    }
}
