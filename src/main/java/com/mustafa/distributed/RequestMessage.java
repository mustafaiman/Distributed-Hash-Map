package com.mustafa.distributed;

import java.io.Serializable;

/**
 * Created by Mustafa on 23.5.2015.
 */
public enum RequestMessage implements Serializable {
    EXCHANGE_CONNECTION_PORTS,
    PROVIDE_CONNECTION_PORTS,
    LEAVE_NETWORK;

    public Serializable data;
    public String identifier;
}
