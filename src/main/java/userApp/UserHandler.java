package userApp;

import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserHandler implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(UserHandler.class);
    private final UserStorage userStorage = new UserStorage();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .setPrettyPrinting()
            .create();


    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        UserHandler userHandler = new UserHandler();

        server.createContext("/users", userHandler);
        server.start();
        logger.info("Сервер запущен");
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String jsonResponse = "";
        String requestPath = exchange.getRequestURI().getPath();
        String[] pathParts = requestPath.split("/");

        switch (exchange.getRequestMethod()) {
            case "GET":
                if (pathParts.length == 3) {
                    logger.info("Получение пользователя по айди {}", Integer.parseInt(pathParts[2]));
                    User user = userStorage.getUserById(Integer.parseInt(pathParts[2]));
                    jsonResponse = gson.toJson(user);
                    sendText(exchange, jsonResponse, 200);
                } else {
                    sendText(exchange, "ошибка в запросе", 404);
                    logger.warn("Неудачная попытка получить пользователя по айди");
                }
                break;

            case "POST":
                logger.info("Добавление нового пользователя");
                User newUser = readRequestBody(exchange);
                jsonResponse = gson.toJson(userStorage.addUser(newUser));
                sendText(exchange, jsonResponse, 201);
                break;

            case "PUT":
                User updatedUser = readRequestBody(exchange);
                logger.info("Обновление пользователя с id {}", updatedUser.getId());
                jsonResponse = gson.toJson(userStorage.updateUser(updatedUser));
                sendText(exchange, jsonResponse, 200);
                break;

            case "DELETE":
                if (pathParts.length == 3) {
                    logger.info("Удаление пользователя с id {}", Integer.parseInt(pathParts[2]));
                    userStorage.remove(Integer.parseInt(pathParts[2]));
                    sendText(exchange, "User удален", 200);
                } else {
                    sendText(exchange, "ошибка в запросе", 404);
                    logger.warn("Неудачная попытка удаления пользователя");
                }
                break;

            default:
                sendText(exchange, "ошибка в запросе", 405);
                logger.warn("Ошибка в запросе");
                break;
        }
    }

    private User readRequestBody(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        return gson.fromJson(reader, User.class);
    }

    private void sendText(HttpExchange h, String text, int code) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(code, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }
}