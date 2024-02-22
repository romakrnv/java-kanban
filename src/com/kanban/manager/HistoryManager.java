package com.kanban.manager;

import com.kanban.model.Task;

import java.util.List;

public interface HistoryManager {
    List<Task> getHistory();

    void add(Task task);
}
