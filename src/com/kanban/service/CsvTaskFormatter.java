package com.kanban.service;

import com.kanban.model.Epic;
import com.kanban.model.Subtask;
import com.kanban.model.Task;
import com.kanban.model.TaskStatus;

import java.util.ArrayList;
import java.util.List;

public class CsvTaskFormatter {
    public static String createDataToSave(List<Task> tasks, List<Task> history) {
        StringBuilder dataToSave = new StringBuilder();
        dataToSave.append("type,name,description,id,status,epicId\n");

        for (Task task : tasks) {
            dataToSave.append(task.toString()).append("\n");
        }

        dataToSave.append("\n");
        if (history.isEmpty())
            dataToSave.append("History is empty");
        else {
            dataToSave.append(historyToString(history));
        }
        return dataToSave.toString();
    }

    private static String historyToString(List<Task> history) {
        StringBuilder historyDataToSave = new StringBuilder();
        for (Task task : history) {
            historyDataToSave.append(task.getId()).append(",");
        }
        return historyDataToSave.toString();
    }

    public static Task fromString(String value) {
        String[] data = value.split(",");
        String type = data[0];
        String name = data[1];
        String description = data[2];
        int id = Integer.parseInt(data[3]);
        TaskStatus status = TaskStatus.valueOf(data[4]);

        switch (type) {
            case "Task": {
                Task task = new Task();
                task.setId(id);
                task.setName(name);
                task.setDescription(description);
                task.setStatus(status);
                return task;
            }
            case "Epic": {
                Epic epic = new Epic();
                epic.setId(id);
                epic.setName(name);
                epic.setDescription(description);
                epic.setStatus(status);
                return epic;
            }
            case "Subtask": {
                int epicId = Integer.parseInt(data[5]);
                Subtask subtask = new Subtask(epicId);
                subtask.setId(id);
                subtask.setName(name);
                subtask.setDescription(description);
                subtask.setStatus(status);
                return subtask;
            }
            default: {
                throw new RuntimeException("unexpected task format");
            }
        }
    }

    static List<Integer> historyFromString(String value) {
        String regex = "^\\d+(,\\d+)*,$";
        List<Integer> data = new ArrayList<>();
        if (value.equals("History is empty") || value.isBlank()) {
            return data;
        }
        if (!value.matches(regex)) { //check that value matches the format 1,2,3,
            return data;
        }
        for (String id : value.split(",")) {
            data.add(Integer.parseInt(id));
        }
        return data;
    }
}