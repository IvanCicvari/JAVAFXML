package com.example.java2project;

import com.example.java2project.config.ConfigurationKey;
import com.example.java2project.config.UserRole;
import com.example.java2project.models.GameState;
import com.example.java2project.remote.RemoteChatService;
import com.example.java2project.utils.ConfigurationReader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class HelloApplication extends Application {
    public static UserRole currentUserRole;
    private static Scene mainScene;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
        stage.setTitle(currentUserRole.name());
        stage.setScene(scene);
        stage.show();
        mainScene = scene;

    }

    public static void main(String[] args) {
        new Thread(Application::launch).start();
        System.out.println("Username: " + args[0]);
        UserRole activeUser = UserRole.CLIENT;
        currentUserRole = activeUser;

        Boolean userValid = false;

        for(UserRole userRole : UserRole.values()) {
            if(userRole.name().equals(args[0])) {
                userValid = true;
                activeUser = UserRole.valueOf(args[0]);
                currentUserRole = activeUser;
                break;
            }
        }

        if(userValid) {

            if(UserRole.SERVER.name().equals(activeUser.name())) {
                startServer();
            }
            else if(UserRole.CLIENT.name().equals(activeUser.name())){
                startClient();
            }

        }
    }

    private static void startServer() {
        acceptRequestsFromClient();
    }

    private static void acceptRequestsFromClient() {
        Integer serverPort = ConfigurationReader.readIntegerConfigurationValueForKey(ConfigurationKey.SERVER_PORT);
        try (ServerSocket serverSocket = new ServerSocket(serverPort)){
            System.err.println("Server listening on port: " + serverSocket.getLocalPort());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.err.println("Client connected from port: " + clientSocket.getPort());
                Platform.runLater(() ->  processSerializableClient(clientSocket));
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void processSerializableClient(Socket clientSocket) {
        try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());){
            GameState receivedGameState = (GameState) ois.readObject();
            HelloController controller = new HelloController();
            HelloController.refreshGameBoard(receivedGameState);
            oos.writeObject("Game state received!");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    private static void acceptRequestsFromServer() {
        Integer clientPort = ConfigurationReader.readIntegerConfigurationValueForKey(ConfigurationKey.CLIENT_PORT);
        try (ServerSocket serverSocket = new ServerSocket(clientPort)){
            System.err.println("Server listening on port: " + serverSocket.getLocalPort());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.err.println("Client connected from port: " + clientSocket.getPort());
                Platform.runLater(() ->  processSerializableClient(clientSocket));
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void startClient() {
        acceptRequestsFromServer();
    }


    public static Scene getMainScene() {
        return mainScene;
    }
}