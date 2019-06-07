package pt.danielvieira.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAProvider;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Persistable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

/**
 * Repository with basic operations combined to QueryDsl and JPA.
 */
@Component
@Transactional
public class BasicRepository implements Repository {
	
    private final EntityManager entityManager;
    private final JPQLTemplates jpqlTemplate;
    private final PathBuilderFactory pathBuilderFactory;

    public BasicRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.jpqlTemplate = JPAProvider.getTemplates(entityManager);
        this.pathBuilderFactory = new PathBuilderFactory();
    }

    /**
     * Find an entity by type and ID.
     *
     * @param entityClass class to be found
     * @param id          id of entity to be found
     * @param <T>         type of entity
     * @return an entity containing type and ID provided
     */
    @Override
    public <T> T find(Class<T> entityClass, Serializable id) {
        return entityManager.find(entityClass, id);
    }

    /**
     * Localiza as entidades ordenadas que atendam os predicados fornecidos
     * Find all entities ordered by the provided predicateds
     *
     * @param entityClass entities class to be found
     * @param where       predicated used to find entities
     * @param <T>         type of entities to be founded
     * @return a ordered list containing all entities founded
     */
    @Override
    public <T> Collection<T> findAll(Class<T> entityClass, Predicate... where) {
        return query(entityClass).where(where).fetch();
    }
    
    /**
     * Saves an entity into databse
     *
     * @param entity to be persisted
     * @param <T>    type of entity to be persisted
     * @return persisted entity
     */
    @Override
    public <T extends Persistable> T save(T entity) {
        Objects.requireNonNull(entity);
        return entityManager.merge(entity);
    }

    /**
     * Remove an entity from database
     *
     * @param entity to be removed
     * @param <T>    type of entity to be removed
     */
    @Override
    public <T> void delete(T entity) {
        Objects.requireNonNull(entity);
        entityManager.remove(entity);
    }

    /**
     * Remove an entity from DataBase
     *
     * @param entityClass entity to be removed
     * @param id          id of entity to be removed
     * @param <T>         entity type to be removed
     */
    @Override
    public <T> void deleteById(Class<T> entityClass, Serializable id) {
        Objects.requireNonNull(entityClass);
        Objects.requireNonNull(id);
        entityManager.remove(find(entityClass, id));
    }

    /**
     * Checks if exists any entity with provided predicateds
     *
     * @param entityClass class to be checked
     * @param where       predicateds used to check entity's existence 
     * @param <T>         type of entity
     * @return true if find an entity with provided parameters, otherwise returns false
     */
    @Override
    public <T> boolean exists(Class<T> entityClass, Predicate... where) {
        return query(entityClass).where(where).fetchCount() > 0;
    }

    /**
     * Checks if there is none entity with provided predicateds
     *
     * @param entityClass class to be checked
     * @param where       predicateds used to check entity's existence 
     * @param <T>         type of entity
     * @return true if there is no entity with provided parameters, otherwise returns true
     */
    @Override
    public <T> boolean notExists(Class<T> entityClass, Predicate... where) {
        return !exists(entityClass, where);
    }

    /**
     * Delete all records by a type
     */
    @Override
    public <T> void deleteAll(Class<T> entityClass) {
        findAll(entityClass).forEach(this::delete);
    }

    /**
     * Created a link {@link JPAQuery}.
     *
     * @return JPAQuery
     */
    private <T> JPAQuery<T> query(Class<T> entityClass) {
        JPAQuery<T> query = new JPAQuery<>(entityManager, jpqlTemplate);
        query.from(pathBuilderFactory.create(entityClass));
        return query;
    }

}