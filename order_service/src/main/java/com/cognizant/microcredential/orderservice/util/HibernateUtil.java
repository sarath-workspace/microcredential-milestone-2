package com.cognizant.microcredential.orderservice.util;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {

    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (null != sessionFactory)
            return sessionFactory;

        Configuration configuration = new Configuration().setProperty("hibernate.connection.url", String.format("jdbc:mysql://%s/%s",
                System.getenv("RDS_HOSTNAME"),
                System.getenv("RDS_DB_NAME")))
                .setProperty("hibernate.connection.username", System.getenv("RDS_USERNAME"))
                .setProperty("hibernate.connection.password", System.getenv("RDS_PASSWORD"))
                .configure();

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .configure("hibernate.cfg.xml")
                .build();

        try {
            /*sessionFactory = new Configuration()
                    .configure() // configures settings from hibernate.cfg.xml
                    .buildSessionFactory();
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);*/
            Metadata meta = new MetadataSources(serviceRegistry).getMetadataBuilder().build();

            sessionFactory = meta.getSessionFactoryBuilder().build();
        } catch (HibernateException e) {
            System.err.println("Initial SessionFactory creation failed." + e);
            throw new ExceptionInInitializerError(e);
        }

        return sessionFactory;
    }

}
