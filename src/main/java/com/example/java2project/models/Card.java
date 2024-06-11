package com.example.java2project.models;

import java.io.Serializable;

public class Card implements Serializable {
    private String name;
    private String effect;

    public Card(String name, String effect) {
        this.name = name;
        this.effect = effect;
    }

    public String getName() {
        return name;
    }

    public String getEffect() {
        return effect;
    }

    @Override
    public String toString() {
        return name + ": " + effect;
    }
}