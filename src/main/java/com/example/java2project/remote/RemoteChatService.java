package com.example.java2project.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RemoteChatService extends Remote {
    String REMOTE_OBJECT_NAME = "hr.algebra.rmi.service";
    void sendChatMessage(String chatMessage) throws RemoteException;
    List<String> getAllChatMessages() throws RemoteException;

}
