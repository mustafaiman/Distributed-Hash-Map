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

    public void testOtherMapsShouldGetTheSameValue() {
        DistributedHashMap<String, String> peer1 = new DistributedHashMap<>();
        DistributedHashMap<String, String> peer2 = new DistributedHashMap<>();
        DistributedHashMap<String, String> peer3 = new DistributedHashMap<>();
        peer2.connect(peer1.getHost(),peer1.getPort());
        peer3.connect(peer2.getHost(), peer2.getPort());

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        peer2.put("Key","Value");
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String ret1=peer1.get("Key");
        String ret2=peer2.get("Key");
        String ret3=peer3.get("Key");
        assertEquals("Value",ret1);
        assertEquals("Value",ret2);
        assertEquals("Value",ret3);
    }

    public void testOtherMapsShouldReturnNullForRemovecEndtry() {
        DistributedHashMap<String, String> peer1 = new DistributedHashMap<>();
        DistributedHashMap<String, String> peer2 = new DistributedHashMap<>();
        DistributedHashMap<String, String> peer3 = new DistributedHashMap<>();
        peer2.connect(peer1.getHost(),peer1.getPort());
        peer3.connect(peer2.getHost(),peer2.getPort());

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        peer2.put("Key","Value");
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        peer3.remove("Key");
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String ret1=peer1.get("Key");
        String ret2=peer2.get("Key");
        String ret3=peer3.get("Key");

        assertNull(ret1);
        assertNull(ret2);
        assertNull(ret3);
    }
}