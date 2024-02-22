package com.kanban.service;

import com.kanban.model.Epic;
import com.kanban.model.Subtask;
import com.kanban.model.Task;

import java.util.Collection;
import java.util.List;

public interface TaskManager {
    Collection<Task> getAllTasks();

    void removeAllTask();

    Task getTask(int id);

    Task createTask(Task task);

    void updateTask(Task task);

    void removeTask(int id);

    Collection<Epic> getAllEpic();

    void removeAllEpic();

    Epic getEpic(int id);

    Epic createEpic(Epic epic);

    void updateEpic(Epic epic);

    void removeEpic(int id);

    Collection<Subtask> getAllEpicsSubtasks(int id);

    Collection<Subtask> getAllSubtask();

    void removeAllSubtask(int relatedEpicId);

    Subtask getSubtask(int id);

    Subtask createSubTask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void removeSubtask(int id);

    List<Task> getHistory();
}
