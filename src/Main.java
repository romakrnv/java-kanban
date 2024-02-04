public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();

        System.out.println("Test Task");
        manager.CreateTask(new Task("name1", "text", Manager.id));
        manager.CreateTask(new Task("nam2", "text", Manager.id));
        manager.updateTask(new Task("changeName1", "text", 1, Status.IN_PROGRESS));

        System.out.println(manager.getAllTasks().toString());
        System.out.println(manager.getTask(1));

        manager.removeTask(2);

        System.out.println(manager.getAllTasks().toString());
        manager.removeAllTask();

        System.out.println("Test Epic and Subtask");
        manager.createEpic(new Epic("epic name 1", "some text", Manager.id));
        manager.createEpic(new Epic("epic name 2", "some text2", Manager.id));
        manager.updateEpic(new Epic("change epic name 1", "some text", 3));
        System.out.println(manager.getAllEpic().toString());
        System.out.println(manager.getEpic(4).toString());

        manager.createSubTask(new Subtask("sub name", "text", Manager.id, Status.NEW, 3));
        manager.createSubTask(new Subtask("sub name2", "text", Manager.id, Status.NEW, 3));
        manager.createSubTask(new Subtask("sub name", "text", Manager.id, Status.NEW, 4));
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
        System.out.println("Test done");
    }
}
