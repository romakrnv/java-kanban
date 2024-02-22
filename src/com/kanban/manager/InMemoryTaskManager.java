package com.kanban.manager;

import com.kanban.model.enums.Status;
import com.kanban.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private static int id = 1;
    private final HistoryManager historyManager;
    private final Storage storage = new Storage();

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public Collection<Task> getAllTasks() {
        return storage.getTaskStorage().values();
    }

    @Override
    public void removeAllTask() {
        storage.getTaskStorage().clear();
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
        storage.getTaskStorage().remove(id);
    }

    @Override
    public Collection<Epic> getAllEpic() {
        return storage.getEpicStorage().values();
    }

    @Override
    public void removeAllEpic() {
        storage.getEpicStorage().clear();
        storage.getSubtaskStorage().clear();
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
        storage.getEpicStorage().put(epic.getId(), epic);
        checkEpicStatus(epic);
    }

    @Override
    public void removeEpic(int id) {
        if (storage.getEpicStorage().containsKey(id)) {
            for (int subId : storage.getEpicStorage().get(id).getSubtasksId()) {
                storage.getSubtaskStorage().remove(subId);
            }
            storage.getEpicStorage().remove(id);
        }
    }

    @Override
    public Collection<Subtask> getAllEpicsSubtasks(int id) {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (int subId : storage.getEpicStorage().get(id).getSubtasksId()) {
            subtasks.add(storage.getSubtaskStorage().get(subId));
        }
        return subtasks;
    }

    @Override
    public Collection<Subtask> getAllSubtask() {
        return storage.getSubtaskStorage().values();
    }

    @Override
    public void removeAllSubtask(int relatedEpicId) {
        for (int subId : storage.getEpicStorage().get(relatedEpicId).getSubtasksId()) {
            storage.getSubtaskStorage().remove(subId);
        }
        storage.getEpic(relatedEpicId).getSubtasksId().clear();
        storage.getEpic(relatedEpicId).setStatus(Status.NEW);
    }

    @Override
    public Subtask getSubtask(int id) {
        historyManager.add(storage.getSubtask(id));
        return storage.getSubtask(id);
    }

    @Override
    public Subtask createSubTask(Subtask subtask) {
        subtask.setId(generateId());
        storage.add(subtask.getId(), subtask);
        storage.getEpic(subtask.getRelatedEpicId()).getSubtasksId().add(subtask.getId());
        checkEpicStatus(storage.getEpic(subtask.getRelatedEpicId()));
        return subtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtask.setRelatedEpicId(storage.getSubtask(subtask.getId()).getRelatedEpicId());
        storage.add(subtask.getId(), subtask);
        checkEpicStatus(storage.getEpic(subtask.getRelatedEpicId()));
    }

    @Override
    public void removeSubtask(int id) {
        if (storage.getSubtask(id) == null) {
            return;
        }
        storage.getEpic(storage.getSubtask(id).getRelatedEpicId()).getSubtasksId().remove((Integer) id);
        checkEpicStatus(storage.getEpic(storage.getSubtask(id).getRelatedEpicId()));
        storage.getSubtaskStorage().remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private int generateId() {
        return id++;
    }

    private void checkEpicStatus(Epic epic) {
        if (epic.getSubtasksId().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        } else {
            for (int subId : epic.getSubtasksId()) {
                if (storage.getSubtask(subId).getStatus() == Status.IN_PROGRESS) {
                    epic.setStatus(Status.IN_PROGRESS);
                    return;
                }
            }
        }
        epic.setStatus(Status.DONE);
    }
}
