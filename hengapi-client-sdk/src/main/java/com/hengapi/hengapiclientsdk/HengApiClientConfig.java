package com.hengapi.hengapiclientsdk;

import com.hengapi.hengapiclientsdk.client.HengApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("hengapi.client")
@Data
@ComponentScan
public class HengApiClientConfig {

    private String accessKey;
    private String secretKey;

    @Bean
    public HengApiClient hengApiClient(){
        return new HengApiClient(accessKey,secretKey);
    }
}
