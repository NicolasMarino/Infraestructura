package com.infra;

import java.util.List;

public class Program {
    private String name;
    private List<Process> processList;

    public Program(String name, List<Process> processList) {
        this.name = name;
        this.processList = processList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Process> getProcessList() {
        return processList;
    }

    public void setProcessList(List<Process> processList) {
        this.processList = processList;
    }
}
