package com.example.java2project.models;

import java.io.Serializable;

public class Card implements Serializable {
    private String effect;

    public Card( String effect) {
        this.effect = effect;
    }


    public String getEffect() {
        return effect;
    }

}