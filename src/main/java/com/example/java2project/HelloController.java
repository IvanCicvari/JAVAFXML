package com.example.java2project;

import com.example.java2project.config.UserRole;
import com.example.java2project.models.Card;
import com.example.java2project.models.GameState;
import com.example.java2project.utils.*;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

import java.util.List;

public class HelloController {
    @FXML private Button USAButtonUK;
    @FXML private Button USSRButtonUK;
    @FXML private Button USAButtonFrance;
    @FXML private Button USSRButtonFrance;
    @FXML private Button USAButtonGermany;
    @FXML private Button USSRButtonGermany;
    @FXML private Button USAButtonPoland;
    @FXML private Button USSRButtonPoland;
    @FXML private Button USAButtonYugoslavia;
    @FXML private Button USSRButtonYugoslavia;
    @FXML private Button USAButtonBenelux;
    @FXML private Button USSRButtonUKBenelux;
    @FXML private VBox cardbox;
    private static PlayerType playerTurn;
    private static Integer numberOfMoves;
    public static Button[][] gameBoard;
    private List<Card> cards;

    @FXML private TextField chatTextField;
    @FXML private TextFlow chatTextFlow;
    @FXML private ScrollPane scroll;
    @FXML private Label theLastGameMoveLabel;

    public void initialize() {
        System.out.println("Initializing game...");
        chatTextFlow.setDisable(true);
        numberOfMoves = 0;
        playerTurn = PlayerType.USA;
        gameBoard = new Button[GameStateUtils.COUNTRY_INDEX][GameStateUtils.SUPERPOWER_INDEX];
        cards = GameStateUtils.generateSampleCards();
        initializeGameBoard();
        displayCards(cards);
        System.out.println("Deck initialized.");
    }

    private void initializeGameBoard() {
        gameBoard[0][0] = USAButtonUK;
        gameBoard[0][1] = USSRButtonUK;
        gameBoard[1][0] = USAButtonFrance;
        gameBoard[1][1] = USSRButtonFrance;
        gameBoard[2][0] = USAButtonGermany;
        gameBoard[2][1] = USSRButtonGermany;
        gameBoard[3][0] = USAButtonPoland;
        gameBoard[3][1] = USSRButtonPoland;
        gameBoard[4][0] = USAButtonYugoslavia;
        gameBoard[4][1] = USSRButtonYugoslavia;
        gameBoard[5][0] = USAButtonBenelux;
        gameBoard[5][1] = USSRButtonUKBenelux;
    }

    public static void refreshGameBoard(GameState gameState, HelloController controller) {
        controller.restoreGameState(gameState);
    }

    public void restoreGameState(GameState gameState) {
        numberOfMoves = gameState.getNumberOfMoves();
        playerTurn = gameState.getCurrentPlayer();
        Boolean endGame = gameState.getEndGame();
        if (endGame) {
            DialogUtils.showInformationDialog("YOU WON", "Congrats");
        } else {
            playerTurn = (playerTurn == PlayerType.USA) ? PlayerType.USSR : PlayerType.USA;
        }
        PlayerType[][] symbolsOnBoard = gameState.getPlayerSymbols();
        for (int i = 0; i < GameStateUtils.COUNTRY_INDEX; i++) {
            for (int j = 0; j < GameStateUtils.SUPERPOWER_INDEX; j++) {
                if (symbolsOnBoard[i][j] != null) {
                    gameBoard[i][j].setText(symbolsOnBoard[i][j].name());
                } else {
                    gameBoard[i][j].setText("");
                }
            }
        }
        // Restore the cards state
        cards = gameState.getCards();
        displayCards(cards);
    }

    public void addCardToVBox(Card card) {
        VBox cardVBox = new VBox();
        Label cardName = new Label(card.getName());
        Label cardDescription = new Label(card.getEffect());
        Button playButton = new Button("Play Card");

        playButton.setOnAction(event -> {
            playCard(card);
            cardbox.getChildren().remove(cardVBox);
        });

        cardVBox.getChildren().addAll(cardName, cardDescription, playButton);
        cardbox.getChildren().add(cardVBox);
    }

    public void displayCards(List<Card> cards) {
        for (Card card : cards) {
            addCardToVBox(card);
        }
    }

    private void playCard(Card card) {
        System.out.println("Playing card: " + card.getName());
    }

    public void buttonPressed(Event event) {
        Button buttonPressed = (Button) event.getSource();
        if (buttonPressed.getText().isBlank()) {
            buttonPressed.setText(playerTurn.name());

            numberOfMoves++;

            Boolean endGame = checkWinner();
            if (UserRole.CLIENT.name().equals(HelloApplication.currentUserRole.name())) {
                sendGameStateToServer(endGame);
            } else if(UserRole.SERVER.name().equals(HelloApplication.currentUserRole.name())) {
                sendGameStateToClient(endGame);
            }
            playerTurn = playerTurn == PlayerType.USA ? PlayerType.USSR : PlayerType.USA;

        }
    }

    public void sendGameStateToServer(Boolean endGame) {
        GameState gameState = GameStateUtils.createGameState(
                gameBoard, numberOfMoves, playerTurn, endGame, cards);
        NetworkingUtils.sendRequestToServer(gameState);
    }

    public void sendGameStateToClient(Boolean endGame) {
        GameState gameState = GameStateUtils.createGameState(
                gameBoard, numberOfMoves, playerTurn, endGame,cards);
        NetworkingUtils.sendRequestToClient(gameState);
    }

    private Boolean checkWinner() {
        int count = 0;
        for (int i = 0; i < GameStateUtils.COUNTRY_INDEX; i++) {
            for (int j = 0; j < GameStateUtils.SUPERPOWER_INDEX; j++) {
                if (gameBoard[i][j].getText().equals(PlayerType.USA.name())) {
                    count++;
                    if (count == 3) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void saveGame() {
        // FileUtils.saveGameToFile(gameBoard, numberOfMoves, playerTurn, GameStateUtils.COUNTRY_INDEX, GameStateUtils.SUPERPOWER_INDEX);
    }

    public void loadGame() {
        GameState recoveredGameState = FileUtils.loadGameStateFromFile();
        restoreGameState(recoveredGameState);
    }

    public void sendChatMessage() {
        System.out.println("jej");
    }

    public void generateDocumentation() {
        DocumentationUtils.generateHtmlDocumentationFile();
    }
}
