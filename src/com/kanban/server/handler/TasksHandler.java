package com.kanban.server.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kanban.model.Task;
import com.kanban.service.ManagerSaveException;
import com.kanban.service.TaskManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                getTask(exchange);
                break;
            case "POST":
                createOrUpdateTask(exchange);
                break;
            case "DELETE":
                deleteTask(exchange);
                break;
            default:
                writeBadRequest(exchange);
        }
        exchange.close();
    }

    private void getTask(HttpExchange exchange) throws IOException {
        String[] splitPath = exchange.getRequestURI().getPath().split("/");
        switch (splitPath.length) {
            case 2: {
                writeResponse(exchange, gson.toJson(taskManager.getAllTasks()), 200);
                break;
            }
            case 3: {
                Optional<Integer> optionalID = getOptionalID(splitPath[2]);
                if (optionalID.isEmpty()) {
                    writeFormatException(exchange);
                    return;
                }
                Task task = taskManager.getTask(optionalID.get());
                if (task == null) {
                    writeNotFound(exchange);
                    return;
                }
                writeResponse(exchange, gson.toJson(task), 200);
                break;
            }
            default: {
                writeBadRequest(exchange);
            }
        }
    }

    private void createOrUpdateTask(HttpExchange exchange) throws IOException {
        String[] splitPath = exchange.getRequestURI().getPath().split("/");
        if (splitPath.length != 2) {
            writeBadRequest(exchange);
            return;
        }
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        JsonElement jsonElement = JsonParser.parseString(body);
        if (!jsonElement.isJsonObject()) {
            writeFormatException(exchange);
            return;
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Task taskFromJson = gson.fromJson(jsonObject, Task.class);
        if (taskFromJson.getId() == null) {
            try {
                Task task = taskManager.addTask(taskFromJson);
                if (task == null) {
                    writeInternalServerError(exchange);
                    return;
                }
                writeResponse(exchange, gson.toJson(task), 200);
                return;

            } catch (ManagerSaveException e) {
                writeTaskIntersectError(exchange);
                return;
            }
        }
        try {
            Task task = taskManager.updateTask(taskFromJson);
            if (task == null) {
                writeInternalServerError(exchange);
                return;
            }
            writeResponse(exchange, gson.toJson(task), 200);

        } catch (ManagerSaveException e) {
            writeTaskIntersectError(exchange);
        }
    }

    private void deleteTask(HttpExchange exchange) throws IOException {
        String[] splitPath = exchange.getRequestURI().getPath().split("/");

        switch (splitPath.length) {
            case 2: {
                taskManager.removeAllTasks();
                writeNoContent(exchange);
                break;
            }
            case 3: {
                Optional<Integer> optionalID = getOptionalID(splitPath[2]);
                if (optionalID.isEmpty()) {
                    writeFormatException(exchange);
                    return;
                }
                taskManager.removeTask(optionalID.get());
                writeNoContent(exchange);
                break;
            }
            default: {
                writeBadRequest(exchange);
            }
        }
    }
}
