package org.workspace7.nexus.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RepoGroups {

    @JsonProperty(value = "data")
    private List<RepoItem> repoItems;

}
