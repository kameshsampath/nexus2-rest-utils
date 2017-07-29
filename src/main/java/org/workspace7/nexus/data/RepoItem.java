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
public class RepoItem {

    @NonNull
    private String id;
    @Builder.Default
    private String format = "maven2";
    @Builder.Default
    private String provider = "maven2";
    @NonNull
    private String name;
    @NonNull
    private String repoType;
    @NonNull
    @Builder.Default
    private String repoPolicy = "RELEASE";
    @NonNull
    private String remoteUri;
    @Builder.Default
    private boolean exposed = true;
    private String resourceURI;
    private String contentResourceURI;
}
