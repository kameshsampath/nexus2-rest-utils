package org.workspace7.nexus;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.workspace7.nexus.data.*;
import org.workspace7.nexus.utils.RepoDataParserUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ObjectMapperTests {

    final RepoDataParserUtil repoDataParserUtil = new RepoDataParserUtil();

    @Test
    public void testDeserialize() throws Exception {

        ObjectMapper xmlMapper = new XmlMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        InputStream xml = this.getClass().getResourceAsStream("/sample-repo.xml");

        Repositories repositories = xmlMapper.readValue(xml, Repositories.class);

        System.out.println(repositories);

        assertThat(repositories).isNotNull();
        assertThat(repositories.getRepoItems()).isNotNull();
        assertThat(repositories.getRepoItems().size()).isGreaterThan(1);
    }

    @Test
    public void testSerializeProxyRepo() throws Exception {

        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        InputStream in = this.getClass().getResourceAsStream("/nexus-proxy-repos.yaml");

        List<RepoItem> repoItems = repoDataParserUtil.loadRepos(in);

        Assertions.assertThat(repoItems).isNotNull();
        Assertions.assertThat(repoItems.size()).isEqualTo(2);

        repoItems.stream().forEach(repoItem -> {
            try {
                StringWriter stringWriter = new StringWriter();
                ProxyRepoItem proxyRepoItem = ProxyRepoItem.adapt(repoItem);
                jsonMapper.writer()
                        .withRootName("data")
                        .forType(ProxyRepoItem.class)
                        .writeValue(stringWriter, proxyRepoItem);
                System.out.println(stringWriter.toString());
            } catch (IOException e) {
            }
        });

    }

    @Test
    public void testSerializeRepoGroup() throws Exception {

        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        RepoGroup repoGroup = RepoGroup.builder().build();

        RepoGroupItem repoGroupItem = RepoGroupItem.builder()
                .id("public")
                .name("Public Repositories")
                .exposed(true)
                .build();
        repoGroup.setRepoGroupItem(repoGroupItem);

        StringWriter stringWriter = new StringWriter();

        jsonMapper.writeValue(stringWriter, repoGroup);

        stringWriter.flush();

        System.out.println(stringWriter.toString());

        stringWriter.close();
    }
}
