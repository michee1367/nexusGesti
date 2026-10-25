package com.nexusgesti;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = {
        "com.nexusgesti.tenant",
        "com.nexusgesti.identity",
        "com.nexusgesti.auth",
        "com.nexusgesti.notification",
        "com.nexusgesti.audit",
        "com.nexusgesti.reporting",
        "com.nexusgesti.socle"
})
@ConfigurationPropertiesScan(basePackages = {
        "com.nexusgesti.auth.config",
        "com.nexusgesti.identity.config"
})
public class SocleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocleApplication.class, args);
    }
}
