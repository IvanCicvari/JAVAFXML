package com.example.java2project.models;

import com.example.java2project.PlayerType;
import javafx.scene.control.Button;

import java.io.Serializable;
import java.util.List;

public class GameState implements Serializable {

    private Integer numberOfMoves;
    private PlayerType currentPlayer;
    private PlayerType[][] playerSymbols;
    private Boolean endGame;
    private List<Card> cards;


    public GameState(Integer numberOfMoves, PlayerType currentPlayer, PlayerType[][] playerSymbols,
                     Boolean endGame,List<Card> cards) {
        this.numberOfMoves = numberOfMoves;
        this.currentPlayer = currentPlayer;
        this.playerSymbols = playerSymbols;
        this.endGame = endGame;
        this.cards = cards;

    }
    public GameState( Integer numberOfMoves,PlayerType currentPlayer,PlayerType[][] playerSymbols ) {
        this.numberOfMoves = numberOfMoves;
        this.currentPlayer = currentPlayer;
        this.playerSymbols = playerSymbols;
    }
    public PlayerType getCurrentPlayer() {
        return currentPlayer;
    }
    public PlayerType[][] getPlayerSymbols() {
        return playerSymbols;
    }
    public Integer getNumberOfMoves() {
        return numberOfMoves;
    }
    public Boolean getEndGame() {
        return endGame;
    }
    public List<Card> getCards() {
        return cards;
    }

}
