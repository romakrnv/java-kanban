package com.kanban.service;

import com.kanban.model.Epic;
import com.kanban.model.Subtask;
import com.kanban.model.Task;
import com.kanban.model.TaskStatus;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private static TaskManager manager = Managers.getDefault();

    @BeforeAll
    static void beforeAll() {
        manager = Managers.getDefault();
        Task task1 = new Task();
        Task task2 = new Task();
        Epic epic1 = new Epic();
        Epic epic2 = new Epic();
        Subtask subtask1 = new Subtask( 3);
        Subtask subtask2 = new Subtask( 3);
        Subtask subtask3 = new Subtask( 4);
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createSubTask(subtask1);
        manager.createSubTask(subtask2);
        manager.createSubTask(subtask3);
    }

    @Test
    void getAllTasks() {
        assertEquals(2, manager.getAllTasks().size());
    }

    @Test
    void getTask() {
        Task expectedTask = new Task();

        expectedTask.setId(1);

        assertEquals(expectedTask, manager.getTask(1));
    }

    @Test
    void createTask() {
        Task task = new Task();

        manager.createTask(task);

        assertEquals(task, manager.getTask(task.getId()));
    }

    @Test
    void updateTask() {
        Task task = new Task();
        task.setId(1);
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setName("new name");
        task.setDescription("new description");
        manager.updateTask(task);

        assertEquals("new name", manager.getTask(1).getName());
        assertEquals("new description", manager.getTask(1).getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, manager.getTask(1).getStatus());
    }

    @Test
    void getAllEpic() {
        assertEquals(2, manager.getAllEpic().size());
    }

    @Test
    void getEpic() {
        Epic expectedEpic = new Epic();

        expectedEpic.setId(3);

        assertEquals(expectedEpic, manager.getEpic(3));
    }

    @Test
    void createEpic() {
        Epic epic = new Epic();

        manager.createEpic(epic);

        assertEquals(epic, manager.getEpic(epic.getId()));
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic();
        epic.setId(3);
        epic.setName("new name");
        epic.setDescription("new description");
        manager.updateEpic(epic);

        assertEquals("new name", manager.getEpic(3).getName());
        assertEquals("new description", manager.getEpic(3).getDescription());
    }

    @Test
    void getAllEpicsSubtasks() {
        assertEquals(2, manager.getAllEpicsSubtasks(3).size());
    }

    @Test
    void getAllSubtask() {
        assertEquals(3, manager.getAllSubtask().size());
    }

    @Test
    void getSubtask() {
        Subtask expectedSubtask = new Subtask(3);
        expectedSubtask.setId(5);

        assertEquals(expectedSubtask, manager.getSubtask(5));
    }

    @Test
    void createSubTask() {
        Subtask subtask = new Subtask(3);

        manager.createSubTask(subtask);

        assertEquals(subtask, manager.getSubtask(subtask.getId()));
    }

    @Test
    void updateSubtask() {
        Subtask subtask = new Subtask( 3);
        subtask.setId(5);
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        subtask.setName("new name");
        subtask.setDescription("new description");

        manager.updateTask(subtask);

        assertEquals("new name", manager.getTask(5).getName());
        assertEquals("new description", manager.getTask(5).getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, manager.getTask(5).getStatus());
    }

    @Test
    void getHistory() {
        manager.getTask(1);
        manager.getEpic(3);
        manager.getSubtask(5);

        assertEquals(10, manager.getHistory().size());
    }

    @Test
    void getHistory_whenSizeMore10_thenReturnSize10() {
        for (int i = 0; i < 15; i++) {
            manager.getTask(1);
        }

        assertEquals(10, manager.getHistory().size());
    }

    @Test
    void checkEpicStatus_whenAllSubtaskNew_thenEpicStatusNew() {
        assertEquals(TaskStatus.NEW, manager.getEpic(3).getStatus());
    }

    @Test
    void checkEpicStatus_whenUpdateSubtask_thenEpicStatusInProgress() {
        Subtask subtask = manager.getSubtask(5);
        subtask.setStatus(TaskStatus.IN_PROGRESS);

        manager.updateSubtask(subtask);

        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpic(3).getStatus());
    }

    @Test
    void checkEpicStatus_whenAllSubtaskDone_thenEpicStatusDone() {
        Subtask subtask1 = manager.getSubtask(5);
        subtask1.setStatus(TaskStatus.DONE);
        Subtask subtask2 = manager.getSubtask(6);
        subtask2.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);

        assertEquals(TaskStatus.DONE, manager.getEpic(3).getStatus());
    }

    @AfterAll
    static void removeTask() {
        manager.removeTask(1);
        assertNull(manager.getTask(1));
    }

    @AfterAll
    static void removeAllTask() {
        manager.removeAllTask();

        assertEquals(0, manager.getAllTasks().size());
    }

    @AfterAll
    static void removeSubtask() {
        manager.removeSubtask(5);

        assertNull(manager.getSubtask(5));
    }

    @AfterAll
    static void removeAllSubtask() {
        manager.removeAllSubtask(3);

        assertEquals(0, manager.getAllEpicsSubtasks(3).size());
    }

    @AfterAll
    static void removeEpic() {
        manager.removeEpic(3);

        assertNull(manager.getEpic(3));
    }

}