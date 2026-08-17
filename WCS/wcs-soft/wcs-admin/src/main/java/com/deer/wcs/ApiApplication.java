package com.deer.wcs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 启动程序
 * 
 * @author deer
 */
@EnableScheduling
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class }, scanBasePackages = "com.deer.wcs")
@MapperScan("com.deer.wcs.xlPro.dao")
public class ApiApplication
{
    public static void main(String[] args)
    {
         System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(ApiApplication.class, args);
        System.out.println(".-------------------------- --------------------------------------.\n" +
                "|                                                                 |\n" +
                "|                                  |||                            |\n" +
                "|                                   |||                           |\n" +
                "|                        ||         |||                           |\n" +
                "|                        ||         |||                           |\n" +
                "|                   ||| |||         |||||||||                     |\n" +
                "|                   ||| |||   ||||||||||   |||                    |\n" +
                "|                   ||| |||   |||   |||    |||                    |\n" +
                "|                    || |||  |||    |||    |||                    |\n" +
                "|                    || |||  |||    |||    |||                    |\n" +
                "|                   ||  ||   |||    |||    |||                    |\n" +
                "|                   ||  ||   |||    |||   ||||                    |\n" +
                "|                   |  ||     ||    ||| |||||                     |\n" +
                "|                      ||           |||   ||                      |\n" +
                "|                     ||            ||                            |\n" +
                "|                    ||             ||                            |\n" +
                "|                   |               ||                            |\n" +
                "|                                   |                             |\n" +
                "|                                   |                             |\n" +
                "|                                       deer-wcs (仓库控制)         |\n" +
                "|                                                                 |\n" +
                "|                                      --by 原驰智造                |\n" +
                "-------------------------- ----------------------------------------");
    }
}
