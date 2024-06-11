package com.example.java2project.utils;

import com.example.java2project.config.ConfigurationKey;
import com.example.java2project.models.GameState;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetworkingUtils {

    public static void sendRequestToServer(GameState gameStateToSend) {
        try (Socket clientSocket = new Socket(ConfigurationKey.HOST, ConfigurationKey.SERVER_PORT)){
            System.err.println("Client is connecting to " + clientSocket.getInetAddress() + ":" +clientSocket.getPort());
            sendSerializableRequest(clientSocket, gameStateToSend);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static void sendRequestToClient(GameState gameStateToSend) {
        try (Socket clientSocket = new Socket(ConfigurationKey.HOST, ConfigurationKey.CLIENT_PORT)){
            System.err.println("Client is connecting to " + clientSocket.getInetAddress() + ":" +clientSocket.getPort());
            sendSerializableRequest(clientSocket, gameStateToSend);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    private static void sendSerializableRequest(Socket client, GameState gameState) throws IOException, ClassNotFoundException {
        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
        oos.writeObject(gameState);
        System.out.println("Game state sent...");
        String messageFromServer = (String) ois.readObject();
        System.out.println("Got a message from server: " + messageFromServer);
    }

}
