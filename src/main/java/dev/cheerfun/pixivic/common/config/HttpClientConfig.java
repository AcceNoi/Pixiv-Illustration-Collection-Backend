package dev.cheerfun.pixivic.common.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/01 9:25
 * @description HttpClientConfig
 */
@Configuration
public class HttpClientConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
    @Bean
    public TrustManager[] trustAllCertificates() {
        return new TrustManager[]{new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null; // Not relevant.
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
                // TODO Auto-generated method stub
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
                // TODO Auto-generated method stub
            }
        }};
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(12);
    }

    @Bean
    @Primary
    @Autowired
    public HttpClient httpClientWithOutProxy(TrustManager[] trustAllCertificates, ExecutorService executorService) throws NoSuchAlgorithmException, KeyManagementException {
        SSLParameters sslParams = new SSLParameters();
        sslParams.setEndpointIdentificationAlgorithm("");
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCertificates, new SecureRandom());
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .sslParameters(sslParams)
                .sslContext(sc)
                .connectTimeout(Duration.ofMinutes(5))
               //        .proxy(ProxySelector.of(new InetSocketAddress("127.0.0.1", 8888)))
                .executor(executorService)
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
    }

    @Autowired
    @Bean(name = "httpClientWithProxy")
    public HttpClient httpClient(TrustManager[] trustAllCertificates, ExecutorService executorService) throws NoSuchAlgorithmException, KeyManagementException {
        SSLParameters sslParams = new SSLParameters();
        sslParams.setEndpointIdentificationAlgorithm("");
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCertificates, new SecureRandom());
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .sslParameters(sslParams)
                .sslContext(sc)
                .proxy(ProxySelector.of(new InetSocketAddress("127.0.0.1", 9999)))
                .executor(executorService)
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
    }

}
