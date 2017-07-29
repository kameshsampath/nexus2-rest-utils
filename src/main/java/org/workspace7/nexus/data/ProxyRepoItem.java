package org.workspace7.nexus.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
@JsonRootName(value = "data")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProxyRepoItem {

    @NonNull
    private String id;
    @Builder.Default
    private String format = "maven2";
    @Builder.Default
    private String provider = "maven2";
    @NonNull
    private String name;
    @Builder.Default
    private String repoType = "proxy";
    private String repoPolicy;
    @Builder.Default
    private boolean exposed = true;
    @Builder.Default
    private boolean browseable = true;
    @Builder.Default
    private boolean downloadRemoteIndexes = true;
    @Builder.Default
    private boolean indexable = true;
    @Builder.Default
    private String checksumPolicy = "WARN";
    @Builder.Default
    private String providerRole = "org.sonatype.nexus.proxy.repository.Repository";
    @NonNull
    private RemoteStorage remoteStorage;

    public static ProxyRepoItem adapt(RepoItem repoItem) {

        ProxyRepoItem proxyRepoItem = ProxyRepoItem.builder()
                .id(repoItem.getId())
                .name(repoItem.getName())
                .repoPolicy(repoItem.getRepoPolicy())
                .remoteStorage(RemoteStorage.builder().remoteStorageUrl(repoItem.getRemoteUri()).build())
                .build();

        return proxyRepoItem;
    }


    @Data
    @Builder
    private static final class RemoteStorage {
        private String remoteStorageUrl;
    }

}
