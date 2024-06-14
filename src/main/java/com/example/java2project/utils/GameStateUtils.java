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
    public static final int ROW = 1;
    public static final int COLUMNS = 3;


    public static GameState createGameState(Button[][] gameBoard, int numberOfMoves, PlayerType currentPlayer, boolean endGame, Button[][] cardEffect) {
        Integer[][] boardValuesToSave = new Integer[COUNTRY_INDEX][SUPERPOWER_INDEX];

        for (int i = 0; i < COUNTRY_INDEX; i++) {
            for (int j = 0; j < SUPERPOWER_INDEX; j++) {
                if (!gameBoard[i][j].getText().isBlank()) {
                    boardValuesToSave[i][j] = Integer.parseInt(gameBoard[i][j].getText());
                } else {
                    boardValuesToSave[i][j] = 0;
                }
            }
        }

        String[][] cardValuesToSave = new String[ROW][COLUMNS];
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if (cardEffect[i][j] != null && !cardEffect[i][j].getText().isBlank()) {
                    cardValuesToSave[i][j] = cardEffect[i][j].getText();
                } else {
                    cardValuesToSave[i][j] = "";
                }
            }
        }

        return new GameState(numberOfMoves, currentPlayer, boardValuesToSave, endGame, cardValuesToSave);
    }


    //    public static GameState createGameState(Button[][] gameBoard, int numberOfMoves, PlayerType currentPlayer, boolean endGame) {
//        Integer[][] boardValuesToSave = new Integer[COUNTRY_INDEX][SUPERPOWER_INDEX];
//
//        for (int i = 0; i < COUNTRY_INDEX; i++) {
//            for (int j = 0; j < SUPERPOWER_INDEX; j++) {
//                if (!gameBoard[i][j].getText().isBlank()) {
//                    boardValuesToSave[i][j] = Integer.parseInt(gameBoard[i][j].getText());
//                } else {
//                    boardValuesToSave[i][j] = 0; // Default value for empty fields
//                }
//            }
//        }
//
//        return new GameState(numberOfMoves, currentPlayer, boardValuesToSave, endGame);
//    }
    private static List<Card> cards = generateSampleCards();
    private static int currentCardIndex = 0;

    // Sample method to generate a list of cards
    public static List<Card> generateSampleCards() {
        List<Card> cards = new ArrayList<>();
        cards.add(new Card("ADD 1 INFLUCANE POINT TO UK "));
        cards.add(new Card("ADD 1 INFLUCANE POINT TO FRANCE "));
        cards.add(new Card("ADD 4 INFLUCANE POINT TO YUGOSLAVIA "));
        cards.add(new Card("ADD 3 INFLUCANE POINT TO BENELUX "));
        cards.add(new Card("ADD 2 INFLUCANE POINT TO GERMANY "));
        cards.add(new Card("ADD 4 INFLUCANE POINT TO POLAND "));
        cards.add(new Card("ADD 2 INFLUCANE POINT TO UK "));
        cards.add(new Card("ADD 2 INFLUCANE POINT TO FRANCE "));
        cards.add(new Card("ADD 2 INFLUCANE POINT TO YUGOSLAVIA "));
        cards.add(new Card("ADD 3 INFLUCANE POINT TO BENELUX "));
        cards.add(new Card("ADD 3 INFLUCANE POINT TO GERMANY "));
        cards.add(new Card("ADD 1 INFLUCANE POINT TO POLAND "));
        return cards;
    }

    // Sample method to get a single card from the list
    public static Card getNextCard() {
        Card nextCard = cards.get(currentCardIndex);
        currentCardIndex = (currentCardIndex + 1) % cards.size(); // Move to the next card, loop back if at the end
        return nextCard;
    }
    public static String getNextCardEffect() {
        return getNextCard().getEffect();
    }
}