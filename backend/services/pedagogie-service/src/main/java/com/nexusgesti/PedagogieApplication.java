package com.nexusgesti;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.nexusgesti.structure",
        "com.nexusgesti.people",
        "com.nexusgesti.enrollment",
        "com.nexusgesti.attendance",
        "com.nexusgesti.assessment",
        "com.nexusgesti.exam",
        "com.nexusgesti.fees",
        "com.nexusgesti.pedagogie"
})
public class PedagogieApplication {

    public static void main(String[] args) {
        SpringApplication.run(PedagogieApplication.class, args);
    }
}
