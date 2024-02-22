package com.kanban;

import com.kanban.manager.Managers;
import com.kanban.manager.TaskManager;
import com.kanban.model.enums.*;
import com.kanban.model.*;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        System.out.println("Test Task");
        Task task1 = manager.createTask(new Task("task1", "some text"));
        task1.setStatus(Status.IN_PROGRESS);
        manager.createTask(new Task("nam2", "text2"));
        manager.updateTask(task1);
        System.out.println(manager.getAllTasks().toString());
        System.out.println(manager.getTask(1));
        manager.removeTask(2);
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
        Subtask subtask6 = manager.getSubtask(6);
        Subtask subtask5 = manager.getSubtask(5);
        subtask5.setStatus(Status.DONE);
        subtask6.setStatus(Status.DONE);

        System.out.println("---" + manager.getAllEpicsSubtasks(3));
        manager.updateSubtask(subtask6);
        System.out.println("-----" + manager.getSubtask(6));
        System.out.println("-----" + manager.getEpic(subtask6.getRelatedEpicId()));
        manager.removeSubtask(6);
        manager.removeSubtask(5);
        System.out.println(manager.getEpic(3));
        manager.removeAllSubtask(3);
        manager.removeAllEpic();
        manager.removeEpic(1);
        manager.getEpic(3);
        manager.getSubtask(7);
        System.out.println("Test done");
        manager.removeAllTask();
        manager.removeAllEpic();
        System.out.println(manager.getAllEpic());
        System.out.println(manager.getHistory());

    }
}
