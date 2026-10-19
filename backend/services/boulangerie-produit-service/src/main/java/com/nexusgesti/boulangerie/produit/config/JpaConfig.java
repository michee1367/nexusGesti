package com.nexusgesti.boulangerie.produit.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EntityScan(basePackages = "com.nexusgesti.boulangerie.produit.adapter.out.persistence.entity")
@EnableJpaRepositories(basePackages = "com.nexusgesti.boulangerie.produit.adapter.out.persistence")
public class JpaConfig {
}
