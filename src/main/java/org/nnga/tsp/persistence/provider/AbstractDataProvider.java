package org.nnga.tsp.persistence.provider;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.nnga.tsp.persistence.config.HibernateSessionProvider;

import javax.annotation.Resource;
import java.util.List;

public abstract class AbstractDataProvider<T> implements DataProvider<T> {

    protected HibernateSessionProvider hibernateSessionProvider;

    @Override
    public List<T> getAll() {
        return listData("from " + getEntityClass().getName());
    }

    @Override
    public T getById(int id) {
        Session session = getSession();
        String sqlQuery = "from " + getEntityClass().getName() + " where id = ?";
        Query query = session.createQuery(sqlQuery);
        query.setInteger(0, id);
        List records = listData(session, query);
        return records.size() > 0 ? (T) records.get(0) : null;
    }

    @Override
    public void save(T t) throws Exception {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.saveOrUpdate(t);
            transaction.commit();
        }
        catch(Exception e) {
            try {
                transaction.rollback();
            }
            catch(HibernateException he) {}
            throw new Exception(e.getMessage(), e);
        }
        finally {
            session.close();
        }
    }

    @Override
    public void delete(T t) throws Exception {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.delete(t);
            transaction.commit();
        }
        catch (Exception e) {
            try {
                transaction.rollback();
            }
            catch (HibernateException e1) {}
            throw new Exception(e.getMessage(), e);
        }
        finally {
            session.close();
        }
    }

    protected List listData(Session session, Query query) {
        try {
            return query.list();
        }
        finally {
            session.close();
        }
    }

    protected List listData(String sqlQuery) {
        Session session = getSession();
        return listData(session, session.createQuery(sqlQuery));
    }

    protected abstract Class<T> getEntityClass();

    protected Session getSession() {
        return hibernateSessionProvider.getSession();
    }

    @Resource
    public void setHibernateSessionProvider(HibernateSessionProvider hibernateSessionProvider) {
        this.hibernateSessionProvider = hibernateSessionProvider;
    }
}
