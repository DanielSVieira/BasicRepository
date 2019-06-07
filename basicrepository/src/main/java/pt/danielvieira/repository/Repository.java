package pt.danielvieira.repository;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.Collection;

public interface Repository {

    <T> T find(Class<T> entityClass, Serializable id);

    <T extends Persistable> T save(T entity);

    <T> void delete(T entity);

    <T> void deleteById(Class<T> entityClass, Serializable id);

    <T> Collection<T> findAll(Class<T> entityClass, Predicate... where);

    <T> boolean exists(Class<T> entityClass, Predicate... where);

    <T> boolean notExists(Class<T> entityClass, Predicate... where);

    <T> void deleteAll(Class<T> entityClass);

}
