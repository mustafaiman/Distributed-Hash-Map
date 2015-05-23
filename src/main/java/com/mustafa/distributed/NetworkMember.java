package com.mustafa.distributed;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Mustafa on 20.5.2015.
 */
public class NetworkMember {

    final static Logger logger = LogManager.getLogger(NetworkMember.class.getName());

    ExecutorService executorServerSocket = Executors.newSingleThreadExecutor();
    ExecutorService executorSockets = Executors.newSingleThreadExecutor();

    private ServerSocket serverSocket;
    private ArrayList<ObjectSocket> peers = new ArrayList<>();

    private Object lockPeers = new Object();

    public NetworkMember() {
        try {
            serverSocket = new ServerSocket(0);
            executorServerSocket.submit(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    while(true) {
                        ObjectSocket socket = new ObjectSocket(serverSocket.accept());

                        logger.info(serverSocket.toString() + " got connection from " + socket.toString());
                        addToPeers(socket);
                    }
                }
            });

            executorSockets.submit(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    while (true) {
                        for(ObjectSocket peer : peers) {
                            if (peer.available() > 0) {
                                handlePeerMessage(peer);
                            }
                        }
                    }
                }
            });
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    public void connectPeer(String host, int port) {
        try {
            ObjectSocket socket = new ObjectSocket(new Socket(host,port));
            addToPeers(socket);
            logger.info(serverSocket.toString() + " is connected to " + socket.toString());
            requestPeersListFromNetwork();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return local port that this instance run on
     */
    public int getServerPort() {
        if(serverSocket == null)
            return 0;
        else
            return serverSocket.getLocalPort();
    }

    public ArrayList<ObjectSocket> getPeersList() {
        return (ArrayList<ObjectSocket>) peers.clone();
    }

    private void handlePeerMessage(ObjectSocket socket) {
        try {
            RequestMessage message = (RequestMessage) socket.readObject();

            if (message == null)
                return;
            logger.debug("Message " + message.name() + " received from " + socket.toString());
            switch (message) {
                case GET_PEERS_LIST: messageGetPeersList(socket, message);break;
                case PEERS_LIST: messagePeersList(socket, message);break;
            }
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /**
     * Writes an object to outputstream of desired target and flushes
     * @param object should implement Serializable
     * @param socket ObjectSocket to receive the object
     * @return
     */
    public boolean sendObject(Serializable object, ObjectSocket socket) {
        try {
            socket.sendObjectAndFlush(object);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public void requestPeersListFromNetwork() {
        logger.debug("Peer count of " + serverSocket.toString() + peers.size());
        if (peers.size() == 0)
            return;
        sendObject(RequestMessage.GET_PEERS_LIST, peers.get(0));

    }

    private void addToPeers(ObjectSocket socket) {
        synchronized (lockPeers) {
            peers.add(socket);
        }
    }

    private void messageGetPeersList(ObjectSocket socket, RequestMessage message) {
        RequestMessage response = RequestMessage.PEERS_LIST;
        response.data = peers;
        sendObject(response,socket);
    }

    private void messagePeersList(ObjectSocket socket, RequestMessage message) {
        logger.debug("Peers list received from " + socket);
        for (ObjectSocket s : (ArrayList<ObjectSocket>)message.data) {
            logger.debug("messagePeersList " + s.toString());
        }
    }
}
