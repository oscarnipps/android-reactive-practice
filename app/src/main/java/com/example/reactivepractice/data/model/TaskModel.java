package com.example.reactivepractice.data.model;

public class TaskModel {
    private int priority;
    private boolean isComplete;
    private String description;

    public TaskModel(int priority, boolean isComplete, String description) {
        this.priority = priority;
        this.isComplete = isComplete;
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
