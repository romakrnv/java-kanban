public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        Manager manager = new Manager();
        manager.CreateTask(new Task("qwe", "some", Manager.id, Status.NEW));
        manager.updateTask(new Task("qwe", "some", 1, Status.IN_PROGRESS));
        System.out.println(manager.getAllTasks().get(1).toString());

    }
}

