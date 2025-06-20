package userApp;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateConfiguration {

    public SessionFactory sessionFactory() {
        Configuration configuration = new Configuration();
        configuration.configure();

        return configuration.buildSessionFactory();
    }
}
