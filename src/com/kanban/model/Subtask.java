package com.kanban.model;

public class Subtask extends Task {
    private int epicId;

    public Subtask(int epicId) {
        this.epicId = epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() + '\'' +
                "relatedEpicId=" + epicId +
                '}';
    }
}
