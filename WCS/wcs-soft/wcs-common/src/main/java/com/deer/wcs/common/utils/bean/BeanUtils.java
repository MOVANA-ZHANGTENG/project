package com.deer.wcs.common.utils.bean;

import com.deer.wcs.common.annotation.ForUpdate;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Bean 工具类
 * 
 * @author ruoyi
 */
public class BeanUtils extends org.springframework.beans.BeanUtils
{
    /** Bean方法名中属性名开始的下标 */
    private static final int BEAN_METHOD_PROP_INDEX = 3;

    /** * 匹配getter方法的正则表达式 */
    private static final Pattern GET_PATTERN = Pattern.compile("get(\\p{javaUpperCase}\\w*)");

    /** * 匹配setter方法的正则表达式 */
    private static final Pattern SET_PATTERN = Pattern.compile("set(\\p{javaUpperCase}\\w*)");

    /**
     * Bean属性复制工具方法。
     * 
     * @param dest 目标对象
     * @param src 源对象
     */
    public static void copyBeanProp(Object dest, Object src)
    {
        try
        {
            copyProperties(src, dest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 获取对象的setter方法。
     * 
     * @param obj 对象
     * @return 对象的setter方法列表
     */
    public static List<Method> getSetterMethods(Object obj)
    {
        // setter方法列表
        List<Method> setterMethods = new ArrayList<Method>();

        // 获取所有方法
        Method[] methods = obj.getClass().getMethods();

        // 查找setter方法

        for (Method method : methods)
        {
            Matcher m = SET_PATTERN.matcher(method.getName());
            if (m.matches() && (method.getParameterTypes().length == 1))
            {
                setterMethods.add(method);
            }
        }
        // 返回setter方法列表
        return setterMethods;
    }

    /**
     * 获取对象的getter方法。
     * 
     * @param obj 对象
     * @return 对象的getter方法列表
     */

    public static List<Method> getGetterMethods(Object obj)
    {
        // getter方法列表
        List<Method> getterMethods = new ArrayList<Method>();
        // 获取所有方法
        Method[] methods = obj.getClass().getMethods();
        // 查找getter方法
        for (Method method : methods)
        {
            Matcher m = GET_PATTERN.matcher(method.getName());
            if (m.matches() && (method.getParameterTypes().length == 0))
            {
                getterMethods.add(method);
            }
        }
        // 返回getter方法列表
        return getterMethods;
    }

    /**
     * 检查Bean方法名中的属性名是否相等。<br>
     * 如getName()和setName()属性名一样，getName()和setAge()属性名不一样。
     * 
     * @param m1 方法名1
     * @param m2 方法名2
     * @return 属性名一样返回true，否则返回false
     */

    public static boolean isMethodPropEquals(String m1, String m2)
    {
        return m1.substring(BEAN_METHOD_PROP_INDEX).equals(m2.substring(BEAN_METHOD_PROP_INDEX));
    }

    public static void getModifyContent(Object o1, Object o2,List<String> result) throws Exception {
        Field[] fields1 = o1.getClass().getDeclaredFields();
        Field[] fields2 = o1.getClass().getDeclaredFields();
        for (int i = 0; i < fields1.length; i++) {
            fields1[i].setAccessible(true);
            fields2[i].setAccessible(true);
            Object temp1 = fields1[i].get(o1);
            Object temp2 = fields2[i].get(o2);
            if ((Objects.isNull(temp1) && Objects.isNull(temp2))) {
                continue;
            }
            if ((Objects.nonNull(temp1) && Objects.nonNull(temp2) && temp1.equals(temp2))) {
                continue;
            }
            result.add("将"+fields1[i].getName() + ",从【" + (temp1 == null ? null : String.valueOf(temp1)) + "】修改为【" + (temp2 == null ? null : String.valueOf(temp2))+"】");
        }
    }

    /**
     * 获取变更内容
     * @param oldBean 更改前的Bean
     * @param newBean 更改后的Bean
     * @param <T>
     * @return
     */
    public static <T> String getChangedFields(T oldBean, T newBean){
        Field[] fields = newBean.getClass().getDeclaredFields();
        StringBuilder builder = new StringBuilder();
        for(Field field : fields) {
            field.setAccessible(true);
            //skip filed without @ForUpdate
            if (!field.isAnnotationPresent(ForUpdate.class)) {
                continue;
            }
            try {
                Object oldValue = field.get(oldBean);
                Object newValue = field.get(newBean);
                if(!Objects.equals(newValue, oldValue)) {
                    builder.append(field.getAnnotation(ForUpdate.class).fieldName()); //获取字段名称
                    builder.append(": 【更改前：");
                    builder.append(oldValue);
                    builder.append(", 更改后：");
                    builder.append(newValue);
                    builder.append("】\n");
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return builder.toString();
    }
}
