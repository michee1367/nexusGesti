package com.nexusgesti.common.config;

import com.github.fppt.jedismock.RedisServer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.net.ServerSocket;

@Configuration
@ConditionalOnProperty(name = "nexusgesti.security.e2e-test-mode", havingValue = "true")
public class E2eTestRedisConfig {

    @Bean(destroyMethod = "stop")
    RedisServer e2eRedisServer() throws IOException {
        RedisServer server = RedisServer.newRedisServer(freePort());
        server.start();
        return server;
    }

    @Bean
    @Primary
    RedisConnectionFactory e2eRedisConnectionFactory(RedisServer e2eRedisServer) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(
                "127.0.0.1", e2eRedisServer.getBindPort());
        LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
        factory.afterPropertiesSet();
        return factory;
    }

    @Bean
    @Primary
    StringRedisTemplate e2eStringRedisTemplate(RedisConnectionFactory e2eRedisConnectionFactory) {
        return new StringRedisTemplate(e2eRedisConnectionFactory);
    }

    @Bean
    @Primary
    RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory e2eRedisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(e2eRedisConnectionFactory);
        template.afterPropertiesSet();
        return template;
    }

    private static int freePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to allocate free Redis port", e);
        }
    }
}
