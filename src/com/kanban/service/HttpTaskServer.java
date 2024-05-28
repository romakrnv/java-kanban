package com.kanban.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.kanban.storage.Storage;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HttpTaskServer {
    TaskManager taskManager;
    HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        this.httpServer = HttpServer.create(new InetSocketAddress(8080), 0); //1 создали сервер
        httpServer.createContext("/task", new TasksHandler(taskManager));
        httpServer.start();
    }

    public void stopServer() {
        httpServer.stop(1);
    }
}


class TasksHandler implements HttpHandler {
    TaskManager taskManager;
    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method){
            case "GET":
                getTask(exchange);
                break;
            default:
                System.out.println("sww");
        }
        exchange.close();
    }

    private void getTask(HttpExchange exchange) throws IOException{
        String[] splitPath = exchange.getRequestURI().getPath().split("/");
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
        if (splitPath.length == 2) {
            BaseHttpHandler.writeResponse(exchange, gson.toJson(taskManager.getAllTasks()), 200);
            return;
        } /*else if (splitPath.length == 3) {
            BaseHttpHandler<Integer> taskIdOptional = BaseHttpHandler.getTaskId(exchange);
            if (taskIdOptional.isPresent()) {
                if (inMemoryTasksManager.getTaskByID(taskIdOptional.get()) != null) {
                    BaseHttpHandler.writeResponse(exchange, gson.toJson(inMemoryTasksManager.getTaskByID(taskIdOptional.get())), 200);
                    return;
                }
            }
        }*/
        BaseHttpHandler.writeResponse(exchange, "Not found", 404);
    }

    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private  final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        @Override
        public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
            if(localDateTime == null){
                jsonWriter.nullValue();
                return;
            }
            jsonWriter.value(localDateTime.format(dtf));
        }

        @Override
        public LocalDateTime read(final JsonReader jsonReader) throws IOException {
            return LocalDateTime.parse(jsonReader.nextString(), dtf);
        }
    }

    static class BaseHttpHandler {
        protected static void writeResponse(HttpExchange exchange, String response, int responseCode) throws IOException {
            try (OutputStream os = exchange.getResponseBody()){

                exchange.sendResponseHeaders(responseCode, 0);
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
        }
    }
}