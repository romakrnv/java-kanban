package com.kanban.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kanban.model.Task;
import com.kanban.server.HttpTaskServer;
import com.kanban.storage.Storage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class HttpTasksTest {
    private static final String DEFAULT_URI = "http://localhost:8080/tasks";
    private TaskManager taskManager;
    HttpTaskServer taskServer;
    Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager(), new Storage());
        gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new HttpTaskServer.LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
        taskServer = new HttpTaskServer(taskManager);
    }

    @AfterEach
    public void shutDown() {
        taskServer.stopServer();
    }

    @Test
    public void addTask() throws IOException, InterruptedException {
        Task task = createTask();
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(DEFAULT_URI);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = taskManager.getAllTasks();
        assertEquals(1, tasksFromManager.size(), "incorrect tasks qty");
        assertEquals(task.getName(), tasksFromManager.get(0).getName(), "incorrect task name");
    }

    @Test
    public void addTask_whenTaskIntersect_thenThrowManagerSaveException() throws IOException, InterruptedException {
        Task task1 = new Task();
        task1.setStartTime(LocalDateTime.of(2024, 4, 24, 11, 0));
        task1.setDuration(Duration.ofMinutes(20));
        taskManager.addTask(task1);
        Task task2 = new Task();
        task2.setStartTime(LocalDateTime.of(2024, 4, 24, 11, 10));
        task2.setDuration(Duration.ofMinutes(30));

        String taskJson = gson.toJson(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(DEFAULT_URI);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
        assertEquals(1, taskManager.getAllTasks().size(), "incorrect tasks qty");
    }

    @Test
    public void updateTask_whenTaskIntersect_thenThrowManagerSaveException() throws IOException, InterruptedException {
        Task task1 = new Task();
        task1.setStartTime(LocalDateTime.of(2024, 4, 24, 11, 0));
        task1.setDuration(Duration.ofMinutes(20));
        taskManager.addTask(task1);
        Task task2 = new Task();
        task2.setStartTime(LocalDateTime.of(2023, 4, 24, 11, 10));
        task2.setDuration(Duration.ofMinutes(0));
        taskManager.addTask(task2);

        Task taskForUpdate = new Task();
        taskForUpdate.setStartTime(task2.getStartTime());
        taskForUpdate.setId(task2.getId());
        taskForUpdate.setStartTime(LocalDateTime.of(2024, 4, 24, 11, 10));
        taskForUpdate.setDuration(Duration.ofMinutes(30));

        String taskJson = gson.toJson(taskForUpdate);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(DEFAULT_URI);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
        assertEquals(2, taskManager.getAllTasks().size(), "incorrect tasks qty");
    }

    @Test
    public void updateTask() throws IOException, InterruptedException {
        Task taskBeforeUpdate = createTask();
        taskManager.addTask(taskBeforeUpdate);
        Task taskForUpdate = new Task();
        taskForUpdate.setId(taskBeforeUpdate.getId());
        taskForUpdate.setName("ChangedName");
        String taskJson = gson.toJson(taskForUpdate);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(DEFAULT_URI);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = taskManager.getAllTasks();
        assertEquals(1, tasksFromManager.size(), "incorrect tasks qty");
        assertEquals(taskForUpdate.getName(), tasksFromManager.get(0).getName(), "incorrect task name");
    }

    @Test
    public void getTaskByID() throws IOException, InterruptedException {
        Task task = taskManager.addTask(createTask());
        int id = task.getId();
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(DEFAULT_URI + "/" + id);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(taskJson, response.body());
    }

    @Test
    public void getTasks() throws IOException, InterruptedException {
        Task task1 = taskManager.addTask(createTask());
        Task task2 = taskManager.addTask(createTask());

        String taskJson1 = gson.toJson(task1);
        String taskJson2 = gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(DEFAULT_URI);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String er = ("[" + taskJson1 + "," + taskJson2 + "]");
        er = removeSpaces(er);

        assertEquals(200, response.statusCode());
        assertEquals(er, removeSpaces(response.body()));
    }

    @Test
    public void deleteTask() throws IOException, InterruptedException {
        Task task1 = taskManager.addTask(createTask());
        int id = task1.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(DEFAULT_URI + "/" + id);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(204, response.statusCode());
        assertNull(taskManager.getTask(id));
    }

    @Test
    public void getHistory() throws IOException, InterruptedException {
        Task task1 = taskManager.addTask(createTask());
        Task task2 = taskManager.addTask(createTask());
        String taskJson1 = gson.toJson(task1);
        String taskJson2 = gson.toJson(task2);
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String er = ("[" + taskJson1 + "," + taskJson2 + "]");
        er = removeSpaces(er);

        assertEquals(200, response.statusCode());
        assertEquals(er, removeSpaces(response.body()));
    }

    @Test
    public void getPrioritized() throws IOException, InterruptedException {
        Task task1 = createTask();
        task1.setStartTime(LocalDateTime.of(2023, Month.JUNE, 20, 11, 0));
        task1.setDuration(Duration.ofMinutes(30));
        Task task2 = createTask();
        task2.setStartTime(LocalDateTime.of(2023, Month.MARCH, 19, 10, 0));
        task2.setDuration(Duration.ofMinutes(40));
        Task task3 = createTask();
        task3.setStartTime(LocalDateTime.of(2023, Month.MAY, 21, 19, 0));
        task3.setDuration(Duration.ofMinutes(50));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        String taskJson1 = gson.toJson(task1);
        String taskJson2 = gson.toJson(task2);
        String taskJson3 = gson.toJson(task3);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String er = ("[" + taskJson2 + "," + taskJson3 + "," + taskJson1 + "]");
        er = removeSpaces(er);

        assertEquals(200, response.statusCode());
        assertEquals(er, removeSpaces(response.body()));
    }

    private Task createTask() {
        Task task = new Task();
        task.setName("task");
        task.setDescription("some text");
        task.setStartTime(LocalDateTime.now());
        return task;
    }

    private String removeSpaces(String string) {
        return string.replaceAll(" ", "").replaceAll("\n", "");
    }
} 