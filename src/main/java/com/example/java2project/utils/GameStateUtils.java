package com.example.java2project.utils;

import com.example.java2project.PlayerType;
import com.example.java2project.models.Card;
import com.example.java2project.models.GameState;
import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.List;

public class GameStateUtils {
    public static final int COUNTRY_INDEX = 6;
    public static final int SUPERPOWER_INDEX = 2;

    // Method to create GameState including cards
    public static GameState createGameState(Button[][] gameBoard, int numberOfMoves, PlayerType currentPlayer, boolean endGame, List<Card> cards) {
        PlayerType[][] symbolsOnBoardToSave = new PlayerType[COUNTRY_INDEX][SUPERPOWER_INDEX];

        for (int i = 0; i < COUNTRY_INDEX; i++) {
            for (int j = 0; j < SUPERPOWER_INDEX; j++) {
                if (!gameBoard[i][j].getText().isBlank()) {
                    symbolsOnBoardToSave[i][j] = PlayerType.valueOf(gameBoard[i][j].getText());
                }
            }
        }

        return new GameState(numberOfMoves, currentPlayer,symbolsOnBoardToSave,endGame, cards);
    }

    // Method to create GameState without cards
    public static GameState createGameState(Button[][] gameBoard, int numberOfMoves, PlayerType currentPlayer, boolean endGame) {
        PlayerType[][] symbolsOnBoardToSave = new PlayerType[COUNTRY_INDEX][SUPERPOWER_INDEX];

        for (int i = 0; i < COUNTRY_INDEX; i++) {
            for (int j = 0; j < SUPERPOWER_INDEX; j++) {
                if (!gameBoard[i][j].getText().isBlank()) {
                    symbolsOnBoardToSave[i][j] = PlayerType.valueOf(gameBoard[i][j].getText());
                }
            }
        }

        return new GameState(numberOfMoves, currentPlayer, symbolsOnBoardToSave, endGame, new ArrayList<>());
    }

    // Sample method to generate cards
    public static List<Card> generateSampleCards() {
        List<Card> cards = new ArrayList<>();
        cards.add(new Card("Card 1", "Description of Card 1"));
        cards.add(new Card("Card 2", "Description of Card 2"));
        cards.add(new Card("Card 3", "Description of Card 3"));
        return cards;
    }
}
