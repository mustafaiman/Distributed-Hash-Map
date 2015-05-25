package com.mustafa.distributed.network;

import java.io.Serializable;

/**
 * Created by Mustafa on 23.5.2015.
 */
public class RequestMessage implements Serializable {

    public enum MSG {
        EXCHANGE_CONNECTION_PORTS,
        PROVIDE_CONNECTION_PORTS,
        LEAVE_NETWORK;
    }

    public MSG msg;

    public Serializable data;
    public String identifier;
}
