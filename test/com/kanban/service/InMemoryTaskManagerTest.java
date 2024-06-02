package com.kanban.service;

import com.kanban.model.Epic;
import com.kanban.model.Subtask;
import com.kanban.model.Task;
import com.kanban.model.TaskStatus;
import com.kanban.storage.Storage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InMemoryTaskManagerTest {
    /*
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
    void getAllTasks_when2TasksExist_thenReturnSize2() {
        Task task1 = new Task();
        Task task2 = new Task();
        when(storage.getTasks()).thenReturn(List.of(task1, task2));

        int expectedSize = 2;

        assertEquals(expectedSize, manager.getAllTasks().size());
    }

    @Test
    void getTask_whenTaskExist_thenReturnTask() {
        int taskId = 1;
        Task expectedTask = new Task();
        when(storage.getTask(taskId)).thenReturn(expectedTask);

        Task actualTask = manager.getTask(taskId);

        assertEquals(expectedTask, actualTask);
        verify(historyManager).add(expectedTask);
    }

    @Test
    void createTask_whenNewTask_thenTaskNotNull() {
        Task task = manager.addTask(new Task());

        assertNotNull(task);
        verify(storage).add(task.getId(), task);
    }

    @Test
    void updateTask_whenNewTask_thenStorageAddCallOneTime() {
        Task task = new Task();
        task.setId(1);
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setName("new name");

        manager.updateTask(task);

        verify(storage).add(1, task);
    }

    @Test
    void getAllEpics_when2EpicsExist_thenReturnSize2() {
        Epic epic1 = new Epic();
        Epic epic2 = new Epic();
        when(storage.getEpics()).thenReturn(List.of(epic1, epic2));

        int actualSize = manager.getAllEpics().size();

        assertEquals(2, actualSize);
    }

    @Test
    void getEpic_whenEpicExist_thenReturnEpic() {
        int epicId = 1;
        Epic expectedEpic = new Epic();
        when(storage.getEpic(epicId)).thenReturn(expectedEpic);

        Epic actualEpic = manager.getEpic(epicId);

        assertEquals(expectedEpic, actualEpic);
        verify(historyManager).add(expectedEpic);
    }

    @Test
    void createEpic_whenNewEpic_thenEpicNotNull() {
        Epic epic = manager.addEpic(new Epic());

        assertNotNull(epic);
        assertEquals(1, epic.getId());
        verify(storage).add(epic.getId(), epic);
    }

    @Test
    void updateEpic_whenNewEpic_thenStorageAddCallOneTime() {
        Epic epic = new Epic();
        when(storage.getEpic(0)).thenReturn(epic);

        manager.updateEpic(epic);

        verify(storage).add(0, epic);
    }

    @Test
    void getAllEpicsSubtasks_when2SubtaskExist_ThenReturnSize2() {
        Epic epic = mock(Epic.class);
        int epicId = 1;
        int taskId1 = 2;
        int taskId2 = 3;
        when(storage.getEpic(epicId)).thenReturn(epic);
        when(epic.getSubtasksIds()).thenReturn(List.of(taskId1, taskId2));

        int expectedSize = manager.getAllEpicsSubtasks(epicId).size();

        assertEquals(2, expectedSize);
    }

    @Test
    void getAllSubtasks_when2SubtasksExist_thenReturnSize2() {
        Subtask subtask1 = new Subtask(1);
        Subtask subtask2 = new Subtask(1);
        when(storage.getSubtasks()).thenReturn(List.of(subtask1, subtask2));

        int expectedSize = manager.getAllSubtasks().size();

        assertEquals(expectedSize, manager.getAllSubtasks().size());
    }

    @Test
    void getSubtask_whenSubtaskExist_thenReturnSubtask() {
        Subtask expectedSubtask = new Subtask(1);
        int subtaskId = 2;
        when(storage.getSubtask(subtaskId)).thenReturn(expectedSubtask);

        Subtask actualSubtask = manager.getSubtask(subtaskId);

        assertEquals(expectedSubtask, actualSubtask);
        verify(historyManager).add(expectedSubtask);
    }

    @Test
    void createSubTask_whenNewSubtask_thenSubtaskNotNull() {
        Subtask subtask = new Subtask(2);
        Epic epic = new Epic();
        epic.getSubtasksIds().add(3);
        epic.setId(2);
        subtask.setId(3);
        when(storage.getEpic(2)).thenReturn(epic);
        when(storage.getSubtask(3)).thenReturn(subtask);

        Subtask actualSubtask = manager.addSubtask(subtask);

        assertNotNull(actualSubtask);
        verify(storage).add(subtask.getId(), subtask);
    }

    @Test
    void updateSubtask_whenNewSubtask_thenStorageAddCallOneTime() {
        Subtask subtask = new Subtask(2);
        subtask.setId(1);
        subtask.setName("qwe");
        Epic epic = new Epic();
        epic.setId(2);
        when(storage.getEpic(2)).thenReturn(epic);
        when(storage.getSubtask(1)).thenReturn(subtask);

        manager.updateSubtask(subtask);

        verify(storage).add(1, subtask);
    }

    @Test
    void getHistory_when3RecordsExist_thenReturnHistorySize3() {
        Task task = new Task();
        Epic epic = new Epic();
        Subtask subtask = new Subtask(0);
        when(historyManager.getHistory()).thenReturn(List.of(task, epic, subtask));

        int actualHistorySize = manager.getHistory().size();

        assertEquals(3, actualHistorySize);
        verify(historyManager).getHistory();
    }

    @Test
    void removeTask_thenStorageCallRemove() {
        manager.removeTask(1);

        verify(storage).removeTask(1);
    }

    @Test
    void removeAllTasks_thenStorageCallClear() {
        manager.removeAllTasks();

        verify(storage).clearTasks();
    }

    @Test
    void removeSubtask_whenSubtaskExist_thenStorageCallRemoveSubtaskOneTime() {
        Subtask subtask = new Subtask(0);
        subtask.setId(0);
        Epic epic = new Epic();
        when(storage.getEpic(0)).thenReturn(epic);
        when(storage.getSubtask(0)).thenReturn(subtask);

        manager.removeSubtask(0);

        verify(storage).removeSubtask(0);
    }

    @Test
    void removeAllSubtasks_when2SubtasksExistUnderEpic0_thenStorageCallRemoveSubtasksTwoTimes() {
        Epic epic = new Epic();
        epic.getSubtasksIds().add(1);
        epic.getSubtasksIds().add(2);
        when(storage.getEpic(0)).thenReturn(epic);

        manager.removeAllSubtasks(0);

        verify(storage).removeSubtask(1);
        verify(storage).removeSubtask(2);
    }

    @Test
    void removeEpic_thenStorageCallRemoveOneTime() {
        when(storage.getEpic(0)).thenReturn(new Epic());

        manager.removeEpic(0);

        verify(storage).removeEpic(0);
    }

    @Test
    void removeAllEpics_thenStorageCallClearForEpicsAndSubtasks() {
        manager.removeAllEpics();

        verify(storage).clearEpics();
        verify(storage).clearSubtasks();
    }

    @Test
    void checkEpicStatus_whenAllSubtaskNew_thenEpicStatusNew() {
        Epic epic = new Epic();
        epic.setId(1);
        epic.getSubtasksIds().add(2);
        epic.getSubtasksIds().add(3);
        Subtask subtask1 = new Subtask(1);
        subtask1.setId(2);
        Subtask subtask2 = new Subtask(1);
        subtask2.setId(3);
        when(storage.getEpic(1)).thenReturn(epic);
        when(storage.getSubtask(2)).thenReturn(subtask1);
        when(storage.getSubtask(3)).thenReturn(subtask2);

        manager.updateEpic(epic);

        assertEquals(TaskStatus.NEW, manager.getEpic(1).getStatus());
    }

    @Test
    void checkEpicStatus_whenDifferentSubtasksStatus_thenEpicStatusInProgress() {
        Epic epic = new Epic();
        epic.setId(1);
        epic.getSubtasksIds().add(2);
        epic.getSubtasksIds().add(3);
        Subtask subtask1 = new Subtask(1);
        subtask1.setId(2);
        Subtask subtask2 = new Subtask(1);
        subtask2.setId(3);
        subtask1.setStatus(TaskStatus.DONE);
        when(storage.getEpic(1)).thenReturn(epic);
        when(storage.getSubtask(2)).thenReturn(subtask1);
        when(storage.getSubtask(3)).thenReturn(subtask2);

        manager.updateEpic(epic);

        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpic(1).getStatus());
    }

    @Test
    void checkEpicStatus_whenAllSubtaskDone_thenEpicStatusDone() {
        Epic epic = new Epic();
        epic.setId(1);
        epic.getSubtasksIds().add(2);
        epic.getSubtasksIds().add(3);
        Subtask subtask1 = new Subtask(1);
        subtask1.setId(2);
        Subtask subtask2 = new Subtask(1);
        subtask2.setId(3);
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        when(storage.getEpic(1)).thenReturn(epic);
        when(storage.getSubtask(2)).thenReturn(subtask1);
        when(storage.getSubtask(3)).thenReturn(subtask2);

        manager.updateEpic(epic);

        assertEquals(TaskStatus.DONE, manager.getEpic(1).getStatus());
    }

    @Test
    void updateSubtask_whenStatusChange_thenEpicTimesChange(){
        Epic epic = new Epic();
        epic.setId(1);
        epic.getSubtasksIds().add(2);
        Subtask subtask1 = new Subtask(1);
        subtask1.setId(2);
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        subtask1.setStartTime(LocalDateTime.of(2024, 4, 24, 10, 20));
        subtask1.setDuration(Duration.ofMinutes(20));
        when(storage.getEpic(1)).thenReturn(epic);
        when(storage.getSubtask(2)).thenReturn(subtask1);
        when(storage.getSubtasks()).thenReturn(List.of(subtask1));

        manager.updateSubtask(subtask1);
        Duration duration = subtask1.getDuration();
        LocalDateTime localDateTime = subtask1.getStartTime();
        LocalDateTime expectedEpicEndTime = localDateTime.plusMinutes(duration.toMinutes());

        assertEquals(expectedEpicEndTime, epic.getEndTime());
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void addTask_whenDurationIntersected_thenThrowManagerSaveException(){
        Task task1 = new Task();
        task1.setStartTime(LocalDateTime.of(2024, 4, 24, 11, 0));
        task1.setDuration(Duration.ofMinutes(20));
        Task task2 = new Task();
        task2.setStartTime(LocalDateTime.of(2024, 4, 24, 11, 10));
        task2.setDuration(Duration.ofMinutes(30));
        manager.addTask(task1);
        Assertions.assertThrows(ManagerSaveException.class, ()-> manager.addTask(task2));
    } */
}