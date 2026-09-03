package com.example.portalaluno.shared;

import com.asaas.apisdk.AsaasSdk;
import com.asaas.apisdk.config.ApiKeyAuthConfig;
import com.asaas.apisdk.config.AsaasSdkConfig;
import com.asaas.apisdk.http.Environment;
import com.asaas.apisdk.services.CustomerService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

@Configuration
public class AsaasConfig {

    @Value("${ASAAS_API_KEY}")
    private String asaasApiKey;

    @Bean
    public AsaasSdk  asaasSdk() {
        ApiKeyAuthConfig authConfig = ApiKeyAuthConfig.builder()
                .apiKey(asaasApiKey)
                .apiKeyHeader("access_token")
                .build();

        AsaasSdkConfig config = AsaasSdkConfig.builder()
                .apiKeyAuthConfig(authConfig)
                .build();

        AsaasSdk asaasSdk = new AsaasSdk(config);
        asaasSdk.setEnvironment(Environment.SANDBOX);

        return asaasSdk;
    }

    @Bean
    public CustomerService customerService(AsaasSdk asaasSdk) {
        return asaasSdk.customer;
    }
}
