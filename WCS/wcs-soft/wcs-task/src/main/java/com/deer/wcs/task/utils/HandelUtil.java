package com.deer.wcs.task.utils;

import com.deer.wcs.base.model.PathInfo;
import com.deer.wcs.common.utils.spring.SpringUtils;
import com.deer.wcs.task.model.CallBoxRecord;
import com.deer.wcs.task.model.JobInfo;
import com.deer.wcs.task.model.callBoxLG.CallBoxInfo;
import com.deer.wcs.task.model.CodeScanner.CodeScannerInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HandelUtil {

    public static void invokeMethod(String path, String methodName,Object param )
            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException
    {
        Object bean = SpringUtils.getBean(path);
        Method method = bean.getClass().getMethod(methodName,Integer.class);
        method.invoke(bean,param);
    }

    public static boolean invokeJobHandleMethod(String path, String methodName, JobInfo jobInfo )
            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException
    {
        Object bean = SpringUtils.getBean(path);
        Method method = bean.getClass().getMethod(methodName,JobInfo.class);
        return (boolean) method.invoke(bean,jobInfo);
    }


    public static boolean invokePathHandleMethod(String path, String methodName, PathInfo param )
            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException
    {
        Object bean = SpringUtils.getBean(path);
        Method method = bean.getClass().getMethod(methodName,PathInfo.class);
        return (boolean) method.invoke(bean,param);
    }

    public static void invokeCallBoxHandleMethod(String path, String methodName, CallBoxRecord record)
            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
             InvocationTargetException {
        Object bean = SpringUtils.getBean(path);
        Method method = bean.getClass().getMethod(methodName,CallBoxRecord.class);
        method.invoke(bean,record);
    }

    public static void invokeSmqHandleMethod(String path, String methodName, CodeScannerInfo codeScannerInfo)
            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException
    {
        Object bean = SpringUtils.getBean(path);
        Method method = bean.getClass().getMethod(methodName, CodeScannerInfo.class);
        method.invoke(bean, codeScannerInfo);
    }
}
