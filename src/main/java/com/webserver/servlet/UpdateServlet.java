package com.webserver.servlet;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * ClassName: UpdateServlet
 * Function:  修改密码
 * Date:      2019/11/6 15:39
 * @author     Kenny
 * version    V1.0
 */
public class UpdateServlet extends HttpServlet {
    @Override
    public void service(HttpRequest request, HttpResponse response) {
        System.out.println("UpdateServlet:开始修改密码!");
        //获取用户输入
        String username = request.getParameter("username");
        String oldpwd = request.getParameter("oldpwd");
        String newpwd = request.getParameter("newpwd");
        //修改密码
        try (
                RandomAccessFile raf
                        = new RandomAccessFile("user.dat","rw")
        ){
            boolean update = false;//是否修改成功
            for(int i=0;i<raf.length()/100;i++) {
                //移动指针到该条记录开始位置
                raf.seek(i*100);

                //读取用户名
                byte[] data = new byte[32];
                raf.read(data);
                String name = new String(data,"UTF-8").trim();
                //找到该用户
                if(name.equals(username)) {
                    //比密码
                    raf.read(data);
                    String pwd = new String(data,"UTF-8").trim();
                    if(pwd.equals(oldpwd)) {
                        //改新密码
                        //1先将指针移动到密码位置
                        raf.seek(i*100+32);
                        //2重新写密码
                        data = newpwd.getBytes("UTF-8");
                        data = Arrays.copyOf(data, 32);
                        raf.write(data);
                        update = true;
                    }
                    break;
                }
            }

            if(update) {
                forward("myweb/update_success.html", request, response);
            }else {
                forward("myweb/update_fail.html", request, response);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}
