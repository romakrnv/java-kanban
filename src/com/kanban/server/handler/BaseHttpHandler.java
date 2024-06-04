package com.kanban.server.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kanban.server.handler.utils.LocalDateTimeAdapter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

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
