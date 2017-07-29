package org.workspace7.nexus.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepoGroupItem {
    @NonNull
    private String id;
    @NonNull
    @Builder.Default
    private String format = "maven2";
    @NonNull
    @Builder.Default
    private String provider = "maven2";
    @NonNull
    private String name;
    @NonNull
    @Builder.Default
    private String repoType = "group";
    @Builder.Default
    private boolean exposed = true;

    @JsonProperty(value = "repositories")
    @Builder.Default
    private List<RepoGroupRepository> repositories = new ArrayList<>();
}
