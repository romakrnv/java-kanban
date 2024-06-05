package com.kanban.server.handler;

import com.kanban.service.TaskManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if (method.equals("GET")) {
            getHistory(exchange);
        } else {
            writeBadRequest(exchange);
        }
        exchange.close();
    }


    private void getHistory(HttpExchange exchange) throws IOException {
        String[] splitPath = exchange.getRequestURI().getPath().split("/");
        if (splitPath.length != 2) {
            writeBadRequest(exchange);
        }
        writeResponse(exchange, gson.toJson(taskManager.getHistory()), 200);
    }
}
