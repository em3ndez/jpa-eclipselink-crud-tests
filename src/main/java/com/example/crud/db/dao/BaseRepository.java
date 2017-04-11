package com.example.crud.db.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID>,
        PagingAndSortingRepository<T, ID>, QueryByExampleExecutor<T> { //TODO(gburd): this requires gradle to pre-build classes like '@Employee' etc. }, QueryDslPredicateExecutor<T> {

    /**
     * Saves a given entity. Use the returned instance for further operations as the save operation might have changed the
     * entity instance completely.
     *
     * @param entity
     * @return the saved entity
     */
    <S extends T> S save(S entity);

    /**
     * Saves all given models.
     *
     * @param entities
     * @return the saved models
     * @throws IllegalArgumentException in case the given entity is {@literal null}.
     */
    <S extends T> List<S> save(Iterable<S> entities);

    /**
     * Retrieves an entity by its id.
     *
     * @param id must not be {@literal null}.
     * @return the entity with the given id or {@literal null} if none found
     * @throws IllegalArgumentException if {@code id} is {@literal null}
     */
    T findOne(ID id);

    /**
     * Returns all instances of the type.
     *
     * @return all models
     */
    List<T> findAll();

    /**
     * Returns all instances of the type with the given IDs.
     */
    List<T> findAll(Iterable<ID> ids);

    /**
     * Returns whether an entity with the given id exists.
     *
     * @param id must not be {@literal null}.
     * @return true if an entity with the given id exists, {@literal false} otherwise
     * @throws IllegalArgumentException if {@code id} is {@literal null}
     */
    boolean exists(ID id);

    /**
     * Returns the number of models available.
     *
     * @return the number of models
     */
    long count();

    /**
     * Deletes a given entity.
     *
     * @throws IllegalArgumentException in case the given entity is {@literal null}.
     */
    // Optional<T>...
    void delete(ID id);

    /**
     * Deletes the given models.
     *
     * @throws IllegalArgumentException in case the given {@link Iterable} is {@literal null}.
     */
    void delete(Iterable<? extends T> entities);

    /**
     * Deletes all models managed by the models.
     */
    void deleteAll();

    /**
     * Flush cache.
     */
    default void refresh() {} //TODO(gburd): implement cache flush... ?
}
