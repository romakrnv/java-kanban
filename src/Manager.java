import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    public static int id = 1;
    private HashMap<Integer, Task> taskStorage = new HashMap<>();
    private HashMap<Integer, Epic> epicStorage = new HashMap<>();
    private HashMap<Integer, Subtask> subtaskStorage = new HashMap<>();

    public void CreateTask(Task task) {
        taskStorage.put(id, task);
        id++;
    }

    public HashMap<Integer, Task> getAllTasks() {
        return taskStorage;
    }

    public Task getTaskById(int id) {
        return taskStorage.get(id);
    }

    public void removeAllTask() {
        taskStorage.clear();
    }

    public void updateTask(Task task) {
        taskStorage.put(task.getId(), task);
    }

    public void removeTaskById(int id) {
        taskStorage.remove(id);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public HashMap<Integer, Epic> getAllEpic() {
        return epicStorage;
    }

    public Epic getEpicById(int id) {
        return epicStorage.get(id);
    }

    public void removeAllEpic() {
        epicStorage.clear();
        subtaskStorage.clear();
        //сабтаски тоже нужно удалить
    }

    public ArrayList<Subtask> getAllEpicsSubtasks(int id) {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (int subId : epicStorage.get(id).getSubtasksId()) {
            subtasks.add(subtaskStorage.get(subId));
        }
        return subtasks;
    }

    private void checkEpicStatus(Epic epic) { //добавление сабтаски, удаление сабтаски, апдейт сабтаски
        if (epic.getSubtasksId().isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            for (int subId : epic.getSubtasksId()) {
                if (subtaskStorage.get(subId).getStatus() == Status.IN_PROGRESS) {
                    epic.setStatus(Status.IN_PROGRESS);
                    return;
                }
            }
        }
        epic.setStatus(Status.DONE);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public HashMap<Integer, Subtask> getAllSubtask() { //тут по эпику?
        return subtaskStorage;
    }

    public Subtask getSubtaskById(int id) {
        return subtaskStorage.get(id);
    }

    public void removeAllSubtask(int relatedEpicId) {
        for (int subId : epicStorage.get(relatedEpicId).getSubtasksId()) {
            subtaskStorage.remove(subId);
        }
        epicStorage.get(relatedEpicId).getSubtasksId().clear();
        //тут думаю в рамках одного эпика, но возможно и все сразу, но зачем?
    }


}
