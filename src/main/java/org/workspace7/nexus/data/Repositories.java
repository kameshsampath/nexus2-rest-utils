package org.workspace7.nexus.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class Repositories {

    @JsonProperty(value = "data")
    private List<RepoItem> repoItems;
}
