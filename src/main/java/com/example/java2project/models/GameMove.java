package com.example.java2project.models;

import com.example.java2project.PlayerType;

import java.io.Serializable;
import java.time.LocalDateTime;

public class GameMove implements Serializable {
    private PlayerType playerType;

    private String location;

    private LocalDateTime dateTime;

    public GameMove(PlayerType playerType, String location, LocalDateTime dateTime) {
        this.playerType = playerType;
        this.location = location;
        this.dateTime = dateTime;
    }

    public PlayerType getSymbol() {
        return playerType;
    }

    public void setSymbol(PlayerType symbol) {
        this.playerType = symbol;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
