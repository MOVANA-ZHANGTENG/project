package com.deer.wcs.task.handle.hik;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;

/**
 * 解决HTTPS自签名证书验证的RestTemplate配置（单例复用）
 */
@Configuration
public class RestTemplateConfig {

    /**
     * 忽略SSL证书验证的RestTemplate（仅内网/测试环境使用）
     * 生产环境建议导入证书到Java信任库，而非禁用验证
     */
    @Bean("sslIgnoreRestTemplate")
    public RestTemplate sslIgnoreRestTemplate() throws Exception {
        // 信任所有证书
        SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(null, (X509Certificate[] chain, String authType) -> true)
                .build();

        // 忽略主机名验证
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
                sslContext, NoopHostnameVerifier.INSTANCE);

        // 构建HttpClient，配置超时
        HttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(socketFactory)
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setConnectTimeout(5000);  // 连接超时5秒
        factory.setReadTimeout(10000);    // 读取超时10秒

        return new RestTemplate(factory);
    }
}