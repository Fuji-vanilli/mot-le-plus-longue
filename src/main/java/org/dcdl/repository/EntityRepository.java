package org.dcdl.repository;

import java.util.List;

public interface EntityRepository<T, R> {
    T save(T t);
    T findById(R id);
    List<T> findAll();
    void deleteById(R id);

}