package com.nexusgesti;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.nexusgesti.boulangerie"
})
public class BoulangerieApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoulangerieApplication.class, args);
    }
}
