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
    private static final byte COMMAND_GET_PEERS_LIST = 11;
    private static final int MAX_RESPONSE_STRING = 1024;
    private static final byte COMMAND_PUT_KEY = 12;
    private static final byte COMMAND_SET_PEERS_LIST = 13;

    ExecutorService executorServerSocket = Executors.newSingleThreadExecutor();
    ExecutorService executorSockets = Executors.newSingleThreadExecutor();

    private ServerSocket serverSocket;
    private ArrayList<Socket> peers = new ArrayList<>();

    private Object lockPeers = new Object();

    public NetworkMember() {

    }

    public int joinNetwork() {
        if( serverSocket != null)
            return serverSocket.getLocalPort();

        try {
            serverSocket = new ServerSocket(0);


            executorServerSocket.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    while (true) {
                        Socket socket = serverSocket.accept();
                        logger.debug("Connection from " + socket.getRemoteSocketAddress());
                        addToPeers(socket);

                    }
                }
            });
            logger.debug("Server created at port: " + serverSocket.getLocalPort());

            executorSockets.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    while(true) {
                        for (Socket socket : peers) {
                            if (socket.getInputStream().available() > 0) {
                                handleMessage(socket);
                            }
                        }
                    }
                }
            });

            return serverSocket.getLocalPort();
        } catch (IOException e) {
            logger.error("Could not find suitable port.\n" + e);
            return 0;
        }
    }

    public void joinNetwork(String host, int port) {
        try {
            Socket socket = new Socket(host, port);
            logger.info("Connected to: " + socket.getRemoteSocketAddress().toString());
            //TODO get list of members and connect each of them
        } catch (IOException e) {
            logger.error("Could not connect to server at " + host + ":" + port + "\n" + e);
        }
    }

    private void handleMessage(Socket socket) {
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            byte command = dis.readByte();
            if (command == COMMAND_GET_PEERS_LIST) {
                dos.write(COMMAND_SET_PEERS_LIST);
                dos.writeUTF(Arrays.toString(getPeersListAsStringArray()));
                dos.flush();
            } else if(command == COMMAND_PUT_KEY) {

            } else if(command == COMMAND_SET_PEERS_LIST) {
                String[] arguments = getArgumentsFromStream(dis);
                addToPeers(arguments);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void addToPeers(Socket socket) {
        synchronized (lockPeers) {
            peers.add(socket);
        }
    }

    private void addToPeers(String[] peersList) {
        for (String arg : peersList) {
            String h[] = arg.split(":");
            logger.debug("Adding peer " + Arrays.toString(h));
            try {
                Socket peerSocket = new Socket(h[0],Integer.parseInt(h[1]));
                addToPeers(peerSocket);
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }

    public String[] getPeersListAsStringArray() {
        synchronized (lockPeers) {
            String[] strings = null;
            if (peers.size() == 0)
                return strings;
            strings = new String[peers.size()];
            int i = 0;
            for (Socket socket : peers) {
                strings[i++] = socket.getRemoteSocketAddress().toString();
            }
            return strings;
        }
    }


    private String[] getArgumentsFromStream(DataInputStream dis) {
        String parameters = null;
        try {
            parameters = dis.readUTF();
        } catch (IOException e) {
            logger.error(e);
        }
        return parameters.split("\n");
    }
}
