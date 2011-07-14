package org.nnga.tsp.persistence.config.impl;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import javax.persistence.Entity;
import org.nnga.tsp.persistence.config.HibernateSessionProvider;
import org.nnga.tsp.persistence.entity.PersistenceEntity;
import org.scannotation.AnnotationDB;
import org.scannotation.ClasspathUrlFinder;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.Map;
import java.util.Set;

public class HibernateSessionProviderImpl implements HibernateSessionProvider {

    private static final Logger LOGGER = Logger.getLogger(HibernateSessionProviderImpl.class);

    private SessionFactory sessionFactory;

    public HibernateSessionProviderImpl(String driverClass, String connectionUrl, String username, String password) {
        this(driverClass, connectionUrl, username, password, new Class<?>[]{PersistenceEntity.class});
    }

    public HibernateSessionProviderImpl(String driverClass, String connectionUrl, String username, String password, Class<?>... classBases) {
        try {
            AnnotationConfiguration config = new AnnotationConfiguration();
            config.setProperty("hibernate.connection.autocommit", "true");
            config.setProperty("hibernate.connection.isolation", String.valueOf(Connection.TRANSACTION_READ_UNCOMMITTED));
            config.setProperty("hibernate.show_sql", "false");
            config.setProperty("hibernate.use_outer_join", "true");
            config.setProperty("hibernate.connection.driver_class", driverClass);
            config.setProperty("hibernate.transaction.factory_class", "org.hibernate.transaction.JDBCTransactionFactory");
            config.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
            config.setProperty("hibernate.connection.provider_class", "org.hibernate.connection.C3P0ConnectionProvider");
            config.setProperty("hibernate.c3p0.acquire_increment", "1");
            config.setProperty("hibernate.c3p0.max_size", "20");
            config.setProperty("hibernate.c3p0.min_size", "0");
            config.setProperty("hibernate.c3p0.timeout", "5000");
            config.setProperty("hibernate.c3p0.max_statements", "0");
            config.setProperty("hibernate.connection.url", connectionUrl);
            config.setProperty("hibernate.connection.username", username);
            if (null != password) {
                config.setProperty("hibernate.connection.password", password);
            }

            if (classBases == null || classBases.length == 0) {
                classBases = new Class<?>[]{PersistenceEntity.class};
            }

            loadEntityClasses(config, classBases);

            sessionFactory = config.buildSessionFactory();
        } catch (Throwable ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Search for classes annotated with @Entity in base packages of class(es) given by classBasses argument
     */
    private void loadEntityClasses(AnnotationConfiguration config, Class<?>... classBases) throws IOException, ClassNotFoundException {
        AnnotationDB db = new AnnotationDB();
        for (Class<?> classBase : classBases) {
            db.scanArchives(getEntityPackage(classBase));

            Map<String, Set<String>> annotationIndex = db.getAnnotationIndex();


            Set<String> entityClassNames = annotationIndex.get(Entity.class.getName());
            if (null == entityClassNames) {
                LOGGER.warn("No entity classes loaded!");
                return;
            }

            for (String s : entityClassNames) {
                config.addAnnotatedClass(getClass().getClassLoader().loadClass(s));
            }
        }
    }

    private URL getEntityPackage(Class<?> classBase) {
        return ClasspathUrlFinder.findClassBase(classBase);
    }

    @Override
    public Session getSession() {
        return sessionFactory.openSession();
    }

}
