package com.webserver.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * ClassName: HttpResponse
 * Function:  响应对象
 * 该类的每个实例表示服务器发送给客户端的一个具体HTTP相应
 * 内容
 * 一个HTTP响应包含三部分
 * 状态行,响应头,响应正文
 * Date:      2019/11/6 11:30
 * author     Kenny
 * version    V1.0
 */
public class HttpResponse {
    //状态行相关信息定义
    /**
     * 状态代码,默认为:200
     */
    private int statusCode = 200;
    /**
     * 状态描述,默认为:"OK"
     */
    private String statusReason = "OK";

    /**
     * 响应头相关信息定义
     */
    private Map<String, String> headers = new HashMap<>();

    //响应正文相关信息定义

    /**
     * 响应的实体文件
     */
    private File entity;

    //和连接相关信息

    private Socket socket;
    private OutputStream out;

    public HttpResponse(Socket socket) {
        try {
            this.socket = socket;
            out = socket.getOutputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将当前响应对象内容按照标准的HTTP响应格式发送给
     * 客户端
     */
    public void flush(){
        /**
         * 1:发送状态行
         * 2:发送响应头
         * 3:发送响应正文
         */
        sendStatusLine();
        sendHeaders();
        sendContent();

    }

    private void sendStatusLine() {
        System.out.println("HttpResponse:开始发送状态行...");
        try {
            String line = "HTTP/1.1" + " " + statusCode + " " + statusReason;
            outLine(line);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("HttpResponse:发送状态行完毕");
    }

    private void outLine(String line) throws IOException {
        if (line != null && line.length() > 0) {
            out.write(line.getBytes("ISO8859-1"));
        }
        out.write(13);
        out.write(10);
    }

    private void sendHeaders() {
        System.out.println("HttpResponse:开始发送响应头...");
        try {
            Set<Map.Entry<String, String>> entrySet = headers.entrySet();
            for (Map.Entry<String, String> header : entrySet) {
                String key = header.getKey();
                String value = header.getValue();
                String line = key + ": " + value;
                outLine(line);
            }
            //单独发送一个CRLF表示响应头发送完毕
            outLine("");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("HttpResponse:发送响应头完毕");
    }

    private void sendContent() {
        System.out.println("HttpResponse:开始发送响应正文...");
        if (entity == null) {
            return;
        }
        try (FileInputStream fis = new FileInputStream(entity)) {
            int len = -1;
            byte[] data = new byte[1024 * 10];
            while ((len = fis.read(data) )!= -1) {
                out.write(data, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("HttpResponse:发送相应正文完毕");
    }

    public File getEntity(){
        return entity;
    }

    public void setEntity(File entity) {
        /**
         * 添加响应正文的实体文件
         * 在设置该文件的同时,会自动根据该文件添加两个
         * 响应头:Content-Type与Content-Length
         */
        String fileName = entity.getName();
        int index = fileName.lastIndexOf(".") + 1;
        String ext = fileName.substring(index);
        String contentType = HttpContext.getMimeType(ext);
        this.headers.put("Content-Type", contentType);

        /**
         * 添加Content-Length
         */
        this.headers.put("Content-Length", entity.length() + "");
        this.entity = entity;
    }

    public int getStatusCode(){
        return statusCode;
    }

    /**
     * 设置状态代码
     * 在设置的同时,内部会根据该状态代码去HttpContext中
     * 获取该代码对应的状态描述值并自动进行设置.
     * 这样做就省去了外界每次设置状态代码后还要单独进行状态
     * 描述的设置
     * 除非需要给该代码额外设置不同的状态描述值,否则就不用
     * 在调用setStatusReason方法了.
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        /**
         * 自动设置对应的描述
         */
        this.statusReason = HttpContext.getStatusReason(statusCode);
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    /**
     * 添加指定的响应头
     * @param key
     * @param value
     */
    public void putHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public String getHeader(String key) {
        return headers.get(key);
    }
}
