/*
 * Copyright 2011-2013 the original author or authors.
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
import org.jooq.Table;
import org.jooq.UpdatableRecord;
import org.springframework.data.domain.Persistable;

import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;
import java.io.Serializable;

/**
 * Extension of {@link JooqMetamodelEntityInformation} that consideres methods of {@link Persistable} to lookup the id.
 * 
 * @author Johannes Buehler
 */
public class JooqPersistableEntityInformation<R extends UpdatableRecord<R>, T extends Table<R>, E extends Persistable<ID>, ID extends Serializable> extends JooqEntityInformationSupport<R, T,E, ID> {

	/**
	 * Creates a new {@link JooqPersistableEntityInformation} for the given domain class and {@link Metamodel}.
	 *
	 * @param domainClass must not be {@literal null}.
	 * @param metamodel must not be {@literal null}.
	 */
	public JooqPersistableEntityInformation(Class<E> domainClass, DSLContext context) {
		super(domainClass, context);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.jpa.repository.support.JpaMetamodelEntityInformation#getId(java.lang.Object)
	 */
	@Override
	public ID getId(E entity) {
		return entity.getId();
	}

	@Override
	public Class<ID> getIdType() {
		return null;
	}



	@Override
	public SingularAttribute<? super E, ?> getIdAttribute() {
		return null;
	}

	@Override
	public boolean hasCompositeId() {
		return false;
	}

	@Override
	public Iterable<String> getIdAttributeNames() {
		return null;
	}

	@Override
	public Object getCompositeIdAttributeValue(Serializable id, String idAttribute) {
		return null;
	}

}
