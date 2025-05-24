package com.iut;

import java.util.List;

public interface Repository<T, ID>{
    boolean save(T input);
    boolean update(T input);
    boolean delete(ID id);
    boolean existsById(ID id);
    T findById(ID id);
    List<T> findAll();
}
