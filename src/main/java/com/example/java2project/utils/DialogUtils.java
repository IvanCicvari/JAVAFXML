package com.example.java2project.utils;

import javafx.scene.control.Alert;

public class DialogUtils {
    public static void showInformationDialog(String title, String contentText) {
        showMessageDialog(title, contentText, Alert.AlertType.INFORMATION);
    }
    public static void showErrorDialog(String title, String contentText) {
        showMessageDialog(title, contentText, Alert.AlertType.ERROR);
    }
    private static void showMessageDialog(String title, String contentText, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
    public static void showDialog(Alert.AlertType alertType,
                                  String title, String message)
    {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
