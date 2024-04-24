package com.kanban.service;

import com.kanban.model.Epic;
import com.kanban.model.Subtask;
import com.kanban.model.Task;
import com.kanban.storage.Storage;
import com.kanban.model.TaskStatus;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private int id = 1;
    protected final HistoryManager historyManager;
    protected final Storage storage;
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

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
        storage.getTasks().stream().filter(this::isStartTimeExist).forEach(prioritizedTasks::remove);
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
        if (isStartTimeExist(task)) {
            checkIfIntersectedTaskExist(task);
            prioritizedTasks.add(task);
        }
        storage.add(task.getId(), task);
        return task;
    }

    @Override
    public void updateTask(Task task) {
        if (isStartTimeExist(storage.getTask(task.getId()))) {
            prioritizedTasks.remove(storage.getTask(task.getId()));
        }
        if (isStartTimeExist(task)) {
            checkIfIntersectedTaskExist(task);
            prioritizedTasks.add(task);
        }
        storage.add(task.getId(), task);
    }

    @Override
    public void removeTask(int id) {
        if (isStartTimeExist(storage.getTask(id))) {
            prioritizedTasks.remove(storage.getTask(id));
        }
        storage.removeTask(id);
    }

    @Override
    public List<Epic> getAllEpics() {
        return storage.getEpics();
    }

    @Override
    public void removeAllEpics() {
        storage.getEpics().stream().filter(this::isStartTimeExist).forEach(prioritizedTasks::remove);
        storage.getSubtasks().stream().filter(this::isStartTimeExist).forEach(prioritizedTasks::remove);
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
        storage.getEpic(id).getSubtasksIds().forEach(this::removeSubtask);
        storage.removeEpic(id);
    }

    @Override
    public List<Subtask> getAllEpicsSubtasks(int id) {
        return storage.getEpic(id).getSubtasksIds().stream()
                .map(storage::getSubtask).collect(Collectors.toList());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return storage.getSubtasks();
    }

    @Override
    public void removeAllSubtasks(int epicId) {
        Epic epic = storage.getEpic(epicId);
        epic.getSubtasksIds().stream().map(storage::getSubtask).filter(this::isStartTimeExist)
                .forEach(prioritizedTasks::remove);
        epic.getSubtasksIds().forEach(storage::removeSubtask);
        epic.getSubtasksIds().clear();
        updateEpicDurationAndTime(epic);
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
        if (isStartTimeExist(subtask)) {
            checkIfIntersectedTaskExist(subtask);
            prioritizedTasks.add(subtask);
        }
        storage.add(subtask.getId(), subtask);
        storage.getEpic(subtask.getEpicId()).getSubtasksIds().add(subtask.getId());
        updateEpicDurationAndTime(storage.getEpic(subtask.getEpicId()));
        checkEpicStatus(storage.getEpic(subtask.getEpicId()));
        return subtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (storage.getEpic(subtask.getEpicId()) == null) {
            return;
        }
        if (isStartTimeExist(storage.getSubtask(subtask.getId()))) {
            prioritizedTasks.remove(storage.getSubtask(subtask.getId()));
        }
        subtask.setEpicId(storage.getSubtask(subtask.getId()).getEpicId());
        storage.add(subtask.getId(), subtask);
        if (isStartTimeExist(subtask)) {
            checkIfIntersectedTaskExist(subtask);
            prioritizedTasks.add(subtask);
        }
        updateEpicDurationAndTime(storage.getEpic(subtask.getEpicId()));
        checkEpicStatus(storage.getEpic(subtask.getEpicId()));
    }

    @Override
    public void removeSubtask(int id) {
        if (storage.getSubtask(id) == null) {
            return;
        }
        if (isStartTimeExist(storage.getSubtask(id))) {
            prioritizedTasks.remove(storage.getSubtask(id));
        }
        storage.getEpic(storage.getSubtask(id).getEpicId()).getSubtasksIds().remove((Integer) id);
        checkEpicStatus(storage.getEpic(storage.getSubtask(id).getEpicId()));
        updateEpicDurationAndTime(storage.getEpic(storage.getSubtask(id).getEpicId()));
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

    private void updateEpicDurationAndTime(Epic epic) {
        if (epic.getSubtasksIds().isEmpty()) {
            epic.setDuration(Duration.ofMinutes(0));
            epic.setStartTime(null);
            epic.setEndTime(null);
            return;
        }
        Optional<Subtask> firstSubtask = storage.getSubtasks().stream().filter(sub -> epic.getSubtasksIds()
                .contains(sub.getId())).min(Comparator.comparing(Task::getStartTime)).stream().findFirst();
        firstSubtask.ifPresent(value -> epic.setStartTime(value.getStartTime()));

        Optional<Subtask> lastSubtask = storage.getSubtasks().stream().filter(s -> epic.getSubtasksIds()
                .contains(s.getId())).max(Comparator.comparing(Task::getEndTime)).stream().findFirst();
        lastSubtask.ifPresent(value -> epic.setEndTime(value.getEndTime()));

        long totalDuration = 0;
        for (int i : epic.getSubtasksIds())
            totalDuration = totalDuration + storage.getSubtask(i).getDuration().toMinutes();
        epic.setDuration(Duration.ofMinutes(totalDuration));
    }

    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    private boolean isTasksIntersected(Task firstTask, Task secondTask) {
        return (firstTask.getEndTime().isAfter(secondTask.getStartTime())
                && firstTask.getEndTime().isBefore(secondTask.getEndTime())
                || secondTask.getEndTime().isAfter(firstTask.getStartTime())
                && secondTask.getEndTime().isBefore(firstTask.getEndTime()));
    }

    private void checkIfIntersectedTaskExist(Task currentTask) {
        if (currentTask.getStartTime() == null) {
            return;
        }
        Optional<Task> intersectedTask = prioritizedTasks.stream()
                .filter(task -> isTasksIntersected(currentTask, task)).findFirst();
        if (intersectedTask.isPresent()) {
            throw new ManagerSaveException("error:Task intersect");
        }
    }

    private boolean isStartTimeExist(Task task) {
        if (task == null) {
            return false;
        }
        return task.getStartTime() != null;
    }
}
