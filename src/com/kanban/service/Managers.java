package com.kanban.service;

import com.kanban.storage.Storage;

public class Managers {
    public static TaskManager getDefault() {
        return getInMemoryTaskManager();
    }

    public static InMemoryTaskManager getInMemoryTaskManager() {
        return new InMemoryTaskManager(getDefaultHistory(), getDefaultStorage());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Storage getDefaultStorage() {
        return new Storage();
    }
}