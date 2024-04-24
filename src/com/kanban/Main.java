package com.kanban;

import com.kanban.model.Epic;
import com.kanban.model.Subtask;
import com.kanban.model.Task;
import com.kanban.model.TaskStatus;
import com.kanban.service.FileBackedTaskManager;
import com.kanban.service.InMemoryHistoryManager;
import com.kanban.service.InMemoryTaskManager;
import com.kanban.service.Managers;
import com.kanban.service.TaskManager;
import com.kanban.storage.Storage;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        /*TaskManager tm = Managers.getDefault();
        Task task = new Task();
        task.setStartTime(LocalDateTime.now());
        tm.addTask(task);
        tm.addEpic(new Epic());
        tm.addSubtask(new Subtask(2));
        tm.getTask(1);
        tm.getEpic(2);
        tm.getSubtask(3);
        System.out.println(tm.getAllTasks());
        System.out.println(tm.getAllEpics());
        System.out.println(tm.getAllSubtasks());
        System.out.println(tm.getHistory());*/
        //TaskManager tm = FileBackedTaskManager.loadFromFile(new File("resources/tz8.csv"));
        TaskManager tm = new InMemoryTaskManager(new InMemoryHistoryManager(), new Storage());
        Task task1 = new Task();
        task1.setId(1);
        task1.setStartTime(LocalDateTime.of(2024, 4, 24, 11, 00));
        task1.setDuration(Duration.ofMinutes(20));

        Task task2 = new Task();
        task2.setId(2);
        task2.setStartTime(LocalDateTime.of(2024, 4, 24, 12, 22));
        task2.setDuration(Duration.ofMinutes(30));

        tm.addTask(task1);
        tm.addTask(task2);

        task1.setDuration(Duration.ofMinutes(40));
        tm.updateTask(task1);

        Epic epic = new Epic();
        epic.setId(3);

        tm.addEpic(epic);

        Subtask subtask1 = new Subtask(3);
        subtask1.setId(4);
        subtask1.setStartTime(LocalDateTime.of(2024, 4, 24, 14, 22));
        subtask1.setDuration(Duration.ofMinutes(20));


        tm.addSubtask(subtask1);

        subtask1.setStatus(TaskStatus.IN_PROGRESS);

        tm.updateSubtask(subtask1);

        System.out.println(tm.getPrioritizedTasks());

        Task task3 = new Task();
        task3.setId(5);
        tm.addTask(task3);

        tm.removeAllTasks();
        System.out.println(tm.getAllEpicsSubtasks(3));
        System.out.println(tm.getPrioritizedTasks());
        System.out.println(tm.getEpic(3));
    }
}
