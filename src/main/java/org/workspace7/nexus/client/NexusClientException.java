package org.workspace7.nexus.client;

import lombok.Data;

@Data
public class NexusClientException extends Exception {
    private int status;

    public NexusClientException(int status, String reason) {
        super(reason);
        this.status = status;
    }
}
