package com.kanban.service;

import com.kanban.model.Epic;
import com.kanban.model.Subtask;
import com.kanban.model.Task;
import com.kanban.storage.Storage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private TaskManager fileManager;

    @BeforeEach
    void setUp() {
        fileManager = new FileBackedTaskManager(new InMemoryHistoryManager(),
                new Storage(), new File("resources/test.csv"));
        fileManager.addTask(new Task());
        fileManager.addEpic(new Epic());
        fileManager.addSubtask(new Subtask(2));
    }

    @Test
    void loadFromFile_whenFileExist_thenReturnTasks() {
        TaskManager managerLoadedFromFile = FileBackedTaskManager
                .loadFromFile(new File("resources/test.csv"));

        assertEquals(fileManager.getAllTasks(), managerLoadedFromFile.getAllTasks(),
                "Tasks are not matching");
        assertEquals(fileManager.getAllEpics(), managerLoadedFromFile.getAllEpics(),
                "Epics are not matching");
        assertEquals(fileManager.getAllSubtasks(), managerLoadedFromFile.getAllSubtasks(),
                "Subtasks are not matching");

    }

    @Test
    void loadFromFile_whenFileIsEmpty_thenReturnSize0() {
        TaskManager managerWithEmptyFile = FileBackedTaskManager
                .loadFromFile(new File("resources/emptyfile.csv"));

        assertEquals(0, managerWithEmptyFile.getAllTasks().size());
        assertEquals(0, managerWithEmptyFile.getAllEpics().size());
        assertEquals(0, managerWithEmptyFile.getAllSubtasks().size());
    }

    @Test
    void save_whenCreateNewTask_thenFileSizeEqualMap() throws IOException {
        fileManager.addTask(new Task());
        Epic epic = new Epic();
        fileManager.addEpic(epic);
        fileManager.addSubtask(new Subtask(epic.getId()));
        int mapSize = fileManager.getAllTasks().size()
                + fileManager.getAllSubtasks().size()
                + fileManager.getAllEpics().size();
        File file = new File("resources/test.csv");
        String dataFromFile = Files.readString(file.toPath());
        String[] splitData = dataFromFile.split("\n");
        ArrayList<String> check = new ArrayList<>(Arrays.asList("Task", "Epic", "Subtask"));
        int fileTaskSize = 0;
        for (String value : splitData) {
            String[] data = value.split(",");
            if (check.contains(data[0])) {
                fileTaskSize++;
            }
        }
        assertEquals(mapSize, fileTaskSize, "Size doesn't match");
    }
}