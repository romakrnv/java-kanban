package com.kanban.server;

import com.kanban.server.handler.EpicsHandler;
import com.kanban.server.handler.HistoryHandler;
import com.kanban.server.handler.PrioritizedHandler;
import com.kanban.server.handler.SubTasksHandler;
import com.kanban.server.handler.TasksHandler;
import com.kanban.service.TaskManager;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;


public class HttpTaskServer {
    TaskManager taskManager;
    HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        this.httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        httpServer.createContext("/tasks", new TasksHandler(taskManager));
        httpServer.createContext("/subtasks", new SubTasksHandler(taskManager));
        httpServer.createContext("/epics", new EpicsHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
        httpServer.start();
    }

    public void stopServer() {
        httpServer.stop(1);
    }
}