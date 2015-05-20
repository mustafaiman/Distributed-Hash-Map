package com.mustafa.distributed;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class DistributedHashMapTest
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DistributedHashMapTest(String testName)
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( DistributedHashMapTest.class );
    }

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
