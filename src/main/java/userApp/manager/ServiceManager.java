package userApp.manager;

import userApp.dao.UserStorage;
import userApp.service.UserService;

public class ServiceManager {

    public static UserService getDefault() {
        return new UserService(new UserStorage());
    }
}
