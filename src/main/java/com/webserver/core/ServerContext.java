package com.webserver.core;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName: ServerContext
 * Function:  该类定义有关服务端配置信息
 * Date:      2019/11/6 14:13
 * @!author     Kenny
 * version    V1.0
 */
public class ServerContext {
    /**
     * servlet对应关系
     * key:请求路径
     * value:对应的Servlet类的完全限定名
     */
    private static final Map<String, String> SERVLET_MAPPING = new HashMap<>();

    static {
        /**
         * 初始化
         */
        initServletMapping();
    }

    /**
     * 初始化请求与对应的Servlet名字
     */
    private static void initServletMapping() {
//        SERVLET_MAPPING.put("/myweb/reg", "com.webserver.servlet.RegServlet");
//        SERVLET_MAPPING.put("/myweb/login", "com.webserver.servlet.LoginServlet");
        /**
         * 解析conf/servlets.xml
         * 将根元素<servlets>下的所有<servlet>元素取出
         * 并将每个<servlet>元素中的属性:
         * url的值作为key,className的值为value
         * 保存到SERVLET_MAPPING这个Map中完成初始化
         */

        try {
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new File("conf/servlets.xml"));
            Element root = doc.getRootElement();
            List<Element> list = root.elements("servlet");
            for (Element serletEle : list) {
                String key = serletEle.attributeValue("url");
                String value = serletEle.attributeValue("className");
                SERVLET_MAPPING.put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据请求获取对应的业务处理类的类名
     */
    public static String getServletName(String url) {
        return SERVLET_MAPPING.get(url);
    }
}
