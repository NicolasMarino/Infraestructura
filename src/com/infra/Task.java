package com.infra;

public class Task {
    private String name;
    private Integer executionTime;
    private Resource resource;

    public Task(String name, Integer executionTime, Resource resource) {
        this.name = name;
        this.executionTime = executionTime;
        this.resource = resource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Integer executionTime) {
        this.executionTime = executionTime;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
