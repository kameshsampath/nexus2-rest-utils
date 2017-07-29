package org.workspace7.nexus.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.workspace7.nexus.data.*;

import java.util.List;

public interface NexusRestClient {

    @RequestLine("POST /nexus/service/local/repositories")
    @Headers("Content-Type: application/json")
    void addRepo(String newRepoJson);

    @RequestLine("GET /nexus/service/local/repositories")
    @Headers("Accept: application/json")
    Repositories listRepos();


    @RequestLine("GET /nexus/service/local/repositories/{repoId}")
    @Headers("Accept: application/json")
    Repository getRepo(@Param("repoId") String repoId);

    @RequestLine("GET /nexus/service/local/repo_groups")
    @Headers("Accept: application/json")
    RepoGroups getRepoGroups();

    @RequestLine("GET /nexus/service/local/repo_groups/{groupId}")
    @Headers("Accept: application/json")
    RepoGroup getRepoGroup(@Param("groupId") String groupId);

    @RequestLine("PUT /nexus/service/local/repo_groups/{groupId}")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    RepoGroup updateRepoGroup(@Param("groupId") String groupId, RepoGroup repoGroup);

}
