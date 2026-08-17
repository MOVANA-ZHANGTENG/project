package com.deer.wcs.common.utils;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;

/**
 * @description: 工具类用于连接webService接口
 * @author:zfj
 * @date:2024/11/18 10:45
 */
public class WebserviceUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebserviceUtils.class);

    /**
     * @param wsdlUrl wsdl的url地址
     * @param nameSpace 命名空间名称
     * @param methodName webService方法名称
     * 用于连接webservice无参接口
     * @return object
     */
    public Object sendWsdl(String wsdlUrl,String nameSpace,String methodName) {
        LOGGER.info("--------调用webservice接口begin-------");
        // 创建动态客户端
        JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();

        //对方的wsdl地址
        Client client = dcf.createClient(wsdlUrl);
        Object[] result = null;
        try {

            QName operationName  = new QName(nameSpace, methodName);                                            //*原文章链接：https://blog.csdn.net/qq_27471405/article/details/105275657     * 其他均为盗版，公众号：灵儿的笔记(zygxsq)
            result = client.invoke(operationName); //参数1，参数2，参数3......按顺序放就看可以
            LOGGER.info("返回数据:" + result[0]);

        } catch (Exception e) {
            String errMsg = "WebService发生异常！";
            result = new Object[] { errMsg };
            LOGGER.error(errMsg, e);
        }
        LOGGER.info("--------调用webservice接口end-------");
        return result[0];
    }

    /**
     * @param wsdlUrl wsdl的url地址
     * @param nameSpace 命名空间名称
     * @param methodName webService方法名称
     * @param objects 传入参数
     * 用于连接webservice有参接口
     * @return object
     */
    public Object sendWsdl(String wsdlUrl,String nameSpace,String methodName,Object... objects) {
        LOGGER.info("--------调用webservice接口begin-------");
        // 创建动态客户端
        JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();

        //对方的wsdl地址
        Client client = dcf.createClient(wsdlUrl);
        Object[] result = null;
        try {

            QName operationName  = new QName(nameSpace, methodName);                                            //*原文章链接：https://blog.csdn.net/qq_27471405/article/details/105275657     * 其他均为盗版，公众号：灵儿的笔记(zygxsq)
            result = client.invoke(operationName,objects); //参数1，参数2，参数3......按顺序放就看可以
            LOGGER.info("返回数据:" + result[0]);

        } catch (Exception e) {
            String errMsg = "WebService发生异常！";
            result = new Object[] { errMsg };
            LOGGER.error(errMsg, e);
        }
        LOGGER.info("--------调用webservice接口end-------");
        return result[0];
    }

}
