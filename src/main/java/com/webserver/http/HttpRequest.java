package com.webserver.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: HttpRequest
 * Function:  请求对象
 * 该类的每个实例用于表示客户端游览器发送过来的一个具体
 * 的请求信息.
 * 一个请求包含三部分:请求行,消息头,消息正文
 * Date:      2019/11/4 15:50
 * @author     Kenny
 * version    V1.0
 */
public class HttpRequest {
    //请求行相关信息
    /**
     * 请求方式
     */
    private String method;
    /**
     * 资源路径
     * 包括路径和参数
     */
    private String url;
    /**
     * 协议版本
     */
    private String protocol;
    /**
     * url中的请求部分
     * 只有路径
     */
    private String requestURI;
    /**
     * url中的参数部分
     */
    private String queryString;
    /**
     * 所有参数
     * key:参数名
     * value:参数值
     */
    private Map<String, String> parameters = new HashMap<String, String>();
    /**
     * 消息头相关信息定义
     */
    private Map<String, String> headers = new HashMap<String, String>();

    //消息正文相关信息定义

    /**
     * 和客户端连接相关的属性
     */
    private Socket socket;
    private InputStream in;

    /**
     * 构造方法,用来初始化请求对象
     * 初始化就是解析请求的过程.这里会根据Socket获取
     * 输入流,用来读取客户端发送过来的请求内容,将内容
     * 解析出来并设置到请求对象的对应属性上.
     *
     * @param socket
     * @throws EmptyRequestException
     */
    public HttpRequest(Socket socket) throws EmptyRequestException {
        try {
            this.socket = socket;
            /**
             * 通过socket获取输入流,用于读取客户端发送的情求内容
             */
            this.in = socket.getInputStream();
            /**
             * 解析请求内容需要做三件事:
             * 1:解析请求行内容
             * 2:解析消息头内容
             * 3:解析消息正文内容
             */
            //1
            parseRequestLine();
            //2
            parseHeaders();
            //3
            parseContent();
        } catch (EmptyRequestException e) {
            //空请求抛给ClientHandler
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析请求行
     *
     * @throws EmptyRequestException
     */
    private void parseRequestLine() throws EmptyRequestException {
        System.out.println("HttpRequest:解析请求行...");
        /**
         * 先通过输入流读取第一行字符串(CRLF结尾),因为一个请求中
         * 第一行内容就是请求行内容
         */
        String line = readLine();

        //是否为空请求
        if ("".equals(line)) {
            throw new EmptyRequestException();
        }

        System.out.println("请求行:" + line);
        /**
         * 将请求行中对三部分信息:
         * method url protocol
         * 截取出来,并设置到对应得属性上
         *例如：
         * GET /index.html HTTP/1.1
         * 正则表达  \\s--》对应\和s两个字符
         */
        String[] data = line.split("\\s");
        method = data[0];
        url = data[1];

        //进一步解析URL
        parseURL();
    }

    /**
     * 进一步解析URL
     */
    private void parseURL() {
        System.out.println("HttpRequest:进一步解析url...");
        //判断当前请求中url是否含有参数?
        String str = "?";
        if (url.contains(str)) {
            String[] data = url.split("\\?");
            this.requestURI = data[0];
            //判断"?"后面是否有实际得参数部分
            if (data.length > 1) {
                this.queryString = data[1];
                try {
                    parseParameters(this.queryString);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else {
            this.requestURI = url;
        }
        System.out.println("requestURI:" + requestURI);
        System.out.println("queryString:" + queryString);
        System.out.println("parameters:" + parameters);

        System.out.println("HttpRequest:解析url完毕!");
    }

    /**
     * 解析参数
     *
     * @param line
     */
    private void parseParameters(String line) throws UnsupportedEncodingException {
        //先对参数中含有"%XX"进行转码
        line = URLDecoder.decode(line, "UTF-8");
        //进一步拆分参数
        String[] paraArr = line.split("&");
        //遍历每一个参数进行拆分参数名和参数值
        for (String para : paraArr) {
            //每个参数按照"="拆分
            String[] arr = para.split("=");
            if (arr.length > 1) {
                this.parameters.put(arr[0], arr[1]);
            } else {
                this.parameters.put(arr[0], null);
            }
        }
    }

    /**
     * 解析消息头
     */
    private void parseHeaders() {
        System.out.println("HttpRequest:解析消息头...");
        /**
         * 实现思路
         * 由于消息头是由多行构成的,对此我们应当循环的
         * 调用readLine方法读取每一行(每一个消息头),若
         * readLine方法返回的是一个空字符串时,说明应当
         * 单独读取到了CRLF,这就表示所有消息头都读取完毕
         * 了,那么就应当停止读取工作了.
         * 并且我们在读取每行,即:每个消息头后,应当将该
         * 消息头按照冒号空格拆分为两项(消息头格式为name: value)
         * 第一项应当是消息头名字,第二项为消息头的值.我们
         * 分别将他们以key, value存入到属性headers这个map
         * 中.这样我们最终就完成了解析消息头工作.
         */
        while (true) {
            String line = readLine();
            if ("".equals(line)) {
                break;
            }
            String[] data = line.split(": ");
            headers.put(data[0], data[1]);
        }
        System.out.println("headers:" + headers);
        System.out.println("HttpRequest:解析消息头完毕");
    }

    /**
     * 解析消息正文
     */
    private void parseContent() throws IOException {
        System.out.println("HttpRequest:解析消息正文...");
        /**
         * 判断是否含有消息正文:
         * 查看消息头中是否含有Content-Length
         */
        String contentLength = "Content-Length";
        String formType = "application/x-www-form-urlencoded";
        if (headers.containsKey(contentLength)) {
            int len = Integer.parseInt(headers.get("Content-Length"));
            byte[] data = new byte[len];
            in.read(data);
            /**
             * 判断是否含有消息正文:
             * 查看消息头Content-Type的值
             */
            String contentType = headers.get("Content-Type");
            //是否为form表单
            if (formType.equals(contentType)) {
                String line = new String(data, "ISO8859-1");
                System.out.println("正文内容:" + line);
                /**
                 * 将参数解析出来,存入到属性parameters中
                 */
                try {
                    parseParameters(line);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("HttpRequest:解析消息正文完毕...");
    }

    /**
     * 通过输入流in读取一行字符串
     * 连续读取若干字符,当读取到CRLF时停止,并将之前
     * 读取的所有字符以一个字符串形式返回.返回的字符串
     * 中不含有最后的CRLF.
     * CR:回车符   对应ASC编码值13
     * LF:换行符   对应ASC编码值10
     *
     * @return
     */
    private String readLine() {
        StringBuilder builder = new StringBuilder();
        try {
            /**
             * 记录上次读取到的字符
             */
            int pre = -1;
            /**
             * 记录本次读取到的字符
             */
            int cur = -1;
            while ((cur = in.read()) != -1) {
                //判断上次读取的是否为CR,本次是否为LF
                if (pre == 13 && cur == 10) {
                    break;
                }
                builder.append((char) cur);
                pre = cur;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString().trim();
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getParameter(String key) {
        return this.parameters.get(key);
    }
}
