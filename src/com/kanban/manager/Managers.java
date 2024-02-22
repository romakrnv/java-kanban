package com.kanban.manager;

public class Managers {
    public static TaskManager getDefault(){
        return getInMemoryTaskManager();
    }

    public static InMemoryTaskManager getInMemoryTaskManager(){
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }
}
