package com.kanban.manager;

import com.kanban.model.Epic;
import com.kanban.model.Subtask;
import com.kanban.model.Task;

import java.util.HashMap;

public class Storage {
    private final HashMap<Integer, Task> taskStorage = new HashMap<>();
    private final HashMap<Integer, Epic> epicStorage = new HashMap<>();
    private final HashMap<Integer, Subtask> subtaskStorage = new HashMap<>();


    public void add(int id, Task task){
        taskStorage.put(id, task);
    }

    public void add(int id, Epic epic){
        taskStorage.put(id, epic);
    }

    public void add(int id, Subtask subtask){
        taskStorage.put(id, subtask);
    }
}
