package com.mustafa.distributed;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
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
    private ConcurrentHashMap<String,ObjectSocket> peers = new ConcurrentHashMap<>();
    private HashSet<String> connectionPorts = new HashSet<>();

    private ArrayList<String> pendingConnections = new ArrayList<>();

    private NetworkObserver observer;


    public NetworkMember() {
        try {
            serverSocket = new ServerSocket(0);
            connectionPorts.add(identifier());
            logger.info(identifier() + " started");
            executorServerSocket.submit(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    while (true) {
                        ObjectSocket socket = new ObjectSocket(serverSocket.accept());
                        logger.info(identifier() + " got connection from " + socket.toString() + "             " + socket.identifier());
                        addToPeers(socket);
                        requestExchangeConnectionPorts(socket);
                    }
                }
            });

            executorSockets.submit(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    while (true) {
                        try {
                            for (ObjectSocket peer : peers.values()) {
                                if (peer.available() > 0) {
                                    handlePeerMessage(peer);
                                }
                            }
                        } catch(Exception e) {
                            e.printStackTrace();
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
            connectionPorts.add(getIdentifierFromHostPort(host,port));
            Socket sck = new Socket(host,port);
            ObjectSocket socket = new ObjectSocket(sck);
            addToConnectionPorts(socket);
            addToPeers(socket);
            logger.info(identifier() + " is connected to " + socket.toString());
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

    public ConcurrentHashMap<String,ObjectSocket> getPeersList() {
        return (ConcurrentHashMap<String,ObjectSocket>) peers;
    }

    private void handlePeerMessage(ObjectSocket socket) {
        try {
            Object objectMessage = socket.readObject();
            if (objectMessage instanceof  RequestMessage) {
                RequestMessage requestMessage = (RequestMessage) objectMessage;
                logger.debug("Message ( " + identifier() + " ) " + requestMessage.msg.name() + " received from " + getIdentifierFromHostPort(socket.getRemoteHostAddress(),socket.getRemoteHostPort()));
                switch (requestMessage.msg) {
                    case EXCHANGE_CONNECTION_PORTS: messageExchangeConnectionPorts(socket, requestMessage); break;
                    case PROVIDE_CONNECTION_PORTS: messageProvideConnectionPorts(socket, requestMessage); break;
                }
            } else {
                ApplicationMessage applicationMessage = (ApplicationMessage)objectMessage;
                logger.debug("Message ( " + identifier() + " ) " + applicationMessage.msg.name() + " received from " + getIdentifierFromHostPort(socket.getRemoteHostAddress(),socket.getRemoteHostPort()));
                notifyObserver(socket, applicationMessage);
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

    public void addToConnectionPorts(ObjectSocket socket) {
        connectionPorts.add(getIdentifierFromHostPort(socket.getSocket().getInetAddress().toString().split("/")[1],socket.getSocket().getPort()));
    }

    public void requestExchangeConnectionPorts(ObjectSocket socket) {
        logger.debug("Connection ports of " + identifier() + " ------> " + connectionPorts.toString());

        RequestMessage req = new RequestMessage();
        req.msg = RequestMessage.MSG.EXCHANGE_CONNECTION_PORTS;
        req.data = connectionPorts;
        System.out.println(connectionPorts.toString());
        req.identifier = identifier();
        sendObject(req, socket);

    }

    public void requestProvideConnectionPorts(ObjectSocket socket) {
        logger.debug("Connection ports of " + identifier() + " ------> " + connectionPorts.toString());


        RequestMessage req = new RequestMessage();
        req.msg = RequestMessage.MSG.PROVIDE_CONNECTION_PORTS;
        req.data = connectionPorts;
        req.identifier = identifier();
        sendObject(req, socket);
    }

    public void broadcast(RequestMessage message) {
        for (ObjectSocket peer : peers.values()) {
            sendObject(message, peer);
        }
    }

    public void broadcast(ApplicationMessage message) {
        String ids = "";
        for (ObjectSocket peer : peers.values()) {
            sendObject(message, peer);
            ids += peer.identifier() + ";";
        }
        logger.debug(identifier() + " broadcasted to " + ids);
    }

    private void addToPeers(ObjectSocket socket) {
            peers.put(socket.identifier(),socket);
    }

    private void messageExchangeConnectionPorts(ObjectSocket socket, RequestMessage message) {
        try {
            System.out.println(message.identifier);
            connectionPorts.add(((String) message.identifier));
            for (String s : ((HashSet<String>) message.data)) {
                if (!connectionPorts.contains(s) && !pendingConnections.contains(s)) {
                    pendingConnections.add(s);
                }
            }
            for (String s : pendingConnections) {
                logger.debug("Connecting( " + identifier() + " ) to " + s);
                connectPeer(getHostFromIdentifier(s), getPortFromIdentifier(s));
            }
            pendingConnections.clear();

            requestProvideConnectionPorts(socket);
        } catch (Exception e) {
            System.out.println(message.data);
            e.printStackTrace();
        }
    }

    private void messageProvideConnectionPorts(ObjectSocket socket, RequestMessage message) {
        connectionPorts.add(((String) message.identifier));
        for (String s : ((HashSet<String>) message.data)) {
            if (!connectionPorts.contains(s) && !pendingConnections.contains(s)) {
                pendingConnections.add(s);
            }
        }
        for (String s : pendingConnections) {
            connectPeer(getHostFromIdentifier(s), getPortFromIdentifier(s));
        }
        pendingConnections.clear();
    }

    public String getHostFromIdentifier(String id) {
        return id.split(":")[0];
    }

    public int getPortFromIdentifier(String id) {
        return Integer.parseInt(id.split(":")[1]);
    }

    public String getIdentifierFromHostPort(String host, int port) {
        if(host.contains("/")) {
            host = host.split("/")[1];
        }
        return host + ":" + port;
    }

    public String identifier() {
        try {
            return getIdentifierFromHostPort(InetAddress.getLocalHost().toString().split("/")[1], getServerPort());
        } catch (UnknownHostException e) {
            logger.error(e);
            return null;
        }
    }

    /**
     * NetworkMember supports only one observer at a time. Registering a new observer removes previous one.
     * NetworkMember redirects every message other than its own internal messages to the observer.
     * @param observer Observer object to be notified.
     */
    public void registerObserver(NetworkObserver observer) {
        this.observer = observer;
    }

    public void notifyObserver(ObjectSocket socket, ApplicationMessage message) {
        if(observer!= null) {
            observer.onMessage(socket, message);
        }
    }
}
