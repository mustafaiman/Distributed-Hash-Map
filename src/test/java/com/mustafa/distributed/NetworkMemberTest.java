package com.mustafa.distributed;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.net.Socket;
import java.util.ArrayList;
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

    public void testNetworkMemberWaitForPossibleConnectionsAndRespond() {
        NetworkMember serv = new NetworkMember();
        NetworkMember client = new NetworkMember();
        int port = serv.getServerPort();
        assertTrue(serv.getPeersList().size() == 0);
        client.connectPeer("127.0.0.1", port);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(1, serv.getPeersList().size());
        assertEquals(1, client.getPeersList().size());
    }

    public void testNewMemberShouldGetPeersListFromNetwork() {
        NetworkMember serv = new NetworkMember();
        NetworkMember client = new NetworkMember();
        int port = serv.getServerPort();
        client.connectPeer("127.0.0.1", port);
        NetworkMember client2 = new NetworkMember();
        client2.connectPeer("127.0.0.1",port);
        assertEquals(2,client2.getPeersList().size());
    }

    public void testOldMemberShouldLearnANewMemberJoined() {
        NetworkMember serv = new NetworkMember();
        NetworkMember client = new NetworkMember();
        int port = serv.getServerPort();
        client.connectPeer("127.0.0.1", port);
        NetworkMember client2 = new NetworkMember();
        client2.connectPeer("127.0.0.1",port);
        assertEquals(2,client.getPeersList().size());
    }

    public void testSendObject() {
        NetworkMember s1 = new NetworkMember();
        NetworkMember s2 = new NetworkMember();
        int port = s1.getServerPort();
        s2.connectPeer("127.0.0.1",port);
        boolean succ = s1.sendObject("ssss", s1.getPeersList().get(0));
        assertTrue(succ);
    }


}
