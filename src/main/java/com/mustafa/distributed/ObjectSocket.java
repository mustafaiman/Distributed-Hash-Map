package com.mustafa.distributed;

import java.io.*;
import java.net.Socket;

/**
 * Created by Mustafa on 23.5.2015.
 */
public class ObjectSocket implements Serializable {
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Socket socket;

    public ObjectSocket(Socket socket) {
        this.socket = socket;
    }

    public ObjectOutputStream getObjectOutputStream() throws IOException {
        if (oos == null) {
            oos = new ObjectOutputStream(getOutputStream());
        }
        return oos;
    }

    public ObjectInputStream getObjectInputStream() throws IOException {
        if (ois == null) {
            ois = new ObjectInputStream(getInputStream());
        }
        return ois;
    }

    public void sendObjectAndFlush(Serializable object) throws IOException{
        getObjectOutputStream().writeObject(object);
        getObjectOutputStream().flush();
    }

    public Serializable readObject() throws  IOException {
        if (available() <= 0)
            return null;
        try {
            return (Serializable) getObjectInputStream().readObject();
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public int available() throws IOException{
        return getObjectInputStream().available();
    }

    public Socket getSocket() {
        return socket;
    }

    public InputStream getInputStream() throws IOException{
        return socket.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException{
        return socket.getOutputStream();
    }

    public String toString() {
        return socket.toString();
    }
}
