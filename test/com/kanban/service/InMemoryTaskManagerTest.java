package com.kanban.service;

import com.kanban.model.Epic;
import com.kanban.model.Subtask;
import com.kanban.model.Task;
import com.kanban.model.TaskStatus;
import com.kanban.storage.Storage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class InMemoryTaskManagerTest {
    private InMemoryTaskManager manager;
    private Storage storage;
    private InMemoryHistoryManager historyManager;

    @BeforeEach
    void setUp() {
        storage = mock(Storage.class);
        historyManager = mock(InMemoryHistoryManager.class);
        manager = new InMemoryTaskManager(historyManager, storage);
    }

    @Test
    void getAllTasks() {
        Task task1 = new Task();
        Task task2 = new Task();
        Mockito.when(storage.getTasks()).thenReturn(List.of(task1, task2));

        int expectedSize = 2;

        assertEquals(expectedSize, manager.getAllTasks().size());
    }

    @Test
    void getTask() {
        int taskId = 1;
        Task expectedTask = new Task();
        Mockito.when(storage.getTask(taskId)).thenReturn(expectedTask);

        Task actualTask = manager.getTask(taskId);

        assertEquals(expectedTask, actualTask);
    }

    @Test
    void createTask() {
        Task task = manager.createTask(new Task());

        assertNotNull(task);
    }

    @Test
    void updateTask() {
        Task task = new Task();
        task.setId(1);
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setName("new name");

        manager.updateTask(task);

        Mockito.verify(storage).add(1, task);
    }

    @Test
    void getAllEpic() {
        Epic epic1 = new Epic();
        Epic epic2 = new Epic();
        Mockito.when(storage.getEpics()).thenReturn(List.of(epic1, epic2));

        int actualSize = manager.getAllEpic().size();

        assertEquals(2, actualSize);
    }

    @Test
    void getEpic() {
        int epicId = 1;
        Epic expectedEpic = new Epic();
        Mockito.when(storage.getEpic(epicId)).thenReturn(expectedEpic);

        Epic actualEpic = manager.getEpic(epicId);

        assertEquals(expectedEpic, actualEpic);
    }

    @Test
    void createEpic() {
        Epic epic = manager.createEpic(new Epic());

        assertNotNull(epic);
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic();
        Mockito.when(storage.getEpic(0)).thenReturn(epic);

        manager.updateEpic(epic);

        Mockito.verify(storage).add(0, epic);
    }

    @Test
    void getAllEpicsSubtasks() {
        Epic epic = mock(Epic.class);
        int epicId = 1;
        int taskId1 = 2;
        int taskId2 = 3;
        Mockito.when(storage.getEpic(epicId)).thenReturn(epic);
        Mockito.when(epic.getSubtaskId()).thenReturn(List.of(taskId1, taskId2));

        int expectedSize = manager.getAllEpicsSubtasks(epicId).size();

        assertEquals(2, expectedSize);
    }

    @Test
    void getAllSubtask() {
        Subtask subtask1 = new Subtask(1);
        Subtask subtask2 = new Subtask(1);
        Mockito.when(storage.getSubtasks()).thenReturn(List.of(subtask1, subtask2));

        int expectedSize = manager.getAllSubtask().size();

        assertEquals(expectedSize, manager.getAllSubtask().size());
    }

    @Test
    void getSubtask() {
        Subtask expectedSubtask = new Subtask(1);
        int subtaskId = 2;
        Mockito.when(storage.getSubtask(subtaskId)).thenReturn(expectedSubtask);

        Subtask actualSubtask = manager.getSubtask(subtaskId);

        assertEquals(expectedSubtask, actualSubtask);
    }

    @Test
    void createSubTask() {
        Subtask subtask = new Subtask(2);
        subtask.setId(1);
        Epic epic = new Epic();
        epic.setId(2);
        Mockito.when(storage.getEpic(2)).thenReturn(epic);
        Mockito.when(storage.getSubtask(1)).thenReturn(subtask);

        Subtask actualSubtask = manager.createSubTask(new Subtask(epic.getId()));

        assertNotNull(actualSubtask);
    }

    @Test
    void updateSubtask() {
        Subtask subtask = new Subtask(2);
        subtask.setId(1);
        subtask.setName("qwe");
        Epic epic = new Epic();
        epic.setId(2);
        Mockito.when(storage.getEpic(2)).thenReturn(epic);
        Mockito.when(storage.getSubtask(1)).thenReturn(subtask);

        manager.updateSubtask(subtask);

        Mockito.verify(storage).add(1, subtask);
    }

    @Test
    void getHistory() {
        Task task = new Task();
        Epic epic = new Epic();
        Subtask subtask = new Subtask(0);
        Mockito.when(historyManager.getHistory()).thenReturn(List.of(task, epic, subtask));

        int actualHistorySize = manager.getHistory().size();

        assertEquals(3, actualHistorySize);
    }

    @Test
    void removeTask() {
        manager.removeTask(1);

        Mockito.verify(storage).removeTask(1);
    }

    @Test
    void removeAllTask() {
        manager.removeAllTask();

        Mockito.verify(storage).clearTasks();
    }

    @Test
    void removeSubtask() {
        Subtask subtask = new Subtask(0);
        subtask.setId(0);
        Epic epic = new Epic();
        Mockito.when(storage.getEpic(0)).thenReturn(epic);
        Mockito.when(storage.getSubtask(0)).thenReturn(subtask);

        manager.removeSubtask(0);

        Mockito.verify(storage).removeSubtask(0);
    }

    @Test
    void removeAllSubtask() {
        Epic epic = new Epic();
        epic.getSubtaskId().add(1);
        epic.getSubtaskId().add(2);
        Mockito.when(storage.getEpic(0)).thenReturn(epic);

        manager.removeAllSubtask(0);

        Mockito.verify(storage).removeSubtask(2);
    }

    @Test
    void removeEpic() {
        Mockito.when(storage.getEpic(0)).thenReturn(new Epic());

        manager.removeEpic(0);

        Mockito.verify(storage).removeEpic(0);
    }

    @Test
    void checkEpicStatus_whenAllSubtaskNew_thenEpicStatusNew() {
        Epic epic = new Epic();
        epic.setId(1);
        epic.getSubtaskId().add(2);
        epic.getSubtaskId().add(3);
        Subtask subtask1 = new Subtask(1);
        subtask1.setId(2);
        Subtask subtask2 = new Subtask(1);
        subtask2.setId(3);
        Mockito.when(storage.getEpic(1)).thenReturn(epic);
        Mockito.when(storage.getSubtask(2)).thenReturn(subtask1);
        Mockito.when(storage.getSubtask(3)).thenReturn(subtask2);

        manager.updateEpic(epic);

        assertEquals(TaskStatus.NEW, manager.getEpic(1).getStatus());
    }

    @Test
    void checkEpicStatus_whenDifferentSubtasksStatus_thenEpicStatusInProgress() {
        Epic epic = new Epic();
        epic.setId(1);
        epic.getSubtaskId().add(2);
        epic.getSubtaskId().add(3);
        Subtask subtask1 = new Subtask(1);
        subtask1.setId(2);
        Subtask subtask2 = new Subtask(1);
        subtask2.setId(3);
        subtask1.setStatus(TaskStatus.DONE);
        Mockito.when(storage.getEpic(1)).thenReturn(epic);
        Mockito.when(storage.getSubtask(2)).thenReturn(subtask1);
        Mockito.when(storage.getSubtask(3)).thenReturn(subtask2);

        manager.updateEpic(epic);

        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpic(1).getStatus());
    }

    @Test
    void checkEpicStatus_whenAllSubtaskDone_thenEpicStatusDone() {
        Epic epic = new Epic();
        epic.setId(1);
        epic.getSubtaskId().add(2);
        epic.getSubtaskId().add(3);
        Subtask subtask1 = new Subtask(1);
        subtask1.setId(2);
        Subtask subtask2 = new Subtask(1);
        subtask2.setId(3);
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        Mockito.when(storage.getEpic(1)).thenReturn(epic);
        Mockito.when(storage.getSubtask(2)).thenReturn(subtask1);
        Mockito.when(storage.getSubtask(3)).thenReturn(subtask2);

        manager.updateEpic(epic);

        assertEquals(TaskStatus.DONE, manager.getEpic(1).getStatus());
    }


}