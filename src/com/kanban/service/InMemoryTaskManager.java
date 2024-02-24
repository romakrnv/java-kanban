package com.kanban.service;

import com.kanban.model.Epic;
import com.kanban.model.Subtask;
import com.kanban.model.Task;
import com.kanban.storage.Storage;
import com.kanban.model.TaskStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private static int id = 1;
    private final HistoryManager historyManager;
    private final Storage storage;

    public InMemoryTaskManager(HistoryManager historyManager, Storage storage) {
        this.historyManager = historyManager;
        this.storage = storage;
    }

    @Override
    public Collection<Task> getAllTasks() {
        return storage.getTasks();
    }

    @Override
    public void removeAllTask() {
        storage.clearTasks();
    }

    @Override
    public Task getTask(int id) {
        historyManager.add(storage.getTask(id));
        return storage.getTask(id);
    }

    @Override
    public Task createTask(Task task) {
        task.setId(generateId());
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
    public Collection<Epic> getAllEpic() {
        return storage.getEpics();
    }

    @Override
    public void removeAllEpic() {
        storage.clearEpics();
        storage.clearSubtasks();
    }

    @Override
    public Epic getEpic(int id) {
        historyManager.add(storage.getEpic(id));
        return storage.getEpic(id);
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        storage.add(epic.getId(), epic);
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        epic.setSubtaskId(storage.getEpic(epic.getId()).getSubtaskId());
        storage.add(epic.getId(), epic);
        checkEpicStatus(epic);
    }

    @Override
    public void removeEpic(int id) {
        if (storage.getEpic(id) == null) {
            return;
        }
        for (int subId : storage.getEpic(id).getSubtaskId()) {
            storage.removeSubtask(subId);
        }
        storage.removeEpic(id);
    }

    @Override
    public Collection<Subtask> getAllEpicsSubtasks(int id) {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (int subId : storage.getEpic(id).getSubtaskId()) {
            subtasks.add(storage.getSubtask(subId));
        }
        return subtasks;
    }

    @Override
    public Collection<Subtask> getAllSubtask() {
        return storage.getSubtasks();
    }

    @Override
    public void removeAllSubtask(int relatedEpicId) {
        for (int subId : storage.getEpic(relatedEpicId).getSubtaskId()) {
            storage.removeSubtask(subId);
        }
        storage.getEpic(relatedEpicId).getSubtaskId().clear();
        storage.getEpic(relatedEpicId).setStatus(TaskStatus.NEW);
    }

    @Override
    public Subtask getSubtask(int id) {
        historyManager.add(storage.getSubtask(id));
        return storage.getSubtask(id);
    }

    @Override
    public Subtask createSubTask(Subtask subtask) {
        if (storage.getEpic(subtask.getEpicId()) == null) {
            return null;
        } else {
            subtask.setId(generateId());
            storage.add(subtask.getId(), subtask);
            storage.getEpic(subtask.getEpicId()).getSubtaskId().add(subtask.getId());
            checkEpicStatus(storage.getEpic(subtask.getEpicId()));
            return subtask;
        }
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
        storage.getEpic(storage.getSubtask(id).getEpicId()).getSubtaskId().remove((Integer) id);
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

    private void checkEpicStatus(Epic epic) {
        if (epic.getSubtaskId().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        TaskStatus taskStatus = storage.getSubtask(epic.getSubtaskId().get(0)).getStatus();
        for (int subId : epic.getSubtaskId()) {
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
