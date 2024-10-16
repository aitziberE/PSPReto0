/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author Ander
 * @author Aitziber
 */
public class ProcessManager {
    private ListView<ProcessInfo> processListView;
    private ObservableList<ProcessInfo> processList;
    private TextField searchField;

    public ProcessManager(ListView<ProcessInfo> processListView, TextField searchField) {
        this.processListView = processListView;
        this.searchField = searchField;
        this.processList = FXCollections.observableArrayList();
        processListView.setItems(processList);
    }

    public void loadProcesses() {
        processList.clear();
        try {
            ProcessBuilder builder = new ProcessBuilder("tasklist","/fo", "csv");
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String name = parts[0].replace("\"", "");
                int pid = Integer.parseInt(parts[1].replace("\"", ""));
                processList.add(new ProcessInfo(name, pid));
            }       
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void filterProcesses() {
        String filter = searchField.getText().toLowerCase();
        processListView.setItems(processList.filtered(process -> {
            return String.valueOf(process.getPid()).contains(filter);
        }));
    }

    public void increasePriority() {
        ProcessInfo selectedProcess = processListView.getSelectionModel().getSelectedItem();
        if (selectedProcess != null) {
            try {
                String command = String.format("wmic process where ProcessId=%d call setpriority 13", selectedProcess.getPid());
                Runtime.getRuntime().exec(command);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Prioridad aumentada para el proceso: " + selectedProcess.getName());
                alert.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Por favor, selecciona un proceso.");
            alert.showAndWait();
        }
    }
}
