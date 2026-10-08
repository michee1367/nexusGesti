package com.nexusgesti.identity.config;

import com.nexusgesti.common.tenancy.TenantDataSourceBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaConfig {

    @Bean
    public static TenantDataSourceBeanPostProcessor tenantDataSourceBeanPostProcessor() {
        return new TenantDataSourceBeanPostProcessor();
    }
}
