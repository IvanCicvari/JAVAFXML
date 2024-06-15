package com.example.java2project.models;

import com.example.java2project.PlayerType;
import javafx.scene.control.Button;

import java.io.Serializable;
import java.util.List;

public class GameState implements Serializable {
    private Integer numberOfMoves;
    private PlayerType currentPlayer;

    private Integer[][] playerSymbols;
    private Boolean endGame;

    public String[][] getCardEffect() {
        return cardEffect;
    }

    private String[][] cardEffect;

    public GameState(Integer numberOfMoves, PlayerType currentPlayer, Integer[][] playerSymbols,
                     Boolean endGame,String[][] cardEffect) {
        this.numberOfMoves = numberOfMoves;
        this.currentPlayer = currentPlayer;
        this.playerSymbols = playerSymbols;
        this.endGame = endGame;
        this.cardEffect =cardEffect;

    }
    public GameState( Integer numberOfMoves,PlayerType currentPlayer,Integer[][] playerSymbols ) {
        this.numberOfMoves = numberOfMoves;
        this.currentPlayer = currentPlayer;
        this.playerSymbols = playerSymbols;
    }
    public PlayerType getCurrentPlayer() {
        return currentPlayer;
    }
    public Integer getNumberOfMoves() {
        return numberOfMoves;
    }
    public Boolean getEndGame() {
        return endGame;
    }
    public Integer[][] getBoardValues() {
        return playerSymbols;
    }
}
