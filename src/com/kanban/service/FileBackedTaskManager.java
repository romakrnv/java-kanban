package com.kanban.service;

import com.kanban.model.Epic;
import com.kanban.model.Subtask;
import com.kanban.model.Task;
import com.kanban.storage.Storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static com.kanban.service.CsvTaskFormatter.createDataToSave;
import static com.kanban.service.CsvTaskFormatter.historyFromString;
import static com.kanban.service.CsvTaskFormatter.fromString;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(HistoryManager historyManager, Storage storage, File file) {
        super(historyManager, storage);
        this.file = file;
    }

    private void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            List<Task> dataToSave = new ArrayList<>();
            dataToSave.addAll(getAllTasks());
            dataToSave.addAll(getAllEpics());
            dataToSave.addAll(getAllSubtasks());
            bw.write(createDataToSave(dataToSave, getHistory()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static TaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(new InMemoryHistoryManager(), new Storage(), file);
        try {
            String dataFromFile = Files.readString(file.toPath());
            String[] splitData = dataFromFile.split("\n");

            for (int i = 1; i < splitData.length - 2; i++) {
                var task = fromString(splitData[i]);
                if (task instanceof Epic) {
                    manager.addEpic((Epic) task);
                } else if (task instanceof Subtask) {
                    manager.addSubtask((Subtask) task);
                } else {
                    manager.addTask(task);
                }
            }
            List<Integer> history = historyFromString(splitData[splitData.length - 1]);
            if (history.isEmpty()) {
                return manager;
            }
            for (int id : history) {
                if (manager.storage.getTask(id) != null) {
                    manager.historyManager.add(manager.getTask(id));
                }
                if (manager.storage.getEpic(id) != null) {
                    manager.historyManager.add(manager.getEpic(id));
                }
                if (manager.storage.getSubtask(id) != null) {
                    manager.historyManager.add(manager.getSubtask(id));
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("File loading error\n");
        }
        return manager;
    }

    @Override
    public Task addTask(Task task) {
        task = super.addTask(task);
        save();
        return task;
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public Task updateTask(Task task) {
        super.updateTask(task);
        save();
        return task;
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public Epic addEpic(Epic epic) {
        epic = super.addEpic(epic);
        save();
        return epic;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
        return epic;
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeAllSubtasks(int epicId) {
        super.removeAllSubtasks(epicId);
        save();
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        subtask = super.addSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }
}
