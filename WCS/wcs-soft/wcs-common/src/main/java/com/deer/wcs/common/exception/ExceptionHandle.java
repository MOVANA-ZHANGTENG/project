package com.deer.wcs.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component("ExceptionHandle")
public class ExceptionHandle {

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandle.class);

    @Autowired
    private DataSource dataSource;



    public void handle(Exception e){
        if (e instanceof BadSqlGrammarException) {
            String msg = e.getMessage();
            if (msg != null && (msg.contains("Unknown column") || msg.contains("doesn't exist"))) {
                String tableName = null;
                String columnName = null;
                java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("column '(.*?)' in 'field list'").matcher(msg);
                if (matcher.find()) {
                    columnName = matcher.group(1);
                }
                java.util.regex.Matcher sqlTableMatcher = java.util.regex.Pattern.compile("from[\t\n\r ]+([`\\w]+)", java.util.regex.Pattern.CASE_INSENSITIVE).matcher(msg);
                if (sqlTableMatcher.find()) {
                    tableName = sqlTableMatcher.group(1).replace("`", "");
                }
                if (tableName != null && columnName != null) {
                    try {
                        try (java.sql.Connection conn = dataSource.getConnection()) {
                            java.sql.DatabaseMetaData metaData = conn.getMetaData();
                            try (java.sql.ResultSet rs = metaData.getColumns(null, null, tableName, columnName)) {
                                if (!rs.next()) {
                                    String alterSql = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " VARCHAR(255) DEFAULT NULL";
                                    try (java.sql.Statement stmt = conn.createStatement()) {
                                        stmt.executeUpdate(alterSql);
                                        log.info("自动为表{}添加字段{}", tableName, columnName);
                                    }
                                }
                            }
                        }
                    } catch (Exception ex) {
                        log.error("自动修复数据库字段失败", ex);
                    }
                }
            }
        }
    }

}
