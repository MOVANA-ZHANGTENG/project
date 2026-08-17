package com.deer.wcs.framework.config;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.TimeZone;
import java.util.UUID;

/**
 * 程序注解配置
 *
 * @author ruoyi
 */
@Configuration
// 表示通过aop框架暴露该代理对象,AopContext能够访问
@EnableAspectJAutoProxy(exposeProxy = true)
// 指定要扫描的Mapper类的包的路径
//@MapperScan(basePackages = "com.deer.wcs.**.dao")
public class ApplicationConfig
{

    /**
     * 时区配置
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomization()
    {
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder.timeZone(TimeZone.getDefault());
    }

    /**
     * UUID，通用唯一识别码,是由一组32位数的16进制数字所构成，
     * 可以产生一个号称全球唯一的ID，可以用来命名文件、
     * 变量以及数据库的ID主键等属于唯一的元素。
     * Java来获取UUID
     * @param args
     */
    public static void main(String[] args) {

        String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
        System.out.println(uuid);
        System.out.println("-----------------------------------------------------------------");
        String s = UUID.randomUUID().toString();
        System.out.println(s);

    }
}
