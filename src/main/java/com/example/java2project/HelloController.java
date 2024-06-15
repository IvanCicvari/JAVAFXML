package com.example.java2project;

import com.example.java2project.config.ConfigurationKey;
import com.example.java2project.config.UserRole;
import com.example.java2project.models.Card;
import com.example.java2project.models.GameMove;
import com.example.java2project.models.GameState;
import com.example.java2project.remote.RemoteChatService;
import com.example.java2project.remote.RemoteChatServiceImpl;
import com.example.java2project.thread.GetLastGameMoveThread;
import com.example.java2project.thread.SaveNewGameMoveThread;
import com.example.java2project.utils.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
    @FXML private TextArea chatMessagesTextArea;
    @FXML private Label theLastGameMoveLabel;
    public static RemoteChatService remoteChatService;

    public void initialize() {
        System.out.println("Initializing game...");

        numberOfMoves = 0;
        playerTurn = PlayerType.USA;
        gameBoard = new Button[GameStateUtils.COUNTRY_INDEX][GameStateUtils.SUPERPOWER_INDEX];
        buttonCard = new Button[GameStateUtils.ROW][GameStateUtils.COLUMNS];
        initializeGameBoard();
        initializeButton();
        if(HelloApplication.currentUserRole == UserRole.SERVER)
        {
            startRmiRemoteChatServer();
        }
        else {
            startRmiClient();
        }
        chatTextField.setOnKeyPressed(event -> {
                    if (event.getCode().equals(KeyCode.ENTER)) {
                        sendChatMessage();
                    }
                }
        );
        GetLastGameMoveThread getLastGameMoveThread
                = new GetLastGameMoveThread(theLastGameMoveLabel);
        Thread starterThread = new Thread(getLastGameMoveThread);
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
    public void replayTheLastGame() {
        List<GameMove> gameMoveList = XmlUtils.getAllGameMoves();

        AtomicInteger i = new AtomicInteger(0);

        final Timeline replayer = new Timeline(
                new KeyFrame(Duration.seconds(0),
                        new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent actionEvent) {
                                GameMove gameMove = gameMoveList.get(i.get());
                                String symbol = gameMove.getPlayerType().name();
                                String buttonId = gameMove.getLocation();
                                Integer value =gameMove.getValue();
                                Scene scene = HelloApplication.getMainScene();
                                Button button = (Button) scene.lookup("#" + buttonId);
                                button.setText(String.valueOf(value)); // Convert Integer to String before setting as text
                                i.set(i.get() + 1);
                            }
                        }
                ),
                new KeyFrame(Duration.seconds(1))
        );

        replayer.setCycleCount(gameMoveList.size());
        replayer.play();
    }
    public static void refreshGameBoard(GameState gameState) {
        restoreGameState(gameState);
    }

    public static void restoreGameState(GameState gameState) {
        numberOfMoves = gameState.getNumberOfMoves();
        playerTurn = gameState.getCurrentPlayer();
        Boolean endGame = gameState.getEndGame();
        System.out.println("playeer turn is:"+playerTurn);
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
                    buttonCard[i][j].setText(cardValuesToSave[i][j]);
                } else {
                    buttonCard[i][j].setText("");
                }
            }
        }

    }
    public void cardCreate(Event event) {
        Button buttonPressed = (Button) event.getSource();

        Card sampleCard = GameStateUtils.getNextCard();

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

        GameMove gameMove = new GameMove(playerTurn, buttonPressed.getId(),currentValue, LocalDateTime.now());
        XmlUtils.saveGameMove(gameMove);

        SaveNewGameMoveThread saveNewGameMoveThread
                = new SaveNewGameMoveThread(gameMove);
        Thread staterThread = new Thread(saveNewGameMoveThread);
        staterThread.start();

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
        FileUtils.saveGameToFile(gameBoard, numberOfMoves, playerTurn,buttonCard);
    }

    public void loadGame() {
        GameState recoveredGameState = FileUtils.loadGameStateFromFile();
        restoreGameState(recoveredGameState);
    }


    public void generateDocumentation() {
        DocumentationUtils.generateHtmlDocumentationFile();
    }
    public void sendChatMessage() {
        String chatMessage = chatTextField.getText();

        try {
            remoteChatService.sendChatMessage(HelloApplication.currentUserRole.name() + ": " + chatMessage);

            chatTextField.clear();

            List<String> chatMessages = remoteChatService.getAllChatMessages();

            chatMessagesTextArea.clear();

            for (String message : chatMessages) {
                chatMessagesTextArea.appendText(message + "\n");
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }



    public static void startRmiRemoteChatServer()
    {
        Integer rmiPort = ConfigurationReader.readIntegerConfigurationValueForKey(ConfigurationKey.RMI_PORT);
        Integer randomPortHint = ConfigurationReader.readIntegerConfigurationValueForKey(ConfigurationKey.RANDOM_PORT_HINT);

        try {
            Registry registry = LocateRegistry.createRegistry(rmiPort);
            remoteChatService = new RemoteChatServiceImpl();
            RemoteChatService skeleton = (RemoteChatService) UnicastRemoteObject.exportObject(remoteChatService,randomPortHint);
            registry.rebind(RemoteChatService.REMOTE_OBJECT_NAME, skeleton);
            System.err.println("Object registered in RMI registry");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    private static void startRmiClient() {
        Integer rmiPort = ConfigurationReader.readIntegerConfigurationValueForKey(ConfigurationKey.RMI_PORT);
        String host = ConfigurationReader.readStringConfigurationValueForKey(ConfigurationKey.HOST);
        try {
            Registry registry = LocateRegistry.getRegistry(host, rmiPort);
            remoteChatService = (RemoteChatService) registry.lookup(RemoteChatService.REMOTE_OBJECT_NAME);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }
}
