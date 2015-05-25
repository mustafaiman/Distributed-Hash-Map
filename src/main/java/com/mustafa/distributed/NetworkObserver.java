package com.mustafa.distributed;

/**
 * Created by Mustafa on 25.5.2015.
 */
public interface NetworkObserver {
    public void onMessage(ObjectSocket socket, ApplicationMessage message);
}
