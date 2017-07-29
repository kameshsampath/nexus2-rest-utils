package org.workspace7.nexus.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.workspace7.nexus.utils.RepoDataParserUtil;


@Configuration
@EnableConfigurationProperties(NexusConfigurationProperties.class)
public class NexusRepoHelperConfiguration {

    @Autowired
    NexusConfigurationProperties nexusConfigurationProperties;


    @Bean
    public RepoDataParserUtil repoDataParserUtil() {
        return new RepoDataParserUtil();
    }
}
