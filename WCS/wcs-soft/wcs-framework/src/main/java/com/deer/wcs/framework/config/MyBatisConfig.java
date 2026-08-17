package com.deer.wcs.framework.config;

import com.deer.wcs.common.utils.StringUtils;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import tk.mybatis.mapper.common.BaseMapper;
import tk.mybatis.spring.mapper.MapperScannerConfigurer;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.*;

/**
 * Mybatis支持*匹配扫描包
 * 
 * @author ruoyi
 */
@Configuration
public  class MyBatisConfig
{
    @Autowired
    private Environment env;



    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    public static String setTypeAliasesPackage(String typeAliasesPackage)
    {
        ResourcePatternResolver resolver = (ResourcePatternResolver) new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resolver);
        List<String> allResult = new ArrayList<String>();
        try
        {
            for (String aliasesPackage : typeAliasesPackage.split(","))
            {
                List<String> result = new ArrayList<String>();
                aliasesPackage = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                        + ClassUtils.convertClassNameToResourcePath(aliasesPackage.trim()) + "/" + DEFAULT_RESOURCE_PATTERN;
                Resource[] resources = resolver.getResources(aliasesPackage);
                if (resources != null && resources.length > 0)
                {
                    MetadataReader metadataReader = null;
                    for (Resource resource : resources)
                    {
                        if (resource.isReadable())
                        {
                            metadataReader = metadataReaderFactory.getMetadataReader(resource);
                            try
                            {
                                result.add(Class.forName(metadataReader.getClassMetadata().getClassName()).getPackage().getName());
                            }
                            catch (ClassNotFoundException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if (result.size() > 0)
                {
                    HashSet<String> hashResult = new HashSet<String>(result);
                    allResult.addAll(hashResult);
                }
            }
            if (allResult.size() > 0)
            {
                typeAliasesPackage = String.join(",", (String[]) allResult.toArray(new String[0]));
            }
            else
            {
                throw new RuntimeException("mybatis typeAliasesPackage 路径扫描错误,参数typeAliasesPackage:" + typeAliasesPackage + "未找到任何包");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return typeAliasesPackage;
    }

    public Resource[] resolveMapperLocations(String[] mapperLocations)
    {
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        List<Resource> resources = new ArrayList<Resource>();
        if (mapperLocations != null)
        {
            for (String mapperLocation : mapperLocations)
            {
                try
                {
                    Resource[] mappers = resourceResolver.getResources(mapperLocation);
                    resources.addAll(Arrays.asList(mappers));
                }
                catch (IOException e)
                {
                    // ignore
                }
            }
        }
        return resources.toArray(new Resource[resources.size()]);
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource, DatabaseIdProvider databaseIdProvider) throws Exception
    {

//        String typeAliasesPackage= env.getProperty("mybatis.typeAliasesPackage");
//        String configLocation= env.getProperty("mybatis.configLocation");
//        String mapperLocations= env.getProperty("mybatis.mapperLocations");
        String typeAliasesPackage="com.deer.wcs.**.model";
        String configLocation= "classpath:mybatis/mybatis-config.xml";
        String mapperLocations= "classpath*:com/deer/**/mapper/*Mapper.xml";


        typeAliasesPackage = setTypeAliasesPackage(typeAliasesPackage);
        VFS.addImplClass(SpringBootVFS.class);

        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setTypeAliasesPackage(typeAliasesPackage);
        sessionFactory.setMapperLocations(resolveMapperLocations(StringUtils.split(mapperLocations, ",")));
        sessionFactory.setConfigLocation(new DefaultResourceLoader().getResource(configLocation));
        sessionFactory.setDatabaseIdProvider(databaseIdProvider);
        return sessionFactory.getObject();
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        mapperScannerConfigurer.setBasePackage(getBasePackage());

        //配置通用Mapper，详情请查阅官方文档
        Properties properties = new Properties();
        properties.setProperty("mappers", BaseMapper.class.getName());
        properties.setProperty("notEmpty", "false");//insert、update是否判断字符串类型!='' 即 test="str != null"表达式内是否追加 and str != ''
        properties.setProperty("IDENTITY", "MYSQL");
        mapperScannerConfigurer.setProperties(properties);

        return mapperScannerConfigurer;
    }



    public String getBasePackage(){
        return   "com.deer.wcs.common.dao"
                + ", " + "com.deer.wcs.framework.dao"
                + ", " + "com.deer.wcs.generator.dao"
                + ", " + "com.deer.wcs.quartz.dao"
                + ", " + "com.deer.wcs.system.dao"
                + ", " + "com.deer.wcs.base.dao"
                + ", " + "com.deer.wcs.task.dao"
                + ", " + "com.deer.wcs.report.dao"
                + ", " + "com.deer.wcs.ds.dao"
                + ", " + "com.deer.wcs.lg1.dao"
                + ", " + "com.deer.wcs.rcs.dao"
                + ", " + "com.deer.wcs.xl.dao"
                + ", " + "com.deer.wcs.api.dao"
                + ", " + "com.deer.wcs.ds_xw.dao"
                ;
    };






}