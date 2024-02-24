package com.kanban.service;

import com.kanban.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_HISTORY_RECORDS = 10;

    private final List<Task> history = new ArrayList<>();

    @Override
    public List<Task> getHistory() {
        return history;
    }

    @Override
    public void add(Task task) {
        if (history.size() >= MAX_HISTORY_RECORDS) {
            history.remove(0);
        }
        history.add(task);
    }
}
