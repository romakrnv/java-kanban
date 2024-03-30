package com.kanban.service;

import com.kanban.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void add_whenHistoryIsEmpty_thenReturnSize1() {
        createTask(1);
        int actualSize = historyManager.getHistory().size();

        assertEquals(1, actualSize);
    }

    @Test
    void add_whenRecordAlreadyExist_thenReturnSize1() {
        Task task = new Task();
        task.setId(1);
        historyManager.add(task);

        historyManager.add(task);
        int actualSize = historyManager.getHistory().size();

        assertEquals(1, actualSize);
    }

    @Test
    void remove_whenRecordExist_thenHistoryIsEmpty() {
        createTask(1);

        historyManager.remove(1);
        boolean isEmpty = historyManager.getHistory().isEmpty();

        assertTrue(isEmpty);
    }

    @Test
    void add_whenRecordIsHead_thenRemoveRecord() {
        Task task1 = createTask(1);
        createTask(2);
        createTask(3);

        historyManager.add(task1);
        int curHeadId = historyManager.getHistory().get(0).getId();

        assertEquals(2, curHeadId);
    }

    @Test
    void add_whenRecordIsTail_thenRemoveRecord() {
        createTask(1);
        createTask(2);
        Task task3 = createTask(3);

        historyManager.add(task3);
        int curTailId = historyManager.getHistory().get(1).getId();

        assertEquals(2, curTailId);
    }

    @Test
    void add_whenRecordInTheMiddle_thenRemoveRecord() {
        createTask(1);
        Task task2 = createTask(2);
        createTask(3);

        historyManager.add(task2);
        int curMiddleId = historyManager.getHistory().get(1).getId();

        assertEquals(3, curMiddleId);
    }


    @Test
    void getHistory_when3RecordsExist_thenReturnHistoryList() {
        Task task1 = createTask(1);
        Task task2 = createTask(2);
        Task task3 = createTask(3);

        List<Task> expectedHistory = List.of(task1, task2, task3);

        assertEquals(expectedHistory, historyManager.getHistory());
    }

    private Task createTask(int id) {
        Task task = new Task();
        task.setId(id);
        historyManager.add(task);
        return task;
    }
}