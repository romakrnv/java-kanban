package com.kanban.server.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kanban.model.Epic;
import com.kanban.model.Subtask;
import com.kanban.service.TaskManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                getEpic(exchange);
                break;
            case "POST":
                createEpic(exchange);
                break;
            case "DELETE":
                deleteEpic(exchange);
                break;
            default:
                writeBadRequest(exchange);
        }
        exchange.close();
    }

    private void getEpic(HttpExchange exchange) throws IOException {
        String[] splitPath = exchange.getRequestURI().getPath().split("/");
        switch (splitPath.length) {
            case 2: {
                writeResponse(exchange, gson.toJson(taskManager.getAllEpics()), 200);
                break;
            }
            case 3: {
                Optional<Integer> optionalID = getOptionalID(splitPath[2]);
                if (optionalID.isEmpty()) {
                    writeFormatException(exchange);
                    return;
                }
                Epic epic = taskManager.getEpic(optionalID.get());
                if (epic == null) {
                    writeNotFound(exchange);
                    return;
                }
                writeResponse(exchange, gson.toJson(epic), 200);
                break;
            }
            case 4: {
                if (!splitPath[3].equals("subtasks")) {
                    writeBadRequest(exchange);
                }
                Optional<Integer> optionalID = getOptionalID(splitPath[2]);
                if (optionalID.isEmpty()) {
                    writeFormatException(exchange);
                    return;
                }
                List<Subtask> epicSubtasks = taskManager.getAllEpicsSubtasks(optionalID.get());
                if (epicSubtasks == null) {
                    writeNotFound(exchange);
                    return;
                }
                writeResponse(exchange, gson.toJson(epicSubtasks), 200);
                break;
            }
            default: {
                writeBadRequest(exchange);
            }
        }
    }

    private void createEpic(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        JsonElement jsonElement = JsonParser.parseString(body);
        if (!jsonElement.isJsonObject()) {
            writeFormatException(exchange);
            return;
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Epic epicFromJson = gson.fromJson(jsonObject, Epic.class);
        String[] splitPath = exchange.getRequestURI().getPath().split("/");
        if (splitPath.length != 2) {
            writeBadRequest(exchange);
            return;
        }
        Epic epic = taskManager.addEpic(epicFromJson);
        if (epic == null) {
            writeInternalServerError(exchange);
            return;
        }
        writeResponse(exchange, gson.toJson(epic), 200);
    }

    private void deleteEpic(HttpExchange exchange) throws IOException {
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
        taskManager.removeEpic(optionalID.get());
        writeNoContent(exchange);
    }
}
