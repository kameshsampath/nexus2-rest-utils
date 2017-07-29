package org.workspace7.nexus.config;

import feign.Contract;
import feign.auth.BasicAuthRequestInterceptor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(NexusConfigurationProperties.class)
@Data
@Slf4j
public class NexusConfiguration {

    @Autowired
    private NexusConfigurationProperties nexusConfigurationProperties;

    @Bean
    public Contract feignContract() {
        log.info("FEIGN Contract");
        return new feign.Contract.Default();
    }

    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        log.info("Nexus User:{} and Password:{}", nexusConfigurationProperties.getUserName()
                , nexusConfigurationProperties.getPassword());
        return new BasicAuthRequestInterceptor(nexusConfigurationProperties.getUserName()
                , nexusConfigurationProperties.getPassword());
    }
}
