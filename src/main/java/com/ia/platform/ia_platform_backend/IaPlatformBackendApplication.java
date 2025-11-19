// IaPlatformBackendApplication.java
package com.ia.platform.ia_platform_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.ia.platform.ia_platform_backend.repository")
@EntityScan(basePackages = "com.ia.platform.ia_platform_backend.entity")
public class IaPlatformBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(IaPlatformBackendApplication.class, args);
    }
}
