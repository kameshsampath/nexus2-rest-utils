package org.workspace7.nexus.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.auth.BasicAuthRequestInterceptor;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.workspace7.nexus.config.NexusConfigurationProperties;
import org.workspace7.nexus.data.RepoItem;
import org.workspace7.nexus.data.Repositories;
import org.workspace7.nexus.data.Repository;
import org.workspace7.nexus.utils.RepoDataParserUtil;
import reactor.core.publisher.Flux;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

@Service
@Slf4j
public class NexusRestService {

    private final RepoDataParserUtil repoDataParserUtil;

    private final NexusConfigurationProperties nexusConfigurationProperties;

    @Autowired
    public NexusRestService(RepoDataParserUtil repoDataParserUtil,
                            NexusConfigurationProperties nexusConfigurationProperties) {
        this.repoDataParserUtil = repoDataParserUtil;
        this.nexusConfigurationProperties = nexusConfigurationProperties;
    }

    /**
     * @param repoItem
     */
    public void addRepository(RepoItem repoItem) {
        //TODO not yet implemented
    }


    /**
     * @return
     */
    public Repositories listAllRepositories() {
        return buildNexusRestClient().listRepos();
    }

    /**
     * @param repoItem
     * @return
     */
    public boolean checkIfRepoExists(RepoItem repoItem) {

        try {
            final Repository repository =
                    buildNexusRestClient()
                            .getRepo(repoItem.getId());

            boolean isExists = repository != null
                    && repository.getRepoItem().getId().equals(repoItem.getId());

            log.info("Checking Repo {} already exists ?  {}", repoItem.getId(), isExists ? "YES" : "NO");

            return isExists;
        } catch (Exception e) {
            if (e.getCause() instanceof NexusClientException) {
                NexusClientException nexusClientException = (NexusClientException) e.getCause();
                if (404 == nexusClientException.getStatus()) {
                    return false;
                } else {
                    throw e;
                }
            }
            throw e;
        }
    }


    /**
     * FIXME - identify the type of repo(s) via resource path
     *
     * @throws Exception
     */
    public void addRepositories() throws Exception {
        NexusRestClient nexusRestClient = Feign.builder()
                .requestInterceptor(new BasicAuthRequestInterceptor(nexusConfigurationProperties.getUserName(),
                        nexusConfigurationProperties.getPassword()))
                .target(NexusRestClient.class, nexusConfigurationProperties.getUrl());

        String[] resourcePaths = nexusConfigurationProperties.getRepositoryResourcePaths();

        for (String repoResourcePath : resourcePaths) {

            log.info("Adding Repositories from Resource : {}", repoResourcePath);

            InputStream yaml = new FileInputStream(ResourceUtils.getFile(repoResourcePath));

            List<RepoItem> repoItems = repoDataParserUtil.loadRepos(yaml);

            yaml.close();

            Flux<RepoItem> fluxRepoItems = Flux.fromIterable(repoItems);

            fluxRepoItems
                    .filter(repoItem -> !checkIfRepoExists(repoItem))
                    .flatMap(repoDataParserUtil::jsonProxyRepoItem)
                    .subscribe(s -> {
                                log.info("Adding Repo {}", s);
                                nexusRestClient.addRepo(s);
                            },
                            err -> log.error("Error adding item", err));
        }

    }

    /**
     * @return
     */
    private NexusRestClient buildNexusRestClient() {
        ObjectMapper jsonMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return Feign.builder()
                .requestInterceptor(new BasicAuthRequestInterceptor(nexusConfigurationProperties.getUserName(),
                        nexusConfigurationProperties.getPassword()))
                .decoder(new JacksonDecoder(jsonMapper))
                .encoder(new JacksonEncoder(jsonMapper))
                .errorDecoder((methodKey, response) -> {
                    if (response.status() >= 400 && response.status() <= 499) {
                        return new NexusClientException(
                                response.status(),
                                response.reason()
                        );
                    }
                    if (response.status() >= 500 && response.status() <= 599) {
                        return new NexusClientException(
                                response.status(),
                                response.reason()
                        );
                    }
                    return new Exception(response.reason());
                })
                .target(NexusRestClient.class, nexusConfigurationProperties.getUrl());
    }
}
