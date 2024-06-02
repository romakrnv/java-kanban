package com.kanban.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.kanban.model.Epic;
import com.kanban.model.Subtask;
import com.kanban.model.Task;
import com.kanban.service.ManagerSaveException;
import com.kanban.service.TaskManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.List;

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


    class TasksHandler extends BaseHttpHandler implements HttpHandler {
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

    class SubTasksHandler extends BaseHttpHandler implements HttpHandler {
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

    class EpicsHandler extends BaseHttpHandler implements HttpHandler {
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

    class HistoryHandler extends BaseHttpHandler implements HttpHandler {
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

    class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
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

    public static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        @Override
        public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
            if (localDateTime == null) {
                jsonWriter.nullValue();
                return;
            }
            jsonWriter.value(localDateTime.format(dtf));
        }

        @Override
        public LocalDateTime read(final JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            }
            String dateStr = jsonReader.nextString();
            return LocalDateTime.parse(dateStr, dtf);
        }
    }

    class BaseHttpHandler {
        protected Gson gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();

        protected void writeResponse(HttpExchange exchange, String response, int responseCode) throws IOException {
            try (OutputStream os = exchange.getResponseBody()) {

                exchange.sendResponseHeaders(responseCode, 0);
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
        }

        protected void writeNotFound(HttpExchange exchange) throws IOException {
            writeResponse(exchange, "Not found", 404);
        }

        protected void writeFormatException(HttpExchange exchange) throws IOException {
            writeResponse(exchange, "Unprocessable Entity", 422);
        }

        protected void writeBadRequest(HttpExchange exchange) throws IOException {
            writeResponse(exchange, "Bad Request", 400);
        }

        protected void writeNoContent(HttpExchange exchange) throws IOException {
            writeResponse(exchange, "No Content", 204);
        }

        protected void writeInternalServerError(HttpExchange exchange) throws IOException {
            writeResponse(exchange, "Internal Server Error", 500);
        }

        protected void writeTaskIntersectError(HttpExchange exchange) throws IOException {
            writeResponse(exchange, "Error: Task Intersect", 406);
        }

        protected Optional<Integer> getOptionalID(String value) {
            try {
                return Optional.of(Integer.parseInt(value));
            } catch (NumberFormatException exception) {
                return Optional.empty();
            }
        }
    }
}