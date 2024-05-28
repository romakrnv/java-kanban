package com.kanban;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.kanban.model.Task;
import com.kanban.service.HttpTaskServer;
import com.kanban.service.InMemoryTaskManager;
import com.kanban.service.Managers;
import com.kanban.service.TaskManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {

    public static void main(String[] args) throws IOException {
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        TaskManager tm = Managers.getDefault();
        Task task1 = new Task();
        task1.setStartTime(LocalDateTime.now());
        Task task2 = new Task();
        task2.setStartTime(LocalDateTime.now());
        tm.addTask(task1);
        tm.addTask(task2);
        System.out.println(tm.getAllTasks());
        //System.out.println(gson.toJson(tm.getAllTasks()));
        tm.addTask(new Task());
        HttpTaskServer httpTaskServer = new HttpTaskServer(tm);


        }
    /*
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        Task task = new Task();
        task.setStartTime(LocalDateTime.now());
        String jsonString = gson.toJson(task);
        System.out.println(jsonString);

    }*/
    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private  final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        @Override
        public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
            jsonWriter.value(localDateTime.format(dtf));
        }

        @Override
        public LocalDateTime read(final JsonReader jsonReader) throws IOException {
            return LocalDateTime.parse(jsonReader.nextString(), dtf);
        }
    }
}
