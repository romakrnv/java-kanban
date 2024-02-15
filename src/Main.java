import Enums.*;
import Task.*;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();

        System.out.println("Test Task.Task");
        manager.CreateTask(new Task("name1", "text"));
        manager.CreateTask(new Task("nam2", "text2"));
        manager.updateTask(new Task("changeName1", "text", 1, Status.IN_PROGRESS));

        System.out.println(manager.getAllTasks().toString());
        System.out.println(manager.getTask(1));
        manager.removeTask(2);
        System.out.println(manager.getAllTasks().toString());
        manager.removeAllTask();

        System.out.println("Test Task.Epic and Task.Subtask");
        manager.createEpic(new Epic("epic name 1", "some text"));
        manager.createEpic(new Epic("epic name 2", "some text2"));
        manager.updateEpic(new Epic("change epic name 1", "some text", 3));
        System.out.println(manager.getAllEpic().toString());
        System.out.println(manager.getEpic(4).toString());

        manager.createSubTask(new Subtask("sub name", "text", 3));
        manager.createSubTask(new Subtask("sub name2", "text", 3));
        manager.createSubTask(new Subtask("sub name", "text", 4));
        manager.updateSubtask(new Subtask("sub name", "text", 7, Status.DONE));

        System.out.println(manager.getSubtask(7).toString());
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
