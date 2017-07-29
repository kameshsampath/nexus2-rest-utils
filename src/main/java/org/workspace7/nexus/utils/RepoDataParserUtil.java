package org.workspace7.nexus.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.workspace7.nexus.data.ProxyRepoItem;
import org.workspace7.nexus.data.RepoItem;
import org.workspace7.nexus.data.Repositories;
import org.workspace7.nexus.data.Repository;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

@Service
@Slf4j
public class RepoDataParserUtil {


    private final ObjectMapper yamlObjectMapper;
    private final ObjectMapper jsonObjectMapper;
    private final ObjectMapper xmlMapper;

    public RepoDataParserUtil() {
        this.yamlObjectMapper = new ObjectMapper(new YAMLFactory());
        this.jsonObjectMapper = new ObjectMapper()
                .configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.xmlMapper = new XmlMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<RepoItem> loadRepos(InputStream yaml) throws Exception {

        log.info("Loading Repositories from YAML Stream");

        return yamlObjectMapper.readValue(yaml, new TypeReference<List<RepoItem>>() {
        });
    }

    public Flux<String> jsonProxyRepoItem(RepoItem repoItem) {
        String repoItemJson;
        try {
            ProxyRepoItem proxyRepoItem = ProxyRepoItem.adapt(repoItem);
            StringWriter stringWriter = new StringWriter();
            jsonObjectMapper.writer()
                    .withRootName("data")
                    .forType(ProxyRepoItem.class)
                    .writeValue(stringWriter, proxyRepoItem);

            stringWriter.flush();
            repoItemJson = stringWriter.toString();
            stringWriter.close();
        } catch (IOException e) {
            return Flux.error(e);
        }
        return Flux.just(repoItemJson);
    }

}
