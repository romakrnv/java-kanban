package com.kanban.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kanban.server.handler.utils.LocalDateTimeAdapter;

import java.time.LocalDateTime;

class TestUtils {
    static Gson getGson(){
        return new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
    }

    static String removeSpaces(String string) {
        return string.replaceAll(" ", "").replaceAll("\n", "");
    }
}
