package com.deer.wcs.framework.web.exception;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import com.deer.wcs.common.core.domain.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import com.deer.wcs.common.constant.HttpStatus;
import com.deer.wcs.common.exception.DemoModeException;
import com.deer.wcs.common.exception.ServiceException;
import com.deer.wcs.common.utils.StringUtils;

/**
 * 全局异常处理器
 * 
 * @author ruoyi
 */
@RestControllerAdvice
public class GlobalExceptionHandler
{
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 权限校验异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Result handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',权限校验失败'{}'", requestURI, e.getMessage());
        return Result.error(HttpStatus.FORBIDDEN, "没有权限，请联系管理员授权");
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
                                                      HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',不支持'{}'请求", requestURI, e.getMethod());
        return Result.error(e.getMessage());
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(ServiceException.class)
    public Result handleServiceException(ServiceException e, HttpServletRequest request)
    {
        log.error(e.getMessage(), e);
        Integer code = e.getCode();
        return StringUtils.isNotNull(code) ? Result.error(code, e.getMessage()) : Result.error(e.getMessage());
    }

    /**
     * 请求路径中缺少必需的路径变量
     */
    @ExceptionHandler(MissingPathVariableException.class)
    public Result handleMissingPathVariableException(MissingPathVariableException e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        log.error("请求路径中缺少必需的路径变量'{}',发生系统异常.", requestURI, e);
        return Result.error(String.format("请求路径中缺少必需的路径变量[%s]", e.getVariableName()));
    }

    /**
     * 请求参数类型不匹配
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        log.error("请求参数类型不匹配'{}',发生系统异常.", requestURI, e);
        return Result.error(String.format("请求参数类型不匹配，参数[%s]要求类型为：'%s'，但输入值为：'%s'", e.getName(), e.getRequiredType().getName(), e.getValue()));
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生未知异常.", requestURI, e);
        return Result.error(e.getMessage());
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生系统异常.", requestURI, e);
        return Result.error(e.getMessage());
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public Result handleBindException(BindException e)
    {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return Result.error(message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e)
    {
        log.error(e.getMessage(), e);
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return Result.error(message);
    }

    /**
     * 演示模式异常
     */
    @ExceptionHandler(DemoModeException.class)
    public Result handleDemoModeException(DemoModeException e)
    {
        return Result.error("演示模式，不允许操作");
    }

    @Autowired
    private DataSource dataSource;

    /**
     * 捕获 SQL 语法异常（如字段不存在），自动修复缺失字段
     */
    @ExceptionHandler(BadSqlGrammarException.class)
    public Result handleBadSqlGrammarException(BadSqlGrammarException e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生SQL语法异常.", requestURI, e);
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
                                    return Result.error("数据库字段缺失已自动修复，请重试操作。");
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    log.error("自动修复数据库字段失败", ex);
                }
            }
        }
        return Result.error(e.getMessage());
    }
}
