package com.webserver.servlet;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

import java.io.File;

/**
 * ClassName: HttpServlet
 * Function:  所有Servlet的超类
 * Date:      2019/11/6 14:43
 * @author     Kenny
 * version    V1.0
 */
public abstract class HttpServlet {

    /**
     * 业务处理方法
     * @param request
     * @param response
     */
    public abstract void service(HttpRequest request, HttpResponse response);

    public void forward(String path, HttpRequest request, HttpResponse response) {
        File file = new File("webapps/" + path);
        response.setEntity(file);
    }
}
