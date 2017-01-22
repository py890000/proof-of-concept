/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.jooq.repository.support;

import org.jooq.DSLContext;
import org.jooq.RecordMapper;
import org.jooq.Table;
import org.jooq.UpdatableRecord;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jooq.repository.support.query.DefaultJooqEntityMetadata;
import org.springframework.data.jooq.repository.support.query.JooqEntityMetadata;
import org.springframework.data.repository.core.support.AbstractEntityInformation;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Base class for {@link JooqEntityInformation} implementations to share common method implementations.
 *
 * @author Johannes Buehler
 */
public abstract class JooqEntityInformationSupport<R extends UpdatableRecord<R>, T extends Table<R>, E, ID extends Serializable> extends AbstractEntityInformation<E, ID> implements JooqEntityInformation<R, T,E, ID> {

    private JooqEntityMetadata<E> metadata;
    private RecordMapper<R, E> mapper;
    private T table;


    public JooqEntityInformationSupport(Class<E> domainClass, DSLContext context) {
        super(domainClass);
        this.metadata = new DefaultJooqEntityMetadata<E>(domainClass);
        String entityPackage = domainClass.getPackage().getName();
        int lastIndexOf = entityPackage.lastIndexOf('.');
        String name = domainClass.getSimpleName();
        String tableClassName = entityPackage.substring(0, lastIndexOf + 1) + name;
        try {
            Class<?> tableClass = Class.forName(tableClassName);
            Field field = tableClass.getField(name.toUpperCase());
            table = (T) field.get(null);
            mapper = context.configuration().recordMapperProvider().provide(table.recordType(), domainClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Creates a {@link JooqEntityInformation} for the given domain class and {@link EntityManager}.
     *
     * @param domainClass must not be {@literal null}.
     * @param dslContext  must not be {@literal null}.
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <R extends UpdatableRecord<R>,J extends Table<R>,E> JooqEntityInformation<R,J,E, ?> getEntityInformation(Class<E> domainClass, DSLContext dslContext) {

        Assert.notNull(domainClass);
        Assert.notNull(dslContext);

        if (Persistable.class.isAssignableFrom(domainClass)) {
            return new JooqPersistableEntityInformation(domainClass, dslContext);
        } else {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.jpa.repository.support.JpaEntityMetadata#getEntityName()
     */
    public String getEntityName() {
        return metadata.getEntityName();
    }

    @Override
    public RecordMapper<R, E> mapper() {
        return mapper;
    }

    @Override
    public T table() {
        return table;
    }
}
