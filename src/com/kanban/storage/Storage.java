package com.kanban.storage;

import com.kanban.model.Epic;
import com.kanban.model.Subtask;
import com.kanban.model.Task;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Storage {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();

    public void add(int id, Task task) {
        tasks.put(id, task);
    }

    public void add(int id, Epic epic) {
        epics.put(id, epic);
    }

    public void add(int id, Subtask subtask) {
        subtasks.put(id, subtask);
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public Collection<Task> getTasks() {
        return tasks.values();
    }

    public Collection<Epic> getEpics() {
        return epics.values();
    }

    public Collection<Subtask> getSubtasks() {
        return subtasks.values();
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearEpics() {
        epics.clear();
    }

    public void clearSubtasks() {
        subtasks.clear();
    }

    public void removeTask(int id) {
        tasks.remove(id);
    }

    public void removeEpic(int id) {
        epics.remove(id);
    }

    public void removeSubtask(int id) {
        subtasks.remove(id);
    }
}
