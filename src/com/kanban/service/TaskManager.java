package com.kanban.service;

import com.kanban.model.Epic;
import com.kanban.model.Subtask;
import com.kanban.model.Task;

import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {
    Collection<Task> getAllTasks();

    void removeAllTasks();

    Task getTask(int id);

    Task addTask(Task task);

    Task updateTask(Task task);

    void removeTask(int id);

    Collection<Epic> getAllEpics();

    void removeAllEpics();

    Epic getEpic(int id);

    Epic addEpic(Epic epic);

    Epic updateEpic(Epic epic);

    void removeEpic(int id);

    List<Subtask> getAllEpicsSubtasks(int id);

    List<Subtask> getAllSubtasks();

    void removeAllSubtasks(int relatedEpicId);

    Subtask getSubtask(int id);

    Subtask addSubtask(Subtask subtask);

    Subtask updateSubtask(Subtask subtask);

    void removeSubtask(int id);

    List<Task> getHistory();

    TreeSet<Task> getPrioritizedTasks();
}
