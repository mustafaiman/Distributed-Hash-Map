package com.mustafa.distributed;

import java.util.concurrent.ConcurrentHashMap;


public class DistributedHashMap<K,V> extends ConcurrentHashMap<K,V>
{
    NetworkMember member;

}
