package com.nexusgesti.common.config;

import com.nexusgesti.common.tenancy.TenantDataSourceBeanPostProcessor;
import com.nexusgesti.common.tenancy.TenantFilter;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@AutoConfiguration
@EnableMethodSecurity
@ComponentScan(basePackages = "com.nexusgesti.common")
@Import({OAuth2ResourceServerConfig.class, RedisConfig.class})
public class NexusGestiAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SecurityExpressionService securityExpressionService() {
        return new SecurityExpressionService();
    }

    @Bean
    @ConditionalOnMissingBean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

    // @ConditionalOnMissingBean sans type se base sur le type de retour
    // (BeanPostProcessor) et le declencheur trouve des BPP d infrastructure :
    // le bean n etait jamais enregistre et le DataSource n etait jamais enveloppe.
    // Type explicite obligatoire ici. static : un BPP non-statique force la
    // creation anticipée de la classe de configuration.
    @Bean
    @ConditionalOnMissingBean(TenantDataSourceBeanPostProcessor.class)
    public static BeanPostProcessor tenantDataSourceBeanPostProcessor() {
        return new TenantDataSourceBeanPostProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public TenantFilter tenantFilter() {
        return new TenantFilter();
    }
}
