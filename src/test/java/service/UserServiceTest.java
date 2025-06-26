package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import userApp.dao.UserStorage;
import userApp.exception.UserNotFoundException;
import userApp.model.User;
import userApp.service.UserService;
import userApp.utils.InputProvider;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class UserServiceTest {
    private UserStorage userStorage;
    private UserService userService;


    @BeforeEach
    void setUp() {
        userStorage = mock(UserStorage.class);
        userService = new UserService(userStorage);
    }

    @Test
    void addUser() throws Exception {
        User user = new User("Test", "email@email.ru", 25);
        when(userStorage.addUser(any(User.class))).thenReturn(user);
        User createdUser = userService.createUser(user.getName(), user.getEmail(), user.getAge());
        assertEquals(user, createdUser);
        verify(userStorage).addUser(any(User.class));
    }

    @Test
    void DeleteExistUser() throws Exception {
        User user = new User("Test", "email@email.ru", 25);
        when(userStorage.getUserById(1)).thenReturn(user);
        assertDoesNotThrow(() -> userService.deleteUser(1));
        verify(userStorage).remove(1);
    }

    @Test
    void DeleteNotFoundUser() throws Exception {
        when(userStorage.getUserById(1)).thenReturn(null);
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1));
        verify(userStorage, never()).remove(1);
    }

    @Test
    void testGetExistsUserById() throws Exception {
        User user = new User("Test", "email@email.ru", 25);
        when(userStorage.getUserById(1)).thenReturn(user);
        User retrievedUser = userService.getUserById(1);
        assertEquals(user, retrievedUser);
        verify(userStorage).getUserById(1);
    }

    @Test
    void testGetNotFoundUserById() throws Exception {
        when(userStorage.getUserById(1)).thenReturn(null);
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1));
        verify(userStorage).getUserById(1);
    }

    @Test
    void testUpdateExistsUser() throws Exception {
        User user = new User("Test", "email@email.ru", 25);
        when(userStorage.getUserById(1)).thenReturn(user);
        InputProvider inputProvider = mock(InputProvider.class);
        when(inputProvider.getInput()).thenReturn("1", "NewName", "2", "newEmail@email.ru", "4", "3", "10", "4");
        userService.updateUser(1, inputProvider);
        assertEquals(user, userService.getUserById(1));
        verify(userStorage).updateUser(user);
    }

    @Test
    void testUpdateNotFoundUser() throws Exception {
        when(userStorage.getUserById(1)).thenReturn(null);
        InputProvider inputProvider = mock(InputProvider.class);
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(1, inputProvider));
        verify(userStorage, never()).updateUser(any(User.class));
    }

    @Test
    void testUpdateUserOldMethodTest() throws Exception {
        User user = new User("Test", "email@email.ru", 25);
        when(userStorage.getUserById(1)).thenReturn(user);
        //Scanner scanner = PowerMockito.mock(Scanner.class); //убрал PowerMockito из pom и его аннотации
        //when(scanner.nextLine()).thenReturn("1", "NewName", "4");
        System.setIn(new ByteArrayInputStream("1\nNewName\n4\n".getBytes())); //как обойти мок сканнера
        userService.updateUserOld(1);
        assertEquals(user, userService.getUserById(1));
        verify(userStorage).updateUser(user);
    }
}