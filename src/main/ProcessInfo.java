/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 *
 * @author Ander
 * @author Aitziber
 */
public class ProcessInfo {
    private String name;
    private int pid;

    public ProcessInfo(String name, int pid) {
        this.name = name;
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public int getPid() {
        return pid;
    }

    @Override
    public String toString() {
        return name + " (PID: " + pid + ")";
    }
}

