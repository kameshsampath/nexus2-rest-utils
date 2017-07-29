package org.workspace7.nexus.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonRootName(value = "repository")
public class Repository {

    @JsonProperty(value = "data")
    private RepoItem repoItem;
}
