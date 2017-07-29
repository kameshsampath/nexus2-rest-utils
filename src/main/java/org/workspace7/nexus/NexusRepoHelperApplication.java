package org.workspace7.nexus;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.workspace7.nexus.client.NexusRestService;

@SpringBootApplication
@Slf4j
public class NexusRepoHelperApplication implements CommandLineRunner {

    @Autowired
    NexusRestService nexusRestService;

    public static void main(String[] args) {

        SpringApplication.run(NexusRepoHelperApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        log.info("Starting ...");
        nexusRestService.addRepositories();
    }

}
