package org.springframework.data.jooq.repository.support.query;

import org.springframework.data.repository.core.EntityMetadata;

public interface JooqEntityMetadata<T>  extends EntityMetadata<T> {
    /**
     * Returns the name of the entity.
     *
     * @return the entityName
     */
    String getEntityName();


}
