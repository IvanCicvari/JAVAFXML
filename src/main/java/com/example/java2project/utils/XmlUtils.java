package com.example.java2project.utils;

import com.example.java2project.PlayerType;
import com.example.java2project.models.GameMove;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class XmlUtils {

    private static final String FILENAME = "gameMoves.xml";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public static void saveGameMove(GameMove newGameMove) {

        List<GameMove> gameMoveList = getAllGameMoves();
        gameMoveList.add(newGameMove);

        try {

            Document document = createDocument("gameMoves");

            for(GameMove gameMoveElement : gameMoveList) {
                Element gameMove = document.createElement("gameMove");
                document.getDocumentElement().appendChild(gameMove);

                gameMove.appendChild(createElement(document, "playerType", gameMoveElement.getPlayerType().name()));
                gameMove.appendChild(createElement(document, "location", gameMoveElement.getLocation()));
                gameMove.appendChild(createElement(document, "value", gameMoveElement.getValue()));
                gameMove.appendChild(createElement(document, "dateTime",
                        gameMoveElement.getDateTime().format(formatter)));
            }

            saveDocument(document, FILENAME);
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    public static void createNewFile() {
        try {
            Document document = createDocument("gameMoves");
            saveDocument(document, FILENAME);
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    private static Document createDocument(String element) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation domImplementation = builder.getDOMImplementation();
        return domImplementation.createDocument(null, element, null);
    }

    private static Node createElement(Document document, String tagName, String data) {
        Element element = document.createElement(tagName);
        Text text = document.createTextNode(data);
        element.appendChild(text);
        return element;
    }
    private static Node createElement(Document document, String tagName, Integer data) {
        return createElement(document, tagName, String.valueOf(data));
    }
    private static void saveDocument(Document document, String fileName) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(document), new StreamResult(new File(FILENAME)));
    }

    public static List<GameMove> getAllGameMoves() {

        List<GameMove> gameMoveList = new ArrayList<>();

        File xmlFile = new File(FILENAME);

        if(xmlFile.exists()) {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(xmlFile);
                Element gameMovesDocumentElement = document.getDocumentElement();

                NodeList gameMovesNodeList = gameMovesDocumentElement.getChildNodes();

                for (int i = 0; i < gameMovesNodeList.getLength(); i++) {
                    PlayerType playerType = PlayerType.USA;
                    String location = "";
                    int value = 0;
                    LocalDateTime localDateTime = LocalDateTime.now();

                    Node gameMoveNode = gameMovesNodeList.item(i);

                    if (gameMoveNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element gameMoveElement = (Element) gameMoveNode;

                        NodeList gameMoveElementNodeList = gameMoveElement.getChildNodes();

                        for (int j = 0; j < gameMoveElementNodeList.getLength(); j++) {

                            if (gameMoveElementNodeList.item(j).getNodeType() == Node.ELEMENT_NODE) {
                                Element gameMoveChildElement = (Element) gameMoveElementNodeList.item(j);

                                switch (gameMoveChildElement.getTagName()) {
                                    case "playerType" -> playerType = PlayerType.valueOf(gameMoveChildElement.getTextContent());
                                    case "location" -> location = gameMoveChildElement.getTextContent();
                                    case "value" -> value = Integer.parseInt(gameMoveChildElement.getTextContent());
                                    case "dateTime" -> localDateTime = LocalDateTime.parse(
                                            gameMoveChildElement.getTextContent(), formatter);
                                }
                            }
                            else {
                                continue;
                            }
                        }
                    }
                    else {
                        continue;
                    }

                    GameMove newGameMove = new GameMove(playerType, location,value, localDateTime);
                    gameMoveList.add(newGameMove);
                }
            } catch (ParserConfigurationException | SAXException | IOException ex) {
                ex.printStackTrace();
            }
        }

        return gameMoveList;
    }

}
