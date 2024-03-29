/*
 * Copyright 2013-2016 the original author or authors.
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
package org.springframework.data.jooq.repository.support.query;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.Entity;

/**
 * Default implementation for {@link JooqEntityMetadata}.
 * 
 * @author Johannes Buehler
 */
public class DefaultJooqEntityMetadata<T> implements JooqEntityMetadata<T> {

	private final Class<T> domainType;

	/**
	 * Creates a new {@link DefaultJooqEntityMetadata} for the given domain type.
	 *
	 * @param domainType must not be {@literal null}.
	 */
	public DefaultJooqEntityMetadata(Class<T> domainType) {

		Assert.notNull(domainType, "Domain type must not be null!");
		this.domainType = domainType;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.core.EntityMetadata#getJavaType()
	 */
	@Override
	public Class<T> getJavaType() {
		return domainType;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.jpa.repository.support.JpaEntityMetadata#getEntityName()
	 */
	public String getEntityName() {

		Entity entity = AnnotatedElementUtils.findMergedAnnotation(domainType, Entity.class);
		boolean hasName = null != entity && StringUtils.hasText(entity.name());

		return hasName ? entity.name() : domainType.getSimpleName();
	}
}
