package com.deer.wcs.framework.config;

import com.deer.wcs.common.annotation.DataSource;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.springframework.context.annotation.Bean;

import java.sql.SQLException;
import java.util.Properties;

//public class VendorDatabaseIdProvider implements DatabaseIdProvider {
//    void setProperties(Properties p) { // 从 3.5.2 开始，该方法为默认方法
//        // 空实现
//    }
//
//    @Override
//    public String getDatabaseId(javax.sql.DataSource dataSource) throws SQLException {
//        return dataSource.get;
//    }
//
//
//
//    @Bean
//    public DatabaseIdProvider databaseIdProvider() {
//        DatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
//        Properties p = new Properties();
//        p.setProperty("Oracle", "oracle");
//        p.setProperty("MySQL", "mysql");
//        p.setProperty("DM", "dm");
//        databaseIdProvider.setProperties(p);
//        return databaseIdProvider;
//    }
//}
