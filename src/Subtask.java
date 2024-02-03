public class Subtask extends Task {

    private int relatedEpicId;

    public Subtask(String name, String description, int id, Status status) {
        super(name, description, id, status);
    }
}
