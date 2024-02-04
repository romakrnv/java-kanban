public class Subtask extends Task {

    private int relatedEpicId;

    public Subtask(String name, String description, int id, Status status, int relatedEpicId) {
        super(name, description, id, status);
        this.relatedEpicId = relatedEpicId;
    }

    public Subtask(String name, String description, int id, Status status) {
        super(name, description, id, status);

    }

    public void setRelatedEpicId(int relatedEpicId) {
        this.relatedEpicId = relatedEpicId;
    }

    public int getRelatedEpicId() {
        return relatedEpicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                "relatedEpicId=" + relatedEpicId +
                '}';
    }
}
