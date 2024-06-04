package com.kanban.server.handler;

import com.kanban.service.TaskManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if (method.equals("GET")) {
            getPrioritized(exchange);
        } else {
            writeBadRequest(exchange);
        }
        exchange.close();
    }


    private void getPrioritized(HttpExchange exchange) throws IOException {
        String[] splitPath = exchange.getRequestURI().getPath().split("/");
        if (splitPath.length != 2) {
            writeBadRequest(exchange);
        }
        writeResponse(exchange, gson.toJson(taskManager.getPrioritizedTasks()), 200);
    }
}
