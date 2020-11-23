package com.infra;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Process {

    private String name;
    private Status status;
    private Integer executionTimeout;
    private Integer availableTimeout;
    private List<Task> taskList;
    private Permissions permission;
    private Resource actualResource;

    public Process(String name, Status status, Integer executionTimeout, List<Task> taskList, Permissions permission) {
        this.name = name;
        this.status = status;
        this.executionTimeout = executionTimeout;
        this.availableTimeout = executionTimeout;
        this.taskList = taskList;
        this.permission = permission;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getExecutionTimeout() {
        return executionTimeout;
    }

    public void setExecutionTimeout(Integer executionTimeout) {
        this.executionTimeout = executionTimeout;
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    public Permissions getPermission() {
        return permission;
    }

    public void setPermission(Permissions permission) {
        this.permission = permission;
    }

    public boolean isAvailable() {
        return Status.AVAILABLE.equals(this.status);
    }

    public boolean isRunning() {
        return Status.RUNNING.equals(this.status);
    }

    public boolean isLocked() {
        return Status.LOCKED.equals(this.status);
    }

    public Resource getActualResource() {
        return actualResource;
    }

    public void setActualResource(Resource actualResource) {
        this.actualResource = actualResource;
    }

    public Task getTaskById(Integer pos) {
        return this.getTaskList().get(pos);
    }

    public Integer getAvailableTimeout() {
        return availableTimeout;
    }

    public void setAvailableTimeout(Integer availableTimeout) {
        this.availableTimeout = availableTimeout;
    }

    Resource getResourceByTaskId(Integer pos) {
        return this.getTaskList().get(pos).getResource();
    }

    public void run(User user) {
        this.setStatus(Status.RUNNING);
    }

    public void terminate() {
        this.setAvailableTimeout(this.getExecutionTimeout());
        this.setStatus(Status.AVAILABLE);
    }

    public boolean validateActionPermission(User user) {
        return user.getRole().getPermissionActionList().contains(this.getPermission());
    }

    public HashMap<Boolean, String> validateResourcesPermission(User user) {
        HashMap<Boolean, String> map = new HashMap<Boolean, String>();

        for (Task t : this.getTaskList()) {
            if (!(user.getRole().getPermissionResourceList().contains(t.getResource()))) {
                map.put(false, t.getResource().getName());
                return map;
            }
        }
        map.put(true, "");
        return map;
    }

    public void giveBackResource() {
        this.setActualResource(null);
    }

    public void sortTaskListByExecutionTime(){
        this.taskList.sort(Comparator.comparing(Task::getExecutionTime));
    }

}
