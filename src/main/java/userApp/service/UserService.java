package userApp.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import userApp.utils.InputProvider;
import userApp.dao.UserStorage;
import userApp.exception.UserNotFoundException;
import userApp.model.User;

import java.util.Scanner;

@RequiredArgsConstructor
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserStorage userStorage;

    public User createUser(String name, String email, int age) throws Exception {
        logger.info("Попытка добавления пользователя");
        User user = new User(name, email, age);
        logger.info("Пользователь добавлен");
        return userStorage.addUser(user);
    }

    public void deleteUser(int id) throws Exception {
        logger.info("Попытка удаления пользователя с id {}", id);
        User user = userStorage.getUserById(id);
        if (user == null) {
            logger.warn("Пользователь не найден");
            throw new UserNotFoundException("Пользователь с id " + id + " не найден.");
        }
        userStorage.remove(id);
        logger.info("Удален пользователь с id {}", id);
    }

    public User getUserById(int id) throws Exception {
        logger.info("Попытка получения пользователя с id {}", id);
        User user = userStorage.getUserById(id);
        if (user == null) {
            logger.warn("Пользователь не найден");
            throw new UserNotFoundException("Пользователь с id " + id + " не найден.");
        }
        logger.info("Получен пользователь с id {}", id);
        return user;
    }

    public void updateUser(int id, InputProvider inputProvider) throws Exception {
        logger.info("Попытка обновления пользователя с id {}", id);
        User user = userStorage.getUserById(id);

        if (user == null) {
            logger.warn("Пользователь не найден");
            throw new UserNotFoundException("Пользователь с id " + id + " не найден.");
        }

        boolean checkUpdate = false;
        boolean running = true;

        while (running) {
            showMenu();
            String choice = inputProvider.getInput();
            switch (choice) {
                case "1":
                    updateName(user, inputProvider);
                    checkUpdate = true;
                    break;
                case "2":
                    updateEmail(user, inputProvider);
                    checkUpdate = true;
                    break;
                case "3":
                    updateAge(user, inputProvider);
                    checkUpdate = true;
                    break;
                case "4":
                    if (checkUpdate) {
                        userStorage.updateUser(user);
                        System.out.println("Пользователь обновлен");
                        logger.info("Обновлен пользователь с id: {}", user.getId());
                        System.out.println(user);
                    } else {
                        System.out.println("Изменений пользователя не было");
                    }
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
    }

    private void showMenu() {
        System.out.println("Выберите пункт из меню:\n" +
                "1. Изменить имя пользователя\n" +
                "2. Изменить почту пользователя\n" +
                "3. Изменить возраст\n" +
                "4. Применить изменения и выйти в главное меню\n" +
                "5. Возврат в главное меню без сохранения изменений"
        );
    }

    private void updateName(User user, InputProvider inputProvider) {
        System.out.println("Введите новое Имя");
        String name = inputProvider.getInput();
        user.setName(name);
    }

    private void updateEmail(User user, InputProvider inputProvider) {
        System.out.println("Введите новый Email");
        String email = inputProvider.getInput();
        user.setEmail(email);
    }

    private void updateAge(User user, InputProvider inputProvider) {
        System.out.println("Введите новый возраст");
        String ageInput = inputProvider.getInput();
        while (!isInteger(ageInput)) {
            System.out.println("Ошибка: введено не число. Введите возраст");
            ageInput = inputProvider.getInput();
        }
        Integer age = Integer.parseInt(ageInput);
        user.setAge(age);
    }

    private boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    //Для себя оставил, было интересно разобраться как мокнуть Scanner
    public void updateUserOld(int id) throws Exception {
        logger.info("Попытка обновления пользователя с id {}", id);
        User user = userStorage.getUserById(id);
        if (user == null) {
            logger.warn("Пользователь не найден");
            throw new UserNotFoundException("Пользователь с id " + id + " не найден.");
        }

        boolean checkUpdate = false;
        Scanner scanner = new Scanner(System.in);

        boolean running = true;
        while (running) {
            showMenu();
            switch (scanner.nextLine()) {
                case "1":
                    System.out.println("Введите новое Имя");
                    String name = scanner.nextLine();
                    user.setName(name);
                    checkUpdate = true;
                    break;
                case "2":
                    System.out.println("Введите новый Email");
                    String email = scanner.nextLine();
                    user.setEmail(email);
                    checkUpdate = true;
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
                    checkUpdate = true;
                    break;
                case "4":
                    if (!checkUpdate) {
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
    }
}
