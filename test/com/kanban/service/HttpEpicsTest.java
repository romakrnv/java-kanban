package com.kanban.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class HttpEpicsTest {
    private static final String DEFAULT_URI = "http://localhost:8080/epics";
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
    public void addEpic() throws IOException, InterruptedException {
        Epic epic = new Epic();
        epic.setName("name");
        String taskJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(DEFAULT_URI);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Epic> epicsFromManager = taskManager.getAllEpics();
        assertEquals(1, epicsFromManager.size(), "incorrect epics qty");
        assertEquals(epic.getName(), epicsFromManager.get(0).getName(), "incorrect epic name");
    }


    @Test
    public void getEpicByID() throws IOException, InterruptedException {
        Epic epic = taskManager.addEpic(new Epic());
        int id = epic.getId();
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(DEFAULT_URI + "/" + id);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(epicJson, response.body());
    }

    @Test
    public void getEpics() throws IOException, InterruptedException {
        Epic epic1 = taskManager.addEpic(new Epic());
        Epic epic2 = taskManager.addEpic(new Epic());

        String taskJson1 = gson.toJson(epic1);
        String taskJson2 = gson.toJson(epic2);

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
    public void getAllEpicsSubtasks() throws IOException, InterruptedException {
        Epic epic1 = taskManager.addEpic(new Epic());
        Subtask subtask1 = taskManager.addSubtask(new Subtask(epic1.getId()));
        Subtask subtask2 = taskManager.addSubtask(new Subtask(epic1.getId()));

        String subtask1ToJson = gson.toJson(subtask1);
        String subtask2ToJson = gson.toJson(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(DEFAULT_URI+"/" + epic1.getId() + "/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String er = ("[" + subtask1ToJson + "," + subtask2ToJson + "]");
        er = removeSpaces(er);

        assertEquals(200, response.statusCode());
        assertEquals(er, removeSpaces(response.body()));
    }


    @Test
    public void deleteTask() throws IOException, InterruptedException {
        Epic epic = taskManager.addEpic(new Epic());
        int id = epic.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(DEFAULT_URI + "/" + id);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(204, response.statusCode());
        assertNull(taskManager.getEpic(id));
    }

    private String removeSpaces(String string) {
        return string.replaceAll(" ", "").replaceAll("\n", "");
    }
}