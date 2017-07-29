package org.workspace7.nexus;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.workspace7.nexus.config.NexusRepoHelperConfiguration;
import org.workspace7.nexus.data.RepoItem;
import org.workspace7.nexus.utils.RepoDataParserUtil;

import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NexusRepoHelperConfiguration.class)
public class LoadNexusReposYaml {

    @Autowired
    RepoDataParserUtil repoDataParserUtil;

    @Test
    public void testLoadYaml() throws Exception {

        InputStream in = this.getClass().getResourceAsStream("/nexus-proxy-repos.yaml");

        List<RepoItem> repoItems = repoDataParserUtil.loadRepos(in);

        assertThat(repoItems).isNotNull();
        assertThat(repoItems.size()).isEqualTo(2);
    }
}
