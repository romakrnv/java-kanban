package com.kanban.server.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kanban.model.Subtask;
import com.kanban.service.ManagerSaveException;
import com.kanban.service.TaskManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class SubTasksHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;

    public SubTasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                getSubTask(exchange);
                break;
            case "POST":
                createOrUpdateSubTask(exchange);
                break;
            case "DELETE":
                deleteSubTask(exchange);
                break;
            default:
                writeBadRequest(exchange);
        }
        exchange.close();
    }

    private void getSubTask(HttpExchange exchange) throws IOException {
        String[] splitPath = exchange.getRequestURI().getPath().split("/");
        switch (splitPath.length) {
            case 2: {
                writeResponse(exchange, gson.toJson(taskManager.getAllSubtasks()), 200);
                break;
            }
            case 3: {
                Optional<Integer> optionalID = getOptionalID(splitPath[2]);
                if (optionalID.isEmpty()) {
                    writeFormatException(exchange);
                    return;
                }
                Subtask subtask = taskManager.getSubtask(optionalID.get());
                if (subtask == null) {
                    writeNotFound(exchange);
                    return;
                }
                writeResponse(exchange, gson.toJson(subtask), 200);
                break;
            }
            default: {
                writeBadRequest(exchange);
            }
        }
    }

    private void createOrUpdateSubTask(HttpExchange exchange) throws IOException {
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
        Subtask subTaskFromJson = gson.fromJson(jsonObject, Subtask.class);
        if (subTaskFromJson.getId() == null) {
            try {
                Subtask subtask = taskManager.addSubtask(subTaskFromJson);
                if (subtask == null) {
                    writeInternalServerError(exchange);
                    return;
                }
                writeResponse(exchange, gson.toJson(subtask), 200);
                return;

            } catch (ManagerSaveException e) {
                writeTaskIntersectError(exchange);
                return;
            }
        }
        try {
            Subtask subtask = taskManager.updateSubtask(subTaskFromJson);
            if (subtask == null) {
                writeInternalServerError(exchange);
                return;
            }
            writeResponse(exchange, gson.toJson(subtask), 200);
        } catch (ManagerSaveException e) {
            writeTaskIntersectError(exchange);
        }
    }

    private void deleteSubTask(HttpExchange exchange) throws IOException {
        String[] splitPath = exchange.getRequestURI().getPath().split("/");

        if (splitPath.length != 3) {
            writeBadRequest(exchange);
            return;
        }
        Optional<Integer> optionalID = getOptionalID(splitPath[2]);
        if (optionalID.isEmpty()) {
            writeFormatException(exchange);
            return;
        }
        taskManager.removeSubtask(optionalID.get());
        writeNoContent(exchange);
    }
}
