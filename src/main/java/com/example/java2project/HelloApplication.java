package com.example.java2project;

import com.example.java2project.config.ConfigurationKey;
import com.example.java2project.config.UserRole;
import com.example.java2project.models.GameState;
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
        Scene scene = new Scene(fxmlLoader.load(), 600, 600);
        mainScene = scene;

        stage.setTitle(currentUserRole.name());
        stage.setScene(scene);
        stage.show();
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
        try (ServerSocket serverSocket = new ServerSocket(ConfigurationKey.SERVER_PORT)){
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

            HelloController.refreshGameBoard(receivedGameState,controller);
            oos.writeObject("Game state received!");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    private static void acceptRequestsFromServer() {
        try (ServerSocket serverSocket = new ServerSocket(ConfigurationKey.CLIENT_PORT)){
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