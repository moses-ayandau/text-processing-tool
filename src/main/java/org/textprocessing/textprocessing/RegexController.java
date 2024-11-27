package org.textprocessing.textprocessing;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class RegexController {

    @FXML private TableView<RegexExample> examplesTable;
    @FXML private TableColumn<RegexExample, String> regexColumn;
    @FXML private TableColumn<RegexExample, String> descriptionColumn;
    @FXML
    private ListView<String> examplesList;

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

        ObservableList<String> examples = FXCollections.observableArrayList(
                new RegexExample("\\d+", "Matches one or more digits.", "12345").toString(),
                new RegexExample("\\w+", "Matches one or more word characters.", "hello_world").toString(),
                new RegexExample("[A-Za-z]+", "Matches alphabetic characters only.", "HelloWorld").toString(),
                new RegexExample("\\b[A-Z][a-z]*\\b", "Matches words starting with a capital letter.", "Apple, Banana").toString(),
                new RegexExample("\\s", "Matches whitespace characters.", "space\ttab\nnewline").toString(),
                new RegexExample("\\d{4}", "Matches exactly four digits.", "2024").toString(),
                new RegexExample("\\b\\d{1,3}(,\\d{3})*(\\.\\d+)?\\b", "Matches numbers with commas (e.g., 1,000 or 1,000.23).", "1,000,000").toString(),
                new RegexExample("\\w+@\\w+\\.\\w+", "Matches a basic email address.", "example@test.com").toString(),
                new RegexExample("^\\d{3}-\\d{2}-\\d{4}$", "Matches a US Social Security number format.", "123-45-6789").toString(),
                new RegexExample("(?i)hello", "Matches 'hello' in a case-insensitive manner.", "Hello, HELLO, hello").toString(),
                new RegexExample("(\\d{3})-(\\d{3})-(\\d{4})", "Matches a phone number format.", "123-456-7890").toString(),
                new RegexExample("^https?://\\S+$", "Matches URLs starting with 'http' or 'https'.", "https://example.com").toString(),
                new RegexExample("\\b(?=\\w{6,})(?=.*\\d)(?=.*[A-Z])(?=.*[a-z]).*\\b", "Matches passwords with at least 6 characters, one digit, one uppercase, and one lowercase.", "Password1").toString(),
                new RegexExample("([01]?\\d|2[0-3]):[0-5]\\d", "Matches 24-hour time format.", "23:59").toString(),
                new RegexExample("([0-9a-fA-F]{2}:){5}[0-9a-fA-F]{2}", "Matches a MAC address.", "00:1A:2B:3C:4D:5E").toString()
        );


        examplesList.setItems(examples);
    }

    private void updateButtonState() {
        boolean isRegexFilled = !regexInput.getText().trim().isEmpty();
        boolean isTextFilled = !textInput.getText().trim().isEmpty();

        addReplacementToCollectionButton.setDisable(!(isRegexFilled && isTextFilled));
    }



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

    public void addTextAndPatternToCollection() {
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

    @FXML
    public void saveToFile() {
        String regex = regexInput.getText().trim();
        String text = textInput.getText().trim();
        String replacement = replaceInput.getText().trim();
        String result = resultOutput.getText().trim();

        if (regex.isEmpty() || text.isEmpty() || replacement.isEmpty() || result.isEmpty()) {
            showAlert("Error", "All fields (Regex, Text, Replacement, Result) must be filled to save.", Alert.AlertType.ERROR);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Regex Details");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("Regex Pattern: " + regex + "\n");
                writer.write("Input Text: " + text + "\n");
                writer.write("Replacement Text: " + replacement + "\n");
                writer.write("Result Text: " + result + "\n");
                showAlert("Success", "File saved successfully to: " + file.getAbsolutePath(), Alert.AlertType.INFORMATION);
            } catch (IOException e) {
                showAlert("Error", "Failed to save the file: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void uploadFileAsInput() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Text File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );


        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {

                StringBuilder content = new StringBuilder();
                Scanner scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    content.append(scanner.nextLine()).append("\n");
                }
                scanner.close();


                textInput.setText(content.toString());
            } catch (IOException e) {
                showAlert("Error", "Failed to read the file: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }


    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
