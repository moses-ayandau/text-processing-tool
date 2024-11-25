package org.textprocessing.textprocessing;


import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.*;

public class DataController {

    @FXML private TextField keyField;
    @FXML private TextField valueField;
    @FXML private ListView<String> dataList;

    private final Map<String, CustomData> dataMap = new HashMap<>();
    private final ObservableList<String> observableList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        dataList.setItems(observableList);
    }

    // Add or Update a data entry
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

    // Delete a data entry
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

