package com.kanban.service;

import com.kanban.storage.Storage;

import java.io.File;

public class Managers {
    public static TaskManager getDefault() {
        return getFileBackedTaskManager();
    }

    public static FileBackedTaskManager getFileBackedTaskManager(){
        return new FileBackedTaskManager(getDefaultHistory(), getDefaultStorage(),
                new File("resources/save.csv"));
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