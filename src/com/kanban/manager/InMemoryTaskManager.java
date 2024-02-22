package com.kanban.manager;

import com.kanban.model.enums.Status;
import com.kanban.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private static int id = 1;
    private final HashMap<Integer, Task> taskStorage = new HashMap<>();
    private final HashMap<Integer, Epic> epicStorage = new HashMap<>();
    private final HashMap<Integer, Subtask> subtaskStorage = new HashMap<>();
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager){
        this.historyManager = historyManager;
    }

    @Override
    public Collection<Task> getAllTasks() {
        return taskStorage.values();
    }

    @Override
    public void removeAllTask() {
        taskStorage.clear();
    }

    @Override
    public Task getTask(int id) {
        historyManager.add(taskStorage.get(id));
        return taskStorage.get(id);
    }

    @Override
    public Task createTask(Task task) {
        task.setId(generateId());
        return task;
    }

    @Override
    public void updateTask(Task task) {
        taskStorage.put(task.getId(), task);
    }

    @Override
    public void removeTask(int id) {
        taskStorage.remove(id);
    }

    @Override
    public Collection<Epic> getAllEpic() {
        return epicStorage.values();
    }

    @Override
    public void removeAllEpic() {
        epicStorage.clear();
        subtaskStorage.clear(); //сабы не могут существовать без эпиков
    }

    @Override
    public Epic getEpic(int id) {
        return epicStorage.get(id);
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epicStorage.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        epicStorage.put(epic.getId(), epic);
        checkEpicStatus(epic);
    }

    @Override
    public void removeEpic(int id) {
        if (epicStorage.containsKey(id)) {
            for (int subId : epicStorage.get(id).getSubtasksId()) {
                subtaskStorage.remove(subId);
            }
            epicStorage.remove(id);
        }
    }

    @Override
    public Collection<Subtask> getAllEpicsSubtasks(int id) {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (int subId : epicStorage.get(id).getSubtasksId()) {
            subtasks.add(subtaskStorage.get(subId));
        }
        return subtasks;
    }

    @Override
    public Collection<Subtask> getAllSubtask() {
        return subtaskStorage.values();
    }

    @Override
    public void removeAllSubtask(int relatedEpicId) {
        for (int subId : epicStorage.get(relatedEpicId).getSubtasksId()) {
            subtaskStorage.remove(subId);
        }
        epicStorage.get(relatedEpicId).getSubtasksId().clear();
        epicStorage.get(relatedEpicId).setStatus(Status.NEW);
    }

    @Override
    public Subtask getSubtask(int id) {
        return subtaskStorage.get(id);
    }

    @Override
    public Subtask createSubTask(Subtask subtask) {
        subtask.setId(generateId());
        subtaskStorage.put(subtask.getId(), subtask);
        epicStorage.get(subtask.getRelatedEpicId()).getSubtasksId().add(subtask.getId());
        checkEpicStatus(epicStorage.get(subtask.getRelatedEpicId()));
        return subtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtask.setRelatedEpicId(subtaskStorage.get(subtask.getId()).getRelatedEpicId());
        subtaskStorage.put(subtask.getId(), subtask);
        checkEpicStatus(epicStorage.get(subtask.getRelatedEpicId()));
    }

    @Override
    public void removeSubtask(int id) {
        for (int i = 0; i < epicStorage.get(subtaskStorage.get(id).
                getRelatedEpicId()).getSubtasksId().size(); i++) {
            if (epicStorage.get(subtaskStorage.get(id).
                    getRelatedEpicId()).getSubtasksId().get(i) == id) {
                epicStorage.get(subtaskStorage.get(id).
                        getRelatedEpicId()).getSubtasksId().remove(i);
                return;
            }
        }
        checkEpicStatus(epicStorage.get(subtaskStorage.get(id).getRelatedEpicId()));
        subtaskStorage.remove(id);
    }

    @Override
    public List<Task> getHistory(){
        return historyManager.getHistory();
    }

    private int generateId(){
        return id++;
    }

    private void checkEpicStatus(Epic epic) {
        if (epic.getSubtasksId().isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            for (int subId : epic.getSubtasksId()) {
                if (subtaskStorage.get(subId).getStatus() == Status.IN_PROGRESS) {
                    epic.setStatus(Status.IN_PROGRESS);
                    return;
                }
            }
        }
        epic.setStatus(Status.DONE);
    }
}
