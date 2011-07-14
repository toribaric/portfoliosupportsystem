package org.nnga.tsp.persistence.provider;

import java.util.List;

public interface DataProvider<T> {
    List<T> getAll();
    T getById(int id);
    void save(T t) throws Exception;
    void delete(T t) throws Exception;
}
