package dao;

import java.util.List;

public interface GenericDAO<T> {
    T save(T object);
    void update(T object);
    void delete(T object);
    T find(int id);
    List<T> getTable();
}
