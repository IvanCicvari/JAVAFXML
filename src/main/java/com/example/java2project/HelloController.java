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
    @FXML private Button firstCardButton;
    private static PlayerType playerTurn;
    private static Integer numberOfMoves;
    public static Button[][] gameBoard;

    public static Button[][] buttonCard;


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
        buttonCard = new Button[GameStateUtils.ROW][GameStateUtils.COLUMNS];
        initializeGameBoard();
        initializeButton();
    }
    private void initializeButton(){
        buttonCard[0][0]= firstCardButton;
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
        // Restore basic game state information
        numberOfMoves = gameState.getNumberOfMoves();
        playerTurn = gameState.getCurrentPlayer();
        Boolean endGame = gameState.getEndGame();

        if (endGame) {
            DialogUtils.showInformationDialog("YOU WON", "Congrats");
        } else {
            playerTurn = (playerTurn == PlayerType.USA) ? PlayerType.USSR : PlayerType.USA;
        }

        Integer[][] boardValues = gameState.getBoardValues();
        for (int i = 0; i < GameStateUtils.COUNTRY_INDEX; i++) {
            for (int j = 0; j < GameStateUtils.SUPERPOWER_INDEX; j++) {
                if (boardValues[i][j] != null) {
                    gameBoard[i][j].setText(boardValues[i][j].toString());
                } else {
                    gameBoard[i][j].setText("0");
                }
            }
        }
        String[][] cardValuesToSave = gameState.getCardEffect();
        for (int i = 0; i < GameStateUtils.ROW; i++) {
            for (int j = 0; j < GameStateUtils.COLUMNS; j++) {
                if (cardValuesToSave[i][j] != null) {
                    buttonCard[i][j].setText(GameStateUtils.getNextCard());
                } else {
                    buttonCard[i][j].setText("");
                }
            }
        }

    }
    public void cardCreate(Event event) {
        Button buttonPressed = (Button) event.getSource();

        Card sampleCard = GameStateUtils.getSampleCard();

        Boolean endGame = checkWinner();
        if (sampleCard != null && sampleCard.getEffect() != null) {
            buttonPressed.setText(sampleCard.getEffect());
        }

        // Update the game state and send it to the server/client
        if (UserRole.CLIENT.name().equals(HelloApplication.currentUserRole.name())) {
            sendGameStateToServer(endGame);
        } else if (UserRole.SERVER.name().equals(HelloApplication.currentUserRole.name())) {
            sendGameStateToClient(endGame);
        }

        playerTurn = playerTurn == PlayerType.USA ? PlayerType.USSR : PlayerType.USA;
    }

    public void buttonPressed(Event event) {
        Button buttonPressed = (Button) event.getSource();
        int currentValue = 0;
        if (!buttonPressed.getText().isBlank()) {
            currentValue = Integer.parseInt(buttonPressed.getText());
        }
        buttonPressed.setText(String.valueOf(currentValue + 1));

        numberOfMoves++;

        Boolean endGame = checkWinner();
        if (UserRole.CLIENT.name().equals(HelloApplication.currentUserRole.name())) {
            sendGameStateToServer(endGame);
        } else if (UserRole.SERVER.name().equals(HelloApplication.currentUserRole.name())) {
            sendGameStateToClient(endGame);
        }
        playerTurn = playerTurn == PlayerType.USA ? PlayerType.USSR : PlayerType.USA;
    }

    public void sendGameStateToServer(Boolean endGame) {
        GameState gameState = GameStateUtils.createGameState(gameBoard, numberOfMoves, playerTurn, endGame,buttonCard);
        NetworkingUtils.sendRequestToServer(gameState);
    }

    public void sendGameStateToClient(Boolean endGame) {
        GameState gameState = GameStateUtils.createGameState(gameBoard, numberOfMoves, playerTurn, endGame,buttonCard);
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
//        FileUtils.saveGameToFile(gameBoard, numberOfMoves, playerTurn);
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
