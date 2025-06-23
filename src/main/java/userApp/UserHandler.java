package userApp;

import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import userApp.dao.UserStorage;
import userApp.exception.UserNotFoundException;
import userApp.model.User;

public class UserHandler {
    private static final Logger logger = LoggerFactory.getLogger(UserHandler.class);
    private static final UserStorage userStorage = new UserStorage();
    private static final Scanner scanner = new Scanner(System.in);


    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            printMenu();
            switch (scanner.nextLine()) {
                case "1" -> createUser();
                case "2" -> updateUser();
                case "3" -> deleteUser();
                case "4" -> getUerById();
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
     */


    private static void createUser() {
        logger.warn("Создание нового пользователя");
        System.out.println("Добавление нового пользователя\n");
        System.out.println("Введите имя");
        String name = scanner.nextLine();
        System.out.println("Введите email");
        String email = scanner.nextLine();
        System.out.println("Введите возраст");
        while (!scanner.hasNextInt()) {
            System.out.println("Ошибка: введено не число. Введите число");
            scanner.next();
        }
        int age = scanner.nextInt();
        scanner.nextLine();
        User user = new User(name, email, age);
        try {
            user = userStorage.addUser(user);
            System.out.println("Пользователь добавлен: " + user);
            logger.info("Создан новый пользователь с id: {}", user.getId());
        } catch (Exception e) {
            System.out.println("Произошла ошибка при добавлении пользователя: " + e.getMessage());
            logger.error("Ошибка при добавлении пользователя", e);
        }
    }

    private static void updateUser() {
        boolean chekUpdate = false;
        System.out.println("Обновление пользователя");
        System.out.println("Введите id пользователя");
        while (!scanner.hasNextInt()) {
            System.out.println("Ошибка: введено не число. Введите номер айди пользователя");
            scanner.next();
        }
        Integer id = scanner.nextInt();
        scanner.nextLine();
        logger.info("Попытка обновление пользователя с id {}", id);
        try {
            User user = userStorage.getUserById(id);
            if (user == null) {
                logger.warn("Пользователь не найден");
                throw new UserNotFoundException("Пользователь с id " + id + " не найден.");
            }
            boolean running = true;
            while (running) {
                System.out.println("Выберите пункт из меню:\n" +
                        "1. Изменить имя пользователя\n" +
                        "2. Изменить почту пользователя\n" +
                        "3. Изменить возраст\n" +
                        "4. Применить изменения и выйти в главное меню\n" +
                        "5. Возврат в главное меню без сохранения изменений"
                );
                switch (scanner.nextLine()) {
                    case "1":
                        System.out.println("Введите новое Имя");
                        String name = scanner.nextLine();
                        user.setName(name);
                        chekUpdate = true;
                        break;
                    case "2":
                        System.out.println("Введите новый Email");
                        String email = scanner.nextLine();
                        user.setEmail(email);
                        chekUpdate = true;
                        break;
                    case "3":
                        System.out.println("Введите новый возраст");
                        while (!scanner.hasNextInt()) {
                            System.out.println("Ошибка: введено не число. Введите возраст");
                            scanner.next();
                        }
                        Integer age = scanner.nextInt();
                        scanner.nextLine();
                        user.setAge(age);
                        chekUpdate = true;
                        break;
                    case "4":
                        if (!chekUpdate) {
                            System.out.println("Изменений пользователя не было");
                            break;
                        }
                        userStorage.updateUser(user);
                        System.out.println("Пользователь обновлен");
                        logger.info("Обновлен пользователь с id: {}", user.getId());
                        System.out.println(user);
                        running = false;
                        break;
                    case "5":
                        System.out.println("Возврат в главное меню");
                        running = false;
                        break;
                    default:
                        System.out.println("Ввели неверное число");
                }
            }
        } catch (Exception e) {
            System.out.println("Произошла ошибка при обновлении пользователя: " + e.getMessage());
            logger.error("Ошибка при обновлении пользователя", e);
        }
    }

    private static void deleteUser() {
        System.out.println("Удаление пользователя");
        System.out.println("Введите id пользователя");
        while (!scanner.hasNextInt()) {
            System.out.println("Ошибка: введено не число. Введите номер айди пользователя");
            scanner.next();
        }
        Integer id = scanner.nextInt();
        scanner.nextLine();
        logger.info("Попытка удаления пользователя с id {}", id);
        try {
            User user = userStorage.getUserById(id);
            if (user == null) {
                logger.warn("Пользователь не найден");
                throw new UserNotFoundException("Пользователь с id " + id + " не найден.");
            }
            userStorage.remove(id);
            logger.info("Удален пользователь с id {}", id);
        } catch (Exception e) {
            System.out.println("Произошла ошибка при удалении пользователя: " + e.getMessage());
            logger.error("Ошибка при удалении пользователя", e);
        }
    }

    private static void getUerById() {
        System.out.println("Получение пользователя по айди");
        System.out.println("Введите id пользователя");
        while (!scanner.hasNextInt()) {
            System.out.println("Ошибка: введено не число. Введите номер айди пользователя");
            scanner.next();
        }
        Integer id = scanner.nextInt();
        scanner.nextLine();
        logger.info("Попытка получения пользователя с id {}", id);
        try {
            User user = userStorage.getUserById(id);
            if (user == null) {
                logger.warn("Пользователь не найден");
                throw new UserNotFoundException("Пользователь с id " + id + " не найден.");
            }
            System.out.println(user);
            logger.info("Получен пользователь с id {}", id);
        } catch (Exception e) {
            System.out.println("Произошла ошибка при получении пользователя: " + e.getMessage());
            logger.error("Ошибка при получении пользователя", e);
        }
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