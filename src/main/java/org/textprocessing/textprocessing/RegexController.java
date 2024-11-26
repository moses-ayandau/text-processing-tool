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
    @FXML private TextArea resultOutput;
    @FXML private Button addReplacementToCollectionButton;

    @FXML private TextField keyField;
    @FXML private TextArea valueField;
    @FXML private ListView<String> dataList;

    // HashMap for Data storage
    private final Map<String, CustomData> dataMap = new HashMap<>();
    private final ObservableList<String> observableList = FXCollections.observableArrayList();

    private String lastMatch;

    @FXML
    public void initialize() {
        dataList.setItems(observableList);

        addReplacementToCollectionButton.setDisable(true);

        regexInput.textProperty().addListener((observable, oldValue, newValue) -> updateButtonState());
        textInput.textProperty().addListener((observable, oldValue, newValue) -> updateButtonState());
    }

    private void updateButtonState() {
        boolean isRegexFilled = !regexInput.getText().trim().isEmpty();
        boolean isTextFilled = !textInput.getText().trim().isEmpty();

        // Enable the button only if both fields are filled
        addReplacementToCollectionButton.setDisable(!(isRegexFilled && isTextFilled));
    }


    // ==== Regex Operations ====
    public void matchRegex() {
        String regex = regexInput.getText().trim();
        String text = textInput.getText().trim();

        if (regex.isEmpty() || text.isEmpty()) {
            showAlert("Error", "Regex pattern and text cannot be empty.", Alert.AlertType.ERROR);
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
            showAlert("Error", "Invalid regex pattern: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void replaceRegex() {
        String regex = regexInput.getText().trim();
        String text = textInput.getText().trim();
        String replacement = replaceInput.getText();

        if (regex.isEmpty() || text.isEmpty()) {
            showAlert("Error", "Regex pattern and text cannot be empty.", Alert.AlertType.ERROR);
            return;
        }

        try {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(text);

            String lastReplacement;
            if (matcher.find()) {
                // Store the first match and its replacement for collection
                lastMatch = matcher.group();
                lastReplacement = replacement;

                // Enable the button for adding to collection
                addReplacementToCollectionButton.setDisable(false);
            } else {
                lastMatch = null;
                lastReplacement = null;
                addReplacementToCollectionButton.setDisable(true);
            }

            String replacedText = matcher.replaceAll(replacement);
            resultOutput.setText(replacedText);
        } catch (Exception e) {
            showAlert("Error", "Invalid regex pattern: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // ==== Data Management Operations ====
    public void addReplacementToCollection() {
        String text = textInput.getText().trim();

        if (lastMatch == null || text == null) {
            showAlert("Error", "No match and textment available to add.", Alert.AlertType.ERROR);
            return;
        }

        CustomData newData = new CustomData(lastMatch, text);
        dataMap.put(lastMatch, newData);
        showAlert("Success", "Item added to the collections", Alert.AlertType.CONFIRMATION);
        refreshList();

        // Disable the button after adding
        addReplacementToCollectionButton.setDisable(true);

        // Clear temporary storage
        lastMatch = null;
        text = null;
    }

    public void addOrUpdateData() {
        String key = keyField.getText().trim();
        String value = valueField.getText().trim();

        if (key.isEmpty() || value.isEmpty()) {
            showAlert("Error", "Key or Value cannot be empty.", Alert.AlertType.ERROR);
            return;
        }

        CustomData newData = new CustomData(key, value);
        dataMap.put(key, newData);
        refreshList();
    }

    public void deleteData() {
        String selected = dataList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "No item selected for deletion. Please click on an item to delete", Alert.AlertType.INFORMATION);
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

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
