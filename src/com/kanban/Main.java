package com.kanban;

import com.kanban.model.Task;
import com.kanban.service.InMemoryHistoryManager;
import com.kanban.service.InMemoryTaskManager;
import com.kanban.service.TaskManager;
import com.kanban.storage.Storage;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new InMemoryTaskManager(new InMemoryHistoryManager(), new Storage());
        Task task = new Task();

        manager.createTask(task);
        manager.createTask(new Task());
        manager.createTask(new Task());
        manager.getTask(1);
        manager.getTask(2);
        manager.getTask(3);
        System.out.println(manager.getHistory());
        manager.getTask(1);
        System.out.println(manager.getHistory());
        manager.getTask(1);
        System.out.println(manager.getHistory());
        manager.getTask(2);
        System.out.println(manager.getHistory());
    }
}
