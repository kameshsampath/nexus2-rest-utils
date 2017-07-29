package org.workspace7.nexus.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class RepoGroup {

    @JsonProperty(value = "data")
    private RepoGroupItem repoGroupItem;
}
