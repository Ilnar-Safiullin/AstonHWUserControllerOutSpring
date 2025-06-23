package userApp.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import userApp.configuration.HibernateConfiguration;
import userApp.exception.UserNotFoundException;
import userApp.model.User;

public class UserStorage {
    private static final Logger logger = LoggerFactory.getLogger(UserStorage.class);


    public User addUser(User user) {
        Transaction transaction = null;
        try (Session session = HibernateConfiguration.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
            logger.info("Пользователь добавлен с id {}", user.getId());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.warn("Ошибка при добавлении пользователя", e);
        }
        return user;
    }

    public User getUserById(int userId) {
        try (Session session = HibernateConfiguration.getSessionFactory().openSession()) {
            User user = session.get(User.class, userId);
            if (user != null) {
                logger.info("Пользователь найден с id {}", userId);
            } else {
                logger.warn("Пользователь не найден с id {}", userId);
                throw new UserNotFoundException("Пользователь не найден");
            }
            return user;
        } catch (Exception e) {
            logger.warn("Ошибка при поиски пользователя", e);
            return null;
        }
    }

    public User updateUser(User user) {
        Transaction transaction = null;
        try (Session session = HibernateConfiguration.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            User existingUser = session.get(User.class, user.getId());
            if (existingUser != null) {
                existingUser.setName(user.getName());
                existingUser.setEmail(user.getEmail());
                existingUser.setAge(user.getAge());
                session.update(existingUser);
                transaction.commit();
                logger.info("Пользователь обновлен с id {}", existingUser.getId());;
            } else {
                logger.warn("Пользователь не обновлен, не найден пользователь с id {}", existingUser.getId());
                transaction.rollback();
                throw new UserNotFoundException("Пользователь не найден");
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.warn("Ошибка обновления пользователя", e);
        }
        return user;
    }

    public void remove(int userId) {
        Transaction transaction = null;
        try (Session session = HibernateConfiguration.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, userId);
            if (user != null) {
                session.delete(user);
                transaction.commit();
                logger.info("Пользователь удален с id {}", userId);;
            } else {
                logger.warn("Пользователь не найден и не удален с id {}", userId);;
                transaction.rollback();
                throw new UserNotFoundException("Пользователь не найден");
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.warn("Ошибка удаления пользователя", e);
        }
    }
}
