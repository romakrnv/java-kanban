package com.kanban.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void checkEqualsForSameTaskId() {
        Task task1 = new Task();
        task1.setId(1);
        Task task2 = new Task();
        task2.setId(1);

        assertEquals(task1, task2);
    }
}