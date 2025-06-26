package userApp.utils;

public interface InputProvider {
    /**
     * Пришлось добавить иначе не смог сделать мок тесты на update
     */

    String getInput();
    boolean hasNextInt();
    int nextInt();
}

