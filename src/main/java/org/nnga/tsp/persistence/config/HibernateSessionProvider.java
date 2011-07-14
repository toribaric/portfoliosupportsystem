package org.nnga.tsp.persistence.config;

import org.hibernate.Session;

public interface HibernateSessionProvider {
    Session getSession();
}
