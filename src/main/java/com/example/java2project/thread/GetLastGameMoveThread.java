package com.example.java2project.thread;

import com.example.java2project.models.GameMove;
import javafx.application.Platform;
import javafx.scene.control.Label;

public class GetLastGameMoveThread extends GameMoveThread implements Runnable {

    private Label theLastGameMoveLabel;

    public GetLastGameMoveThread(Label theLastGameMoveLabel) {
        this.theLastGameMoveLabel = theLastGameMoveLabel;
    }

    @Override
    public void run() {
        while(true) {
            GameMove lastGameMove = getLastGameMove();

            Platform.runLater(() -> {
                theLastGameMoveLabel.setText("The last game move: "
                        + lastGameMove.getPlayerType() + " " + lastGameMove.getLocation()
                        + " " + lastGameMove.getDateTime());
            });

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
