package dao;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import userApp.configuration.HibernateConfiguration;
import userApp.dao.UserStorage;
import userApp.exception.UserNotFoundException;
import userApp.model.User;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.PostgreSQLContainer;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
public class UserStorageTest {
    private UserStorage userStorage;
    User user;

    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @BeforeEach
    void setUp() throws Exception {
        Configuration configuration = new Configuration();
        //это все можно убрать в еще один xml файл, но оставил вдруг чтото криво делаю
        configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        configuration.setProperty("hibernate.connection.url", postgresContainer.getJdbcUrl());
        configuration.setProperty("hibernate.connection.username", postgresContainer.getUsername());
        configuration.setProperty("hibernate.connection.password", postgresContainer.getPassword());
        configuration.setProperty("hibernate.show_sql", "true");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create-drop");

        configuration.addAnnotatedClass(User.class);
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        HibernateConfiguration.setSessionFactory(sessionFactory);

        user = new User("name", "email@email.ru", 25);
        userStorage = new UserStorage();
        userStorage.addUser(user);
    }

    @Test
    void addTest() {
        assertEquals(user.getId(), 1); //можно было тут просто получить юзера по айди но тогда смысл следующего теста теряется
    }

    @Test
    void getUserById() throws Exception {
        assertEquals(user, userStorage.getUserById(user.getId()));
    }

    @Test
    void getUserByIdNotFoundTest() {
        assertThrows(UserNotFoundException.class, () -> userStorage.getUserById(Integer.MAX_VALUE));
    }

    @Test
    void updateUser() throws Exception {
        User updatedUser = new User("NewUser", "NewEmail", 10);
        updatedUser.setId(user.getId());
        userStorage.updateUser(updatedUser);
        assertEquals(updatedUser, userStorage.getUserById(user.getId())); //тут посчитал логичнее проверить юзера именно из базы после обновления
    }

    @Test
    void removeUser() throws Exception {
        userStorage.remove(user.getId());
        assertThrows(UserNotFoundException.class, () -> userStorage.getUserById(Integer.MAX_VALUE));
    }
}