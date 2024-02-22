package com.kanban.manager;

import com.kanban.model.Epic;
import com.kanban.model.Subtask;
import com.kanban.model.Task;

import java.util.HashMap;

public class Storage {
    private final HashMap<Integer, Task> taskStorage = new HashMap<>();
    private final HashMap<Integer, Epic> epicStorage = new HashMap<>();
    private final HashMap<Integer, Subtask> subtaskStorage = new HashMap<>();

    public void add(int id, Task task) {
        taskStorage.put(id, task);
    }

    public void add(int id, Epic epic) {
        epicStorage.put(id, epic);
    }

    public void add(int id, Subtask subtask) {
        subtaskStorage.put(id, subtask);
    }

    public Task getTask(int id) {
        return taskStorage.get(id);
    }

    public Epic getEpic(int id) {
        return epicStorage.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtaskStorage.get(id);
    }

    public HashMap<Integer, Task> getTaskStorage() {
        return taskStorage;
    }

    public HashMap<Integer, Epic> getEpicStorage() {
        return epicStorage;
    }

    public HashMap<Integer, Subtask> getSubtaskStorage() {
        return subtaskStorage;
    }
}
