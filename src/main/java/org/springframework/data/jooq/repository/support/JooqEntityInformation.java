package org.springframework.data.jooq.repository.support;

import org.jooq.RecordMapper;
import org.jooq.Table;
import org.jooq.UpdatableRecord;
import org.springframework.data.jooq.repository.support.query.JooqEntityMetadata;

import javax.persistence.metamodel.SingularAttribute;
import java.io.Serializable;

public interface JooqEntityInformation<R extends UpdatableRecord<R>, T extends Table<R>, E, ID extends Serializable> extends JooqEntityMetadata<E> {
    /**
     * Returns the id attribute of the entity.
     *
     * @return
     */
    SingularAttribute<? super E, ?> getIdAttribute();

    /**
     * Returns {@literal true} if the entity has a composite id.
     *
     * @return
     */
    boolean hasCompositeId();

    /**
     * Returns the attribute names of the id attributes. If the entity has a composite id, then all id attribute names are
     * returned. If the entity has a single id attribute then this single attribute name is returned.
     *
     * @return
     */
    Iterable<String> getIdAttributeNames();

    /**
     * Extracts the value for the given id attribute from a composite id
     *
     * @param id
     * @param idAttribute
     * @return
     */
    <E> Object getCompositeIdAttributeValue(Serializable id, String idAttribute);


   RecordMapper<R,E> mapper();

   T table();

    boolean isNew(E entity);

    ID getId(E entity);
}
