package org.workspace7.nexus;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.auth.BasicAuthRequestInterceptor;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.workspace7.nexus.client.NexusRestClient;
import org.workspace7.nexus.config.NexusConfiguration;
import org.workspace7.nexus.config.NexusConfigurationProperties;
import org.workspace7.nexus.config.NexusRepoHelperConfiguration;
import org.workspace7.nexus.data.*;
import org.workspace7.nexus.utils.RepoDataParserUtil;
import reactor.core.publisher.Flux;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {NexusConfiguration.class,
        NexusRepoHelperConfiguration.class}, properties = {"nexus.url=http://localhost:8081",
        "nexus.username=admin",
        "nexus.password=admin123"})
@Slf4j
public class RepoHelperTest {

    @Autowired
    RepoDataParserUtil repoDataParserUtil;

    @Autowired
    NexusConfigurationProperties nexusConfigurationProperties;

    @Test
    public void testNexusConfigurationProperties() {

        String userName = nexusConfigurationProperties.getUserName();
        String password = nexusConfigurationProperties.getPassword();
        String remoteUrl = nexusConfigurationProperties.getUrl();


        assertThat(userName).isNotNull();
        assertThat(userName).isEqualTo("admin");
        assertThat(password).isNotNull();
        assertThat(password).isEqualTo("admin123");
        assertThat(remoteUrl).isNotNull();
        assertThat(remoteUrl).isEqualTo("localhost:8081");
    }


    @Test
    public void testListRepos() {

        ObjectMapper jsonMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        NexusRestClient nexusRestClient = Feign.builder()
                .decoder(new JacksonDecoder(jsonMapper))
                .encoder(new JacksonEncoder(jsonMapper))
                .target(NexusRestClient.class, nexusConfigurationProperties.getUrl());

        Repositories repos = nexusRestClient.listRepos();

        System.out.println(repos);

        assertThat(repos).isNotNull();
        assertThat(repos.getRepoItems().size()).isGreaterThan(1);
    }

    @Test
    public void testAddRepo() throws Exception {

        NexusRestClient nexusRestClient = Feign.builder()
                .requestInterceptor(new BasicAuthRequestInterceptor(nexusConfigurationProperties.getUserName(),
                        nexusConfigurationProperties.getPassword()))
                .target(NexusRestClient.class, nexusConfigurationProperties.getUrl());

        final InputStream yaml = this.getClass().getResourceAsStream("/nexus-proxy-repos.yaml");

        List<RepoItem> repoItems = repoDataParserUtil.loadRepos(yaml);

        yaml.close();

        Flux<RepoItem> fluxRepoItems = Flux.fromIterable(repoItems);

        fluxRepoItems
                .flatMap(repoDataParserUtil::jsonProxyRepoItem)
                .subscribe(s -> {
                            log.info("Adding Repo {}", s);
                            nexusRestClient.addRepo(s);
                        },
                        err -> log.error("Error adding item", err));

    }

    @Test
    public void testCheckRepoExists() throws Exception {

        String repoId = "jboss-public";

        ObjectMapper jsonMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        NexusRestClient nexusRestClient = Feign.builder()
                .decoder(new JacksonDecoder(jsonMapper))
                .encoder(new JacksonEncoder(jsonMapper))
                .requestInterceptor(new BasicAuthRequestInterceptor(nexusConfigurationProperties.getUserName(),
                        nexusConfigurationProperties.getPassword()))
                .target(NexusRestClient.class, nexusConfigurationProperties.getUrl());

        Repository repository = nexusRestClient.getRepo(repoId);

        assertThat(repository.getRepoItem()).isNotNull();
        assertThat(repository.getRepoItem().getId()).isEqualTo(repoId);

    }

    @Test
    public void testCheckRepoGroups() throws Exception {

        ObjectMapper jsonMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        NexusRestClient nexusRestClient = Feign.builder()
                .decoder(new JacksonDecoder(jsonMapper))
                .encoder(new JacksonEncoder(jsonMapper))
                .requestInterceptor(new BasicAuthRequestInterceptor(nexusConfigurationProperties.getUserName(),
                        nexusConfigurationProperties.getPassword()))
                .target(NexusRestClient.class, nexusConfigurationProperties.getUrl());

        RepoGroups repoGroups = nexusRestClient.getRepoGroups();

        System.out.printf("Repo Groups:%s\n", repoGroups);

        assertThat(repoGroups).isNotNull();
        assertThat(repoGroups.getRepoItems()).isNotNull();
        assertThat(repoGroups.getRepoItems().size()).isEqualTo(1);

    }

    @Test
    public void testUpdateRepoGroup() throws Exception {

        String repoGroupId = "public";

        ObjectMapper jsonMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        NexusRestClient nexusRestClient = Feign.builder()
                .decoder(new JacksonDecoder(jsonMapper))
                .encoder(new JacksonEncoder(jsonMapper))
                .requestInterceptor(new BasicAuthRequestInterceptor(nexusConfigurationProperties.getUserName(),
                        nexusConfigurationProperties.getPassword()))
                .target(NexusRestClient.class, nexusConfigurationProperties.getUrl());

        RepoGroup repoGroup = nexusRestClient.getRepoGroup(repoGroupId);

        System.out.printf("Repo Group:%s\n", repoGroup);

        assertThat(repoGroup).isNotNull();
        assertThat(repoGroup.getRepoGroupItem().getId()).isEqualTo(repoGroupId);
        assertThat(repoGroup.getRepoGroupItem().getRepoType()).isEqualTo("group");
        assertThat(repoGroup.getRepoGroupItem().getName()).isEqualTo("Public Repositories");
        assertThat(repoGroup.getRepoGroupItem().getRepositories()).isNotNull();
        assertThat(repoGroup.getRepoGroupItem().getRepositories().size()).isGreaterThan(3);

        repoGroup
                .getRepoGroupItem()
                .getRepositories()
                .add(RepoGroupRepository
                        .builder()
                        .id("fusesource-ea")
                        .name("Fusesource EA")
                        .build());


        RepoGroup updateRepoGroup = nexusRestClient.updateRepoGroup(repoGroupId, repoGroup);


        assertThat(updateRepoGroup.getRepoGroupItem().getRepositories().size()).isEqualTo(4);

        StringWriter stringWriter = new StringWriter();
        jsonMapper.writeValue(stringWriter, updateRepoGroup);
        stringWriter.flush();
        System.out.printf("Updated Repo Group:%s\n", stringWriter.toString());

        stringWriter.close();
    }
}
