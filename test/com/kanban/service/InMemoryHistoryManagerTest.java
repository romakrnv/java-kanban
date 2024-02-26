package com.kanban.service;

import com.kanban.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void add_whenOneRecordExist_thenReturn2() {
        historyManager.getHistory().add(new Task());

        historyManager.add(new Task());
        int actualCount = historyManager.getHistory().size();

        assertEquals(2, actualCount);
    }

    @Test
    void add_whenRecordsMoreThanMax_thenReturn10() {
        for (int i = 0; i < 10; i++) {
            historyManager.add(new Task());
        }

        historyManager.add(new Task());
        int actualCount = historyManager.getHistory().size();

        assertEquals(10, actualCount);
    }
}