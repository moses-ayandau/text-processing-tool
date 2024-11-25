package org.textprocessing.textprocessing;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexController {

    // Regex Module Components
    @FXML private TextField regexInput;
    @FXML private TextArea textInput;
    @FXML private TextField replaceInput;
    @FXML private TextArea  resultOutput;

    // Data Management Module Components
    @FXML private TextField keyField;
    @FXML private TextField valueField;
    @FXML private ListView<String> dataList;

    // Data storage
    private final Map<String, CustomData> dataMap = new HashMap<>();
    private final ObservableList<String> observableList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialize the list view for the data management module
        dataList.setItems(observableList);
    }

    // ==== Regex Operations ====
    public void matchRegex() {
        String regex = regexInput.getText().trim();
        String text = textInput.getText().trim();

        if (regex.isEmpty() || text.isEmpty()) {
            showAlert("Error", "Regex pattern and text cannot be empty.");
            return;
        }

        try {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(text);

            StringBuilder matches = new StringBuilder();
            while (matcher.find()) {
                matches.append("Match: '").append(matcher.group())
                        .append("' at positions [").append(matcher.start())
                        .append(", ").append(matcher.end()).append("]\n");
            }

            resultOutput.setText(matches.length() > 0 ? matches.toString() : "No matches found.");
        } catch (Exception e) {
            showAlert("Error", "Invalid regex pattern: " + e.getMessage());
        }
    }

    public void replaceRegex() {
        String regex = regexInput.getText().trim();
        String text = textInput.getText().trim();
        String replacement = replaceInput.getText();

        if (regex.isEmpty() || text.isEmpty()) {
            showAlert("Error", "Regex pattern and text cannot be empty.");
            return;
        }

        try {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(text);

            String replacedText = matcher.replaceAll(replacement);
            resultOutput.setText(replacedText);
        } catch (Exception e) {
            showAlert("Error", "Invalid regex pattern: " + e.getMessage());
        }
    }

    // ==== Data Management Operations ====
    public void addOrUpdateData() {
        String key = keyField.getText().trim();
        String value = valueField.getText().trim();

        if (key.isEmpty() || value.isEmpty()) {
            showAlert("Error", "Key and Value cannot be empty.");
            return;
        }

        CustomData newData = new CustomData(key, value);
        dataMap.put(key, newData);
        refreshList();
    }

    public void deleteData() {
        String selected = dataList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "No item selected for deletion.");
            return;
        }

        String key = selected.split(",")[0].split(":")[1].trim();
        dataMap.remove(key);
        refreshList();
    }

    private void refreshList() {
        observableList.clear();
        for (CustomData data : dataMap.values()) {
            observableList.add(data.toString());
        }
    }

    // ==== Utility Methods ====
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}