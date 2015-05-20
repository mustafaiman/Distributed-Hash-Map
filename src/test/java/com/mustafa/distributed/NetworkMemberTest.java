package com.mustafa.distributed;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Arrays;

/**
 * Unit test for simple App.
 */
public class NetworkMemberTest
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public NetworkMemberTest(String testName)
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( NetworkMemberTest.class );
    }

    public void testGetPeersListAsStringShouldReturnNonEmptyList() {
        NetworkMember serv = new NetworkMember();
        int port = serv.joinNetwork();
        NetworkMember client = new NetworkMember();
        client.joinNetwork("127.0.0.1", port);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertFalse(serv.getPeersListAsStringArray().equals(""));
    }

    public void testGetPeersListAsStringSizeShouldBeEqualForEachMember() {
        NetworkMember s1 = new NetworkMember();
        int p1 = s1.joinNetwork();
        NetworkMember s2 = new NetworkMember();
        s2.joinNetwork("127.0.0.1",p1);
        NetworkMember s3 = new NetworkMember();
        s3.joinNetwork("127.0.0.1",p1);

        String[] pp1 = s1.getPeersListAsStringArray();
        String[] pp2 = s2.getPeersListAsStringArray();
        String[] pp3 = s3.getPeersListAsStringArray();

        assertTrue(pp1.length == pp2.length && pp2.length == pp3.length);
        Arrays.sort(pp1);
        Arrays.sort(pp2);
        Arrays.sort(pp3);

        for (int i = 0;i<pp1.length;i++) {
            assertTrue(pp1[i].equals(pp2[i]) && pp2[i].equals(pp3[i]));
        }
    }

}
