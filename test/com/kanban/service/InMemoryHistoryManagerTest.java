package com.kanban.service;

import com.kanban.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        Task task = new Task();

        historyManager.add(task);
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
        Task task = new Task();
        task.setId(1);
        historyManager.add(task);

        historyManager.remove(1);
        boolean isEmpty = historyManager.getHistory().isEmpty();

        assertTrue(isEmpty);
    }

    @Test
    void add_whenRecordsIsHead_thenRemoveRecord() {
        Task task1 = new Task();
        Task task2 = new Task();
        Task task3 = new Task();
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.add(task1);
        int curHeadId = historyManager.getHistory().get(0).getId();

        assertEquals(2, curHeadId);
    }

    @Test
    void getHistory_when3RecordsExist_thenReturnHistorySize3() {
        Task task1 = new Task();
        Task task2 = new Task();
        Task task3 = new Task();
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        int actualSize = historyManager.getHistory().size();

        assertEquals(3, actualSize);
    }
}