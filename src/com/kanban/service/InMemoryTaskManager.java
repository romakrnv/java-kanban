package com.kanban.service;

import com.kanban.model.Epic;
import com.kanban.model.Subtask;
import com.kanban.model.Task;
import com.kanban.storage.Storage;
import com.kanban.model.TaskStatus;

import java.util.ArrayList;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int id = 1;
    public final HistoryManager historyManager;
    protected final Storage storage;

    public InMemoryTaskManager(HistoryManager historyManager, Storage storage) {
        this.historyManager = historyManager;
        this.storage = storage;
    }

    @Override
    public List<Task> getAllTasks() {
        return storage.getTasks();
    }

    @Override
    public void removeAllTasks() {
        storage.clearTasks();
    }

    @Override
    public Task getTask(int id) {
        if (storage.getTask(id) == null) {
            return null;
        }
        historyManager.add(storage.getTask(id));
        return storage.getTask(id);
    }

    @Override
    public Task addTask(Task task) {
        if (task.getId() == 0) {
            task.setId(generateId());
        }
        storage.add(task.getId(), task);
        return task;
    }

    @Override
    public void updateTask(Task task) {
        storage.add(task.getId(), task);
    }

    @Override
    public void removeTask(int id) {
        storage.removeTask(id);
    }

    @Override
    public List<Epic> getAllEpics() {
        return storage.getEpics();
    }

    @Override
    public void removeAllEpics() {
        storage.clearEpics();
        storage.clearSubtasks();
    }

    @Override
    public Epic getEpic(int id) {
        historyManager.add(storage.getEpic(id));
        return storage.getEpic(id);
    }

    @Override
    public Epic addEpic(Epic epic) {
        if (epic.getId() == 0) {
            epic.setId(generateId());
        }
        storage.add(epic.getId(), epic);
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        epic.setSubtasksIds(storage.getEpic(epic.getId()).getSubtasksIds());
        storage.add(epic.getId(), epic);
        checkEpicStatus(epic);
    }

    @Override
    public void removeEpic(int id) {
        if (storage.getEpic(id) == null) {
            return;
        }
        for (int subId : storage.getEpic(id).getSubtasksIds()) {
            storage.removeSubtask(subId);
        }
        storage.removeEpic(id);
    }

    @Override
    public List<Subtask> getAllEpicsSubtasks(int id) {
        List<Subtask> subtasks = new ArrayList<>();
        for (int subId : storage.getEpic(id).getSubtasksIds()) {
            subtasks.add(storage.getSubtask(subId));
        }
        return subtasks;
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return storage.getSubtasks();
    }

    @Override
    public void removeAllSubtasks(int epicId) {
        Epic epic = storage.getEpic(epicId);
        for (int subId : epic.getSubtasksIds()) {
            storage.removeSubtask(subId);
        }
        epic.getSubtasksIds().clear();
        epic.setStatus(TaskStatus.NEW);
    }

    @Override
    public Subtask getSubtask(int id) {
        historyManager.add(storage.getSubtask(id));
        return storage.getSubtask(id);
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        if (storage.getEpic(subtask.getEpicId()) == null) {
            return null;
        }
        if (subtask.getId() == 0) {
            subtask.setId(generateId());
        }
        storage.add(subtask.getId(), subtask);
        storage.getEpic(subtask.getEpicId()).getSubtasksIds().add(subtask.getId());
        checkEpicStatus(storage.getEpic(subtask.getEpicId()));
        return subtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtask.setEpicId(storage.getSubtask(subtask.getId()).getEpicId());
        storage.add(subtask.getId(), subtask);
        checkEpicStatus(storage.getEpic(subtask.getEpicId()));
    }

    @Override
    public void removeSubtask(int id) {
        if (storage.getSubtask(id) == null) {
            return;
        }
        storage.getEpic(storage.getSubtask(id).getEpicId()).getSubtasksIds().remove((Integer) id);
        checkEpicStatus(storage.getEpic(storage.getSubtask(id).getEpicId()));
        storage.removeSubtask(id);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private int generateId() {
        return id++;
    }

    public void checkEpicStatus(Epic epic) {
        if (epic.getSubtasksIds().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        TaskStatus taskStatus = storage.getSubtask(epic.getSubtasksIds().get(0)).getStatus();
        for (int subId : epic.getSubtasksIds()) {
            if (storage.getSubtask(subId).getStatus() == TaskStatus.IN_PROGRESS) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
                return;
            }
            if (taskStatus != storage.getSubtask(subId).getStatus()) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
                return;
            }
            taskStatus = storage.getSubtask(subId).getStatus();
        }
        if (taskStatus.equals(TaskStatus.NEW)) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.DONE);
        }
    }
}
