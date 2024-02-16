package com.kanban.model;

public class Subtask extends Task {

    private int relatedEpicId;

    public Subtask(String name, String description, int relatedEpicId) {
        super(name, description);
        this.relatedEpicId = relatedEpicId;
    }

    public void setRelatedEpicId(int relatedEpicId) {
        this.relatedEpicId = relatedEpicId;
    }

    public int getRelatedEpicId() {
        return relatedEpicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() + '\'' +
                "relatedEpicId=" + relatedEpicId +
                '}';
    }
}
