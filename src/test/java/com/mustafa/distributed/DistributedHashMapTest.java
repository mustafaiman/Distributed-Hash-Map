package com.mustafa.distributed;

import junit.framework.TestCase;

public class DistributedHashMapTest extends TestCase {
    /**
     * Rigourous Test :-)
     */
    public void testMapShouldContainWhatWasInsertedBefore()
    {
        DistributedHashMap<String, String> dhm = new DistributedHashMap<>();
        dhm.put("Kamil","Iman");
        assertTrue(dhm.get("Kamil").equals("Iman"));
    }
}