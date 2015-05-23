package com.mustafa.distributed;

import java.io.Serializable;

/**
 * Created by Mustafa on 23.5.2015.
 */
public enum RequestMessage implements Serializable {
    GET_PEERS_LIST,
    PEERS_LIST,
    LEAVE_NETWORK;

    public Serializable data;
}
