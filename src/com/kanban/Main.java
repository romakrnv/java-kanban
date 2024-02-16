package com.kanban;

import com.kanban.model.enums.*;
import com.kanban.model.*;
import com.kanban.manager.Manager;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();

        System.out.println("Test Task");
        Task task1 = manager.createTask(new Task("qew", "qwe"));
        task1.setStatus(Status.IN_PROGRESS);
        manager.createTask(new Task("nam2", "text2"));
        manager.updateTask(task1);

        System.out.println(manager.getAllTasks().toString());
        System.out.println(manager.getTask(1));
        manager.removeTask(2);
        System.out.println(manager.getAllTasks().toString());
        manager.removeAllTask();

        System.out.println("\nTest Epic and Subtask");
        Epic epic1 = manager.createEpic(new Epic("epic name 1", "some text"));
        epic1.setDescription("change description");
        epic1.setName("change name");
        manager.createEpic(new Epic("epic name 2", "some text2"));
        manager.updateEpic(epic1);
        System.out.println(manager.getAllEpic().toString());
        System.out.println(manager.getEpic(4).toString());

        manager.createSubTask(new Subtask("sub name", "text", 3));
        manager.createSubTask(new Subtask("sub name2", "text", 3));
        Subtask subtask1 = manager.createSubTask(new Subtask("sub name", "text", 4));
        subtask1.setStatus(Status.DONE);
        manager.updateSubtask(subtask1);

        System.out.println("\n" + manager.getSubtask(7).toString());
        System.out.println(manager.getEpic(4).toString());
        System.out.println(manager.getAllEpicsSubtasks(3).toString());
        System.out.println(manager.getAllSubtask().toString());


        manager.removeEpic(4);
        System.out.println(manager.getAllSubtask().toString());

        manager.removeSubtask(6);
        manager.removeAllSubtask(3);
        manager.removeAllEpic();
        manager.removeEpic(1);
        System.out.println("Test done");
    }
}
