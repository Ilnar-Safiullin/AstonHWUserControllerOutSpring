package userApp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import userApp.manager.ServiceManager;
import userApp.model.User;
import userApp.service.UserService;
import userApp.utils.ConsoleInputProvider;

public class UserHandler {
    private static final Logger logger = LoggerFactory.getLogger(UserHandler.class);
    private static final UserService userService = ServiceManager.getDefault(); //изолировал репозиторий от "контроллера"
    private static final ConsoleInputProvider inputProvider = new ConsoleInputProvider();


    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            printMenu();
            switch (inputProvider.getInput()) {
                case "1" -> createUser();
                case "2" -> updateUser();
                case "3" -> deleteUser();
                case "4" -> getUserById();
                case "5" -> running = false;
                default -> System.out.println("Ввели неверное число");
            }
        }
        System.out.println("Всего Доброго");
    }

    /*
    Я здесь не обрабатывал такие моменты как к примеру если почта введена без @ и с пробелами или к примеру имя
    пользователя с цифрами, так как потом в спринге просто аннотацией и валидацией закроем эти моменты, а сейчас просто
    расширять код не хочу, но если нужно добавлю. Спасибо
    Добавил проброс ошибки в ДЗ №2
    Добавил сервисный слой и изолировал репозиторий от "контроллера"
    Добавил inputProvider иначе не мог сделать мок тесты для UserService
    Нужно ли мне тут обрабатывать в кетче UserNotFoundException? чтобы логировать эту ошибку, или это уже лишнее?
     */


    private static void createUser() {
        logger.info("Создание нового пользователя");
        System.out.println("Добавление нового пользователя\n");

        String name = promptForInput("Введите имя");
        String email = promptForInput("Введите email");
        int age = promptForIntInput("Введите возраст");

        try {
            User user = userService.createUser(name, email, age);
            System.out.println("Пользователь добавлен: " + user);
            logger.info("Создан новый пользователь с id: {}", user.getId());
        } catch (Exception e) {
            System.out.println("Произошла ошибка при добавлении пользователя: " + e.getMessage());
            logger.error("Ошибка при добавлении пользователя", e);
        }
    }

    private static void updateUser() {
        System.out.println("Обновление пользователя");
        System.out.println("Введите id пользователя");

        int id = promptForIntInput("Введите номер айди пользователя");

        try {
            userService.updateUser(id, inputProvider);
        } catch (Exception e) {
            System.out.println("Произошла ошибка при обновлении пользователя: " + e.getMessage());
            logger.error("Ошибка при обновлении пользователя", e);
        }
    }

    private static void deleteUser() {
        System.out.println("Удаление пользователя");
        System.out.println("Введите id пользователя");

        int id = promptForIntInput("Введите номер айди пользователя");

        try {
            userService.deleteUser(id);
            System.out.println("Пользователь удален с id: " + id);
        } catch (Exception e) {
            System.out.println("Произошла ошибка при удалении пользователя: " + e.getMessage());
            logger.error("Ошибка при удалении пользователя", e);
        }
    }

    private static void getUserById() {
        System.out.println("Получение пользователя по айди");

        int id = promptForIntInput("Введите номер айди пользователя");

        try {
            User user = userService.getUserById(id);
            System.out.println(user);
        } catch (Exception e) {
            System.out.println("Произошла ошибка при получении пользователя: " + e.getMessage());
            logger.error("Ошибка при получении пользователя", e);
        }
    }

    private static String promptForInput(String message) {
        System.out.println(message);
        return inputProvider.getInput();
    }

    private static int promptForIntInput(String message) {
        System.out.println(message);
        while (!inputProvider.hasNextInt()) {
            System.out.println("Ошибка: введено не число. Введите число");
            inputProvider.getInput();
        }
        int value = inputProvider.nextInt();
        inputProvider.getInput();
        return value;
    }

    private static void printMenu() {
        System.out.println("Выберите пункт из меню:\n" +
                "1. Добавить пользователя\n" +
                "2. Обновить пользователя\n" +
                "3. Удалить пользователя\n" +
                "4. Найти пользователя по id\n" +
                "5. Выход"
        );
    }
}