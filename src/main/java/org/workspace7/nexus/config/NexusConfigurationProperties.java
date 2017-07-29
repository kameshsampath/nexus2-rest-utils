package org.workspace7.nexus.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "nexus")
@Data
@Slf4j
public class NexusConfigurationProperties {

    private String userName;
    private String password;
    private String url;
    private String[] repositoryResourcePaths;

}
