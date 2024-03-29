package com.kanban.service;

import com.kanban.model.Epic;
import com.kanban.model.Subtask;
import com.kanban.model.Task;

import java.util.Collection;
import java.util.List;

public interface TaskManager {
    Collection<Task> getAllTasks();

    void removeAllTasks();

    Task getTask(int id);

    Task createTask(Task task);

    void updateTask(Task task);

    void removeTask(int id);

    Collection<Epic> getAllEpics();

    void removeAllEpics();

    Epic getEpic(int id);

    Epic createEpic(Epic epic);

    void updateEpic(Epic epic);

    void removeEpic(int id);

    Collection<Subtask> getAllEpicsSubtasks(int id);

    Collection<Subtask> getAllSubtasks();

    void removeAllSubtasks(int relatedEpicId);

    Subtask getSubtask(int id);

    Subtask createSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void removeSubtask(int id);

    List<Task> getHistory();
}
