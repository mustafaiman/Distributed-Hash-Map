package com.mustafa.distributed;

import java.io.Serializable;

/**
 * Created by Mustafa on 25.5.2015.
 */
public enum ApplicationMessage {
    PUT,GET,REMOVE;

    public Serializable tag;
    public Serializable value;
    public String identifier;
}
