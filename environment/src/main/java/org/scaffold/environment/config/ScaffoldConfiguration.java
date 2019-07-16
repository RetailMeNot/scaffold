package org.scaffold.environment.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * A simple configuration file that scans for components under the org.scaffold package structure and enables
 * the usage of DesiredCapabilitiesConfigurationProperties as an auto configuration for implementing projects.
 */
@Configuration
@SpringBootApplication
@ComponentScan(value = "org.scaffold")
@EnableConfigurationProperties(DesiredCapabilitiesConfigurationProperties.class)
public class ScaffoldConfiguration {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
