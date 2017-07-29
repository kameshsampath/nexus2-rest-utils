package org.workspace7.nexus.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;


@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepoGroupRepository {

    @NonNull
    private String id;
    @NonNull
    private String name;
    private String resourceURI;
}
