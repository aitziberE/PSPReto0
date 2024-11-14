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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.scene.control.ButtonType;

/**
 *
 * @author Ander
 * @author Aitziber
 */
public class ProcessManager {
    private ListView<ProcessInfo> processListView;
    private ObservableList<ProcessInfo> processList;
    private TextField searchField;
    private List<Integer> pausedProcesses = new ArrayList<>();

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
        processListView.setItems(processList.filtered(process ->
            process.getName().toLowerCase().contains(filter) || String.valueOf(process.getPid()).contains(filter)
        ));
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
    
   public void killProcess() {
        ProcessInfo selectedProcess = processListView.getSelectionModel().getSelectedItem();
        if (selectedProcess != null) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Detener proceso");
            confirmationAlert.setHeaderText("¿Estás seguro de que deseas detener este proceso?");
            confirmationAlert.setContentText("Proceso: " + selectedProcess.getName() + "\nPID: " + selectedProcess.getPid());

            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    String command = String.format("taskkill /F /PID %d", selectedProcess.getPid());
                    Process process = Runtime.getRuntime().exec(command);
                    process.waitFor();

                    // Elimina el proceso de la lista
                    processList.remove(selectedProcess);

                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Proceso detenido: " + selectedProcess.getName());
                    alert.showAndWait();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Error al detener el proceso.");
                    alert.showAndWait();
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Por favor, selecciona un proceso.");
            alert.showAndWait();
        }
    }
   
     public void pauseResumeProcess() {
        ProcessInfo selectedProcess = processListView.getSelectionModel().getSelectedItem();
        if (selectedProcess != null) {
            int pid = selectedProcess.getPid();
            boolean isPaused = pausedProcesses.contains(pid);
            String action = isPaused ? "reanudar" : "pausar";
            String command = String.format("pssuspend %s %d", isPaused ? "-r" : "", pid);


            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle(action.substring(0, 1).toUpperCase() + action.substring(1) + " proceso");
            confirmationAlert.setHeaderText("¿Estás seguro de que deseas " + action + " este proceso?");
            confirmationAlert.setContentText("Proceso: " + selectedProcess.getName() + "\nPID: " + pid);

            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    Process process = Runtime.getRuntime().exec(command);
                    process.waitFor();

                    if (isPaused) {
                        pausedProcesses.remove(Integer.valueOf(pid));
                    } else {
                        pausedProcesses.add(pid);
                    }

                    String message = "Proceso " + (isPaused ? "reanudado: " : "pausado: ") + selectedProcess.getName();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
                    alert.showAndWait();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Error al " + action + " el proceso.");
                    alert.showAndWait();
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Por favor, selecciona un proceso.");
            alert.showAndWait();
        }
    }
}