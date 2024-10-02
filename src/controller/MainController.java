/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import main.ProcessInfo;
import main.ProcessManager;

/**
 *
 * @author Ander
 * @author Aitziber
 */
public class MainController {
    @FXML
    private ListView<ProcessInfo> processListView;
    @FXML
    private TextField searchField;
    @FXML
    private Button increasePriorityButton;

    private ProcessManager processManager;

    @FXML
    public void initialize() {
        processManager = new ProcessManager(processListView, searchField);
        processManager.loadProcesses();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> processManager.filterProcesses());
    }

    @FXML
    public void increasePriority(ActionEvent event) {
        processManager.increasePriority();
    }
}
