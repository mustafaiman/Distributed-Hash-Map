package com.mustafa.distributed;

import java.io.Serializable;

/**
 * Created by Mustafa on 25.5.2015.
 */
public class ApplicationMessage implements Serializable {
    public enum MSG {
        PUT,
        GET,
        REMOVE;
    }

    public MSG msg;
    public Serializable tag;
    public Serializable value;
    public String identifier;
}
