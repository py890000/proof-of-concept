package org.springframework.data.jooq.repository.support;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static org.jooq.impl.DSL.*;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.SelectQuery;
import org.jooq.SortField;
import org.jooq.Table;
import org.jooq.UniqueKey;
import org.jooq.UpdatableRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jooq.repository.JooqRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Repository base implementation for jOOQ.
 * <p>
 * All of the {@link JooqRepository} will be implemented by inheriting this class.
 * </p>
 *
 * @author yukung
 * @author Johannes Buehler
 */
@Repository
@Transactional(readOnly = true)
public class SimpleJooqRepository<R extends UpdatableRecord<R>, T extends Table<R>, E, ID extends Serializable> implements JooqRepository<E, ID> {

    private final JooqEntityInformation<R, T,E, ?> entityInformation;
    private RecordMapper<R, E> mapper;
    private T table;

    private DSLContext jooq;


    /**
     * Creates a new {@link SimpleJooqRepository} to manage objects of the given {@link JooqEntityInformation}.
     *
     * @param entityInformation must not be {@literal null}.
     * @param dslContext must not be {@literal null}.
     */
    public SimpleJooqRepository(JooqEntityInformation<R, T,E, ?> entityInformation, DSLContext dslContext) {

        Assert.notNull(entityInformation);
        this.entityInformation = entityInformation;
        this.jooq = dslContext;
        mapper = entityInformation.mapper();
        table = entityInformation.table();
    }

    /**
     * Creates a new {@link SimpleJooqRepository} to manage objects of the given domain type.
     *
     * @param domainClass must not be {@literal null}.
     * @param dslContext must not be {@literal null}.
     */
    public SimpleJooqRepository(Class<E> domainClass, DSLContext dslContext) {
        this(JooqEntityInformationSupport.getEntityInformation(domainClass, dslContext), dslContext);
    }




    /**
     * {@inheritDoc}
     */
    @Override
    public List<E> findAll() {
        return jooq
                .selectFrom(table)
                .fetch()
                .map(mapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<E> findAll(Iterable<ID> ids) {
        if (ids == null) {
            return Collections.emptyList();
        }
        Field<?>[] pk = pk();
        List<ID> keys = new ArrayList<>();
        ids.forEach(keys::add);

        List<E> result = new ArrayList<>();
        if (pk != null) {
            result = jooq
                    .selectFrom(table)
                    .where(in(pk, keys))
                    .fetch()
                    .map(mapper);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<E> findAll(Sort sort) {
        // TODO Throw exception if the index of the specified columns does not exist.
        SelectQuery<R> query = getQuery(sort);
        return query.fetch().map(mapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<E> findAll(Pageable pageable) {
        if (pageable == null) {
            return new PageImpl<>(findAll());
        }
        SelectQuery<R> query = getQuery(pageable);
        return new PageImpl<>(query.fetch().map(mapper), pageable, count());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E findOne(ID id) {
        Field<?>[] pk = pk();
        R record = null;

        if (pk != null) {
            record = jooq
                    .selectFrom(table)
                    .where(equal(pk, id))
                    .fetchOne();
        }
        return record == null ? null : mapper.map(record);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(ID id) {
        Field<?>[] pk = pk();

        return pk != null && jooq
                .selectCount()
                .from(table)
                .where(equal(pk, id))
                .fetchOne(0, Integer.class) > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count() {
        return jooq
                .selectCount()
                .from(table)
                .fetchOne(0, Long.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S extends E> S save(S entity) {
        Assert.notNull(entity);
        R record;

        if (entityInformation.isNew(entity)) {
            record = jooq.newRecord(table, entity);
        } else {
            R fetched = fetchById(getId(entity));
            if (fetched != null) {
                fetched.from(entity);
                record = fetched;
            } else {
                record = jooq.newRecord(table, entity);
            }
        }
        record.store();
        return record.into(entity);
    }

    /**
     * {@inheritDoc}
     * <p>
     * NOTE: Please note that the case of large number of entity to be saved which is becomes very slowly.
     * Because in the inside are calling the save() in the loop.
     * </p>
     */
    @Override
    public <S extends E> Iterable<S> save(Iterable<S> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        // TODO if jOOQ supports auto-generated ID when the batch update, modify using it here.
        // See: https://github.com/jOOQ/jOOQ/issues/3327
        List<S> result = new ArrayList<>();
        entities.forEach(entity -> result.add(save(entity)));
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(ID id) {
        Field<?>[] pk = pk();

        if (pk != null) {
            jooq
                    .deleteFrom(table)
                    .where(equal(pk, id))
                    .execute();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(E entity) {
        delete(Collections.singletonList(entity));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Iterable<? extends E> entities) {
        Field<?>[] pk = pk();

        if (pk != null) {
            List<ID> ids = new ArrayList<>();
            entities.forEach(entity -> ids.add(getId(entity)));
            jooq
                    .deleteFrom(table)
                    .where(in(pk, ids))
                    .execute();
        }
    }



    /**
     * Deletion of all the records is not supported for ensure safety.
     */
    @Override
    public final void deleteAll() {
        throw new UnsupportedOperationException("deleteAll() is not supported.");
    }

    private ID getId(E entity) {
        Assert.notNull(entity);
        ID id = (ID) entityInformation.getId(entity);
        return id;
    }

    private Field<?>[] pk() {
        UniqueKey<R> key = table.getPrimaryKey();
        return key == null ? null : key.getFieldsArray();
    }

    @SuppressWarnings("unchecked")
    private Condition equal(Field<?>[] pk, ID id) {
        if (pk.length == 1) {
            return ((Field<Object>) pk[0]).equal(pk[0].getDataType().convert(id));
        } else {
            return row(pk).equal((Record) id);
        }
    }

    @SuppressWarnings("SuspiciousToArrayCall")
    private Condition in(Field<?>[] pk, Collection<ID> ids) {
        if (pk.length == 1) {
            if (ids.size() == 1) {
                return equal(pk, ids.iterator().next());
            } else {
                return pk[0].in(pk[0].getDataType().convert(ids));
            }
        } else {
            return row(pk).in(ids.toArray(new Record[ids.size()]));
        }
    }

    private R fetchById(ID id) {
        Field<?>[] pk = pk();
        R record = null;

        if (pk != null) {
            record = jooq
                    .selectFrom(table)
                    .where(equal(pk, id))
                    .fetchOne();
        }

        return record == null ? null : record;
    }

    private SelectQuery<R> getQuery(Sort sort) {
        SelectQuery<R> query = jooq.selectFrom(table).getQuery();
        // Do not sort if specified sort condition.
        if (sort == null) {
            return query;
        }
        for (Sort.Order order : sort) {
            // It's currently only allowed column name of lowercase.
            Field<?> field = table.field(name(LOWER_CAMEL.to(LOWER_UNDERSCORE, order.getProperty())));
            if (field == null) {
                // TODO Consider later that can't find the field which has sort condition.
                continue;
            }
            SortField<?> sortField;
            if (order.getDirection() == Sort.Direction.ASC) {
                sortField = field.asc();
            } else {
                sortField = field.desc();
            }
            query.addOrderBy(sortField);
        }
        return query;
    }

    private SelectQuery<R> getQuery(Pageable pageable) {
        SelectQuery<R> query = getQuery(pageable.getSort());
        query.addLimit(pageable.getOffset(), pageable.getPageSize());
        return query;
    }
}