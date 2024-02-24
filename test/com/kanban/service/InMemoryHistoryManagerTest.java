package com.kanban.service;

import com.kanban.model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager inMemoryHistoryManager;

    @BeforeEach
    void setUp() {
        inMemoryHistoryManager = new InMemoryHistoryManager();
    }

    @Test
    void add_whenOneRecordExist_thenReturn2() {
        inMemoryHistoryManager.getHistory().add(new Task());

        inMemoryHistoryManager.add(new Task());
        int actualCount = inMemoryHistoryManager.getHistory().size();

        Assertions.assertEquals(2, actualCount);
    }

    @Test
    void add_whenRecordsMoreThanMax_thenReturn10() {
        for(int i = 0; i < 10; i++){
            inMemoryHistoryManager.add(new Task());
        }

        inMemoryHistoryManager.add(new Task());
        int actualCount = inMemoryHistoryManager.getHistory().size();

        Assertions.assertEquals(10, actualCount);
    }
}