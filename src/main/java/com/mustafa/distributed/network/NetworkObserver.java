package com.mustafa.distributed.network;

import com.mustafa.distributed.distributedhashmap.ApplicationMessage;

/**
 * Created by Mustafa on 25.5.2015.
 */
public interface NetworkObserver {
    public void onMessage(ObjectSocket socket, ApplicationMessage message);
}
