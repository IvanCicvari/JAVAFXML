package com.example.java2project.utils;

import com.example.java2project.PlayerType;
import com.example.java2project.models.Card;
import com.example.java2project.models.GameState;
import javafx.scene.control.Button;

import java.io.*;
import java.util.List;

public class FileUtils {
    public static final String SAVE_GAME_FILE_NAME = "savegame.dat";

//    public static void saveGameToFile(Button[][] gameBoard, Integer numberOfMoves, PlayerType turn) {
//        GameState gameStateToSave = GameStateUtils.createGameState(gameBoard, numberOfMoves, turn, false);
//
//        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_GAME_FILE_NAME))) {
//            oos.writeObject(gameStateToSave);
//            DialogUtils.showInformationDialog("Save game success!", "You have successfully saved your game!");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public static GameState loadGameStateFromFile() {
        GameState recoveredGameState;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_GAME_FILE_NAME))) {
            recoveredGameState = (GameState) ois.readObject();
            DialogUtils.showInformationDialog("Load game success!", "You have successfully loaded your game!");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return recoveredGameState;
    }
}
