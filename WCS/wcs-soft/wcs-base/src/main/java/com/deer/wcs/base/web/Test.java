package com.deer.wcs.base.web;

import com.deer.wcs.common.utils.StringUtils;
import com.deer.wcs.common.utils.spring.SpringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Test {


    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object bean = SpringUtils.getBean("com.deer.wcs.base.web.Aaa");
        List<Object> list = new ArrayList<>();
        invokeMethod(bean,"bbb",123);

    }
    private static void invokeMethod(Object bean, String methodName,Object parem )
            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException
    {
        Method method = bean.getClass().getMethod(methodName);
        method.invoke(bean,parem);

    }





}

