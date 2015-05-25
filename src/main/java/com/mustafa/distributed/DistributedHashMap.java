package com.mustafa.distributed;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.HashMap;


public class DistributedHashMap<K,V> extends HashMap<K,V> implements NetworkObserver
{
    final static Logger logger = LogManager.getLogger(NetworkMember.class.getName());

    NetworkMember member;

    public DistributedHashMap() {
        super();
        member = new NetworkMember();
        member.registerObserver(this);
    }

    public void connect(String host, int port) {
        member.connectPeer(host, port);
    }

    public String getHost() {
        return member.getHostFromIdentifier(member.identifier());
    }

    public int getPort() {
        return member.getPortFromIdentifier(member.identifier());
    }

    @Override
    public void onMessage(ObjectSocket socket, ApplicationMessage message) {
        switch (message.msg) {
            case PUT: messagePut(socket, message); break;
            case GET: messageGet(socket, message); break;
            case REMOVE: messageRemove(socket, message); break;
        }
    }

    @Override
    public V put(K key, V value) {
        V returnValue = super.put(key, value);
        ApplicationMessage applicationMessage = new ApplicationMessage();
        applicationMessage.msg = ApplicationMessage.MSG.PUT;

        applicationMessage.tag = (Serializable) key;
        applicationMessage.value = (Serializable) value;
        member.broadcast(applicationMessage);
        return returnValue;
    }

    @Override
    public V get(Object key) {
        //TODO
        return super.get(key);
    }

    @Override
    public V remove(Object key) {

        ApplicationMessage applicationMessage = new ApplicationMessage();
        applicationMessage.msg = ApplicationMessage.MSG.REMOVE;
        applicationMessage.tag = (Serializable) key;
        member.broadcast(applicationMessage);
        return super.remove(key);
    }

    public void messagePut(ObjectSocket socket, ApplicationMessage message) {
        logger.debug(member.identifier() + " got PUT message from " + socket.identifier());
        super.put((K) message.tag, (V) message.value);
    }

    public void messageGet(ObjectSocket socket, ApplicationMessage message) {
        //TODO
    }

    public void messageRemove(ObjectSocket socket, ApplicationMessage message) {
        logger.debug(member.identifier() + " got REMOVE message from " + socket.identifier());
        super.remove(message.tag);
    }
}
