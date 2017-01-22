/*
 * Copyright 2008-2015 the original author or authors.
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
import org.springframework.data.jooq.repository.JooqRepository;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * JPA specific generic repository factory.
 *
 * @author Johannes Buehler
 */
public class JooqRepositoryFactory extends RepositoryFactorySupport {

    private final DSLContext dslContext;


    /**
     * Creates a new {@link JooqRepositoryFactory}.
     *
     * @param dslContext must not be {@literal null}
     */
    public JooqRepositoryFactory(DSLContext dslContext) {
        Assert.notNull(dslContext);
        this.dslContext = dslContext;

    }

    /* 
     * (non-Javadoc)
     * @see org.springframework.data.repository.core.support.RepositoryFactorySupport#setBeanClassLoader(java.lang.ClassLoader)
     */
    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        super.setBeanClassLoader(classLoader);
    }

    @Override
    public <E, ID extends Serializable> EntityInformation<E, ID> getEntityInformation(Class<E> domainClass) {
        return (EntityInformation<E, ID>) JooqEntityInformationSupport.getEntityInformation(domainClass, dslContext);
    }


    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.core.support.RepositoryFactorySupport#getTargetRepository(org.springframework.data.repository.core.RepositoryMetadata)
     */
    @Override
    protected Object getTargetRepository(RepositoryInformation information) {

        SimpleJooqRepository<?, ?, ?, ?> repository = getTargetRepository(information, dslContext);
        //repository.setRepositoryMethodMetadata(crudMethodMetadataPostProcessor.getCrudMethodMetadata());

        return repository;
    }

    /**
     * Callback to create a {@link JooqRepository} instance with the given {@link EntityManager}
     *
     * @param <T>
     * @param <ID>
     * @param dslContext
     * @return
     * @see #getTargetRepository(RepositoryInformation, DSLContext)
     */
    protected <T, ID extends Serializable> SimpleJooqRepository<?, ?, ?, ?> getTargetRepository(
            RepositoryInformation information, DSLContext dslContext) {
        JooqEntityInformation<?, ?, ?, Serializable> entityInformation = (JooqEntityInformation<?, ?, ?, Serializable>) getEntityInformation(information.getDomainType());

        return getTargetRepositoryViaReflection(information, entityInformation, dslContext);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.data.repository.support.RepositoryFactorySupport#
     * getRepositoryBaseClass()
     */
    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return SimpleJooqRepository.class;
    }


}
