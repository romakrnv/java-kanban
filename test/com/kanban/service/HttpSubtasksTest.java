package com.kanban.service;

import com.google.gson.Gson;
import com.kanban.model.Epic;
import com.kanban.model.Subtask;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class HttpSubtasksTest {
    private static final String DEFAULT_URI = "http://localhost:8080/subtasks";
    private TaskManager taskManager;
    HttpTaskServer taskServer;
    Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager(), new Storage());
        gson = TestUtils.getGson();
        taskServer = new HttpTaskServer(taskManager);
    }

    @AfterEach
    public void shutDown() {
        taskServer.stopServer();
    }

    @Test
    public void addSubTask() throws IOException, InterruptedException {
        Epic epic = taskManager.addEpic(new Epic());
        Subtask subtask = new Subtask(epic.getId());
        subtask.setName("subtask name");
        String taskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(DEFAULT_URI);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Subtask> subtasksFromManager = taskManager.getAllSubtasks();
        assertEquals(1, subtasksFromManager.size(), "incorrect tasks qty");
        assertEquals(subtask.getName(), subtasksFromManager.get(0).getName(), "incorrect task name");
    }

    @Test
    public void updateSubTask() throws IOException, InterruptedException {
        Epic epic = taskManager.addEpic(new Epic());
        Subtask subtaskBeforeUpdate = taskManager.addSubtask(new Subtask(epic.getId()));
        subtaskBeforeUpdate.setName("subtask name");
        subtaskBeforeUpdate.setDescription("some text");
        Subtask subtaskForUpdate = new Subtask(epic.getId());
        subtaskForUpdate.setId(subtaskBeforeUpdate.getId());
        subtaskForUpdate.setName("changed name");
        subtaskForUpdate.setDescription("changed text");
        String taskJson = gson.toJson(subtaskForUpdate);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(DEFAULT_URI);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Subtask> subtasksFromManager = taskManager.getAllSubtasks();
        assertEquals(1, subtasksFromManager.size(), "incorrect tasks qty");
        assertEquals(subtaskForUpdate.getName(), subtasksFromManager.get(0).getName(), "incorrect task name");
        assertEquals(subtaskForUpdate.getDescription(), subtasksFromManager.get(0).getDescription());
    }

    @Test
    public void getSubTaskByID() throws IOException, InterruptedException {
        Epic epic = taskManager.addEpic(new Epic());
        Subtask subtask = taskManager.addSubtask(new Subtask(epic.getId()));
        String taskJson = gson.toJson(subtask);
        int id = subtask.getId();

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
    public void getSubTasks() throws IOException, InterruptedException {
        Epic epic = taskManager.addEpic(new Epic());
        Subtask subtask1 = taskManager.addSubtask(new Subtask(epic.getId()));
        Subtask subtask2 = taskManager.addSubtask(new Subtask(epic.getId()));

        String taskJson1 = gson.toJson(subtask1);
        String taskJson2 = gson.toJson(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(DEFAULT_URI);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String er = ("[" + taskJson1 + "," + taskJson2 + "]");
        er = TestUtils.removeSpaces(er);

        assertEquals(200, response.statusCode());
        assertEquals(er, TestUtils.removeSpaces(response.body()));
    }

    @Test
    public void deleteTask() throws IOException, InterruptedException {
        Epic epic = taskManager.addEpic(new Epic());
        Subtask subtask = taskManager.addSubtask(new Subtask(epic.getId()));
        int id = subtask.getId();

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
}