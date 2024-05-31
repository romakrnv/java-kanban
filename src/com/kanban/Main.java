package com.kanban;

import com.kanban.model.Epic;
import com.kanban.model.Subtask;
import com.kanban.model.Task;
import com.kanban.service.HttpTaskServer;
import com.kanban.service.Managers;
import com.kanban.service.TaskManager;

import java.io.IOException;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws IOException {
        TaskManager tm = Managers.getDefault();
        Task task1 = new Task();
        task1.setStartTime(LocalDateTime.now());
        Task task2 = new Task();
        task2.setStartTime(LocalDateTime.now());
        tm.addTask(task1);
        tm.addTask(task2);
        tm.addTask(new Task());
        Epic epic = tm.addEpic(new Epic());
        Subtask subtask1 = new Subtask(epic.getId());
        subtask1.setStartTime(LocalDateTime.now());
        Subtask subtask2 = new Subtask(epic.getId());
        Subtask subtask3 = new Subtask(epic.getId());
        tm.addSubtask(subtask1);
        tm.addSubtask(subtask2);
        tm.addSubtask(subtask3);

        HttpTaskServer httpTaskServer = new HttpTaskServer(tm);
        System.out.println("server up");
    }
}
