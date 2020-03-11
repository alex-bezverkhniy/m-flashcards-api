package com.alexbezverkhniy.myflashcards.common;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.SecurityContext;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BaseService<E extends Model, R extends PanacheRepository<E>> {
    public static final String DEFAULT_USER = "admin";
    protected SecurityContext securityContext;

    @Inject
    protected R repository;
    protected Class<E> entityType;

    public BaseService withSecurityContext(final SecurityContext securityContext) {
        this.securityContext = securityContext;
        return this;
    }

    @Transactional
    public E create(final E model) {
        if (model.getId() != null) {
            throw new IllegalArgumentException("ID should be null when creating new");
        }
        final E storeEntity = buildFromModel(model, entityType);

        populateMetadata(storeEntity);
        repository.persist(storeEntity);
        return storeEntity;
    }

    @Transactional
    public E update(final Long id, final E model) {
        if (id == null) {
            throw new IllegalArgumentException("ID should not be null");
        }
        final E existOne = repository.findById(id);

        if (existOne == null) {
            throw new EntityNotFoundException();
        }

        copy(model, existOne);
        ((Model) existOne).setId(id);

        populateMetadata(existOne);
        ((Model) existOne).setUpdatedAt(LocalDateTime.now());
        repository.persist(existOne);
        return existOne;
    }

    public E findById(final Long id) {
        final E entity = repository.findById(id);
        if (entity == null) {
            throw new EntityNotFoundException();
        }
        return entity;
    }

    @Transactional
    public void delete(final Long id) {
        final E entity = repository.findById(id);
        if (entity == null) {
            throw new EntityNotFoundException();
        }
        repository.delete(entity);
    }

    public List<E> findAll() {
//        return convertList(repository.listAll(), false);
        return repository.findAll().list();
    }

    public List<E> query(final String query, final Object... values) {
        return convertList(repository.list(query, values), true);
    }

    protected void populateMetadata(final E model) {
        if (((Model) model).getCreatedAt() == null) {
            ((Model) model).setCreatedAt(LocalDateTime.now());
        }
        if (((Model) model).getUpdatedAt() == null) {
            ((Model) model).setUpdatedAt(LocalDateTime.now());
        }
        if (StringUtils.isEmpty(((Model) model).getCreatedBy())) {
            ((Model) model).setCreatedBy(retreiveUserName());
        }
        if (StringUtils.isEmpty(((Model) model).getUpdatedBy())) {
            ((Model) model).setUpdatedBy(retreiveUserName());
        }
    }

    protected String retreiveUserName() {
        if (securityContext != null) return securityContext.getUserPrincipal().getName();
        else return DEFAULT_USER;
    }

    protected List<E> convertList(final List<E> result, final boolean throwException) throws EntityNotFoundException {
        if (result != null && !result.isEmpty()) {
            return result.stream().map(entity -> {
                try {
                    final E model = (E) this.entityType.getSuperclass().getConstructor().newInstance();
                    copy(entity, model);
                    return model;
                } catch (final ReflectiveOperationException e) {
                    throw new RuntimeException("Cannot map Entity to Model");
                }
            }).collect(Collectors.toList());
        }
        if (throwException) {
            throw new EntityNotFoundException();
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Converts generated model into "child entity"
     *
     * @param source - Source Model object
     * @param type   - Type of Entity object (Should extend source type)
     * @param <T>
     * @return
     * @throws RuntimeException
     */
    protected <T> T buildFromModel(final Object source, final Class<T> type) throws RuntimeException {
        if (source == null) {
            throw new IllegalArgumentException("Source object should not be null");
        }

        try {
            final T instance = type.getConstructor().newInstance();
            copy(source, instance);
            return instance;
        } catch (final ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected <T> T copy(final T source, final T target) {
        if (source == null) {
            throw new IllegalArgumentException("Source object should not be null");
        }

        if (target == null) {
            throw new IllegalArgumentException("Target object should not be null");
        }

        final Class instanceClass = target.getClass();

        Stream.of(source.getClass().getMethods())
                .filter(m -> Modifier.isPublic(m.getModifiers()) && m.getName().startsWith("get"))
                .forEach(m -> {
                    try {
                        final Object value = m.invoke(source);
                        if (!m.getName().equals("getClass")) {
                            if (value != null && !(
                                    m.getName().equals("getCreatedBy") ||
                                            m.getName().equals("getCreatedAt") ||
                                            m.getName().equals("getUpdatedAt") ||
                                            m.getName().equals("getUpdatedBy")))
                                instanceClass.getMethod(m.getName().replaceFirst("get", "set"), m.getReturnType()).invoke(target, value);
                        }
                    } catch (final Exception e) {
                        throw new IllegalArgumentException(String.format("Missed field: %s", m.getName()));
                    }
                });
        return target;

    }
}