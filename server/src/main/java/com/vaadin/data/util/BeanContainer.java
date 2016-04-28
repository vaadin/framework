/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.data.util;

import java.util.Collection;

/**
 * An in-memory container for JavaBeans.
 * 
 * <p>
 * The properties of the container are determined automatically by introspecting
 * the used JavaBean class. Only beans of the same type can be added to the
 * container.
 * </p>
 * 
 * <p>
 * In BeanContainer (unlike {@link BeanItemContainer}), the item IDs do not have
 * to be the beans themselves. The container can be used either with explicit
 * item IDs or the item IDs can be generated when adding beans.
 * </p>
 * 
 * <p>
 * To use explicit item IDs, use the methods {@link #addItem(Object, Object)},
 * {@link #addItemAfter(Object, Object, Object)} and
 * {@link #addItemAt(int, Object, Object)}.
 * </p>
 * 
 * <p>
 * If a bean id resolver is set using
 * {@link #setBeanIdResolver(com.vaadin.data.util.AbstractBeanContainer.BeanIdResolver)}
 * or {@link #setBeanIdProperty(Object)}, the methods {@link #addBean(Object)},
 * {@link #addBeanAfter(Object, Object)}, {@link #addBeanAt(int, Object)} and
 * {@link #addAll(java.util.Collection)} can be used to add items to the
 * container. If one of these methods is called, the resolver is used to
 * generate an identifier for the item (must not return null).
 * </p>
 * 
 * <p>
 * Note that explicit item identifiers can also be used when a resolver has been
 * set by calling the addItem*() methods - the resolver is only used when adding
 * beans using the addBean*() or {@link #addAll(Collection)} methods.
 * </p>
 * 
 * <p>
 * It is not possible to add additional properties to the container.
 * </p>
 * 
 * @param <IDTYPE>
 *            The type of the item identifier
 * @param <BEANTYPE>
 *            The type of the Bean
 * 
 * @see AbstractBeanContainer
 * @see BeanItemContainer
 * 
 * @since 6.5
 */
public class BeanContainer<IDTYPE, BEANTYPE> extends
        AbstractBeanContainer<IDTYPE, BEANTYPE> {

    public BeanContainer(Class<? super BEANTYPE> type) {
        super(type);
    }

    /**
     * Adds the bean to the Container.
     * 
     * @see com.vaadin.data.Container#addItem(Object)
     */
    @Override
    public BeanItem<BEANTYPE> addItem(IDTYPE itemId, BEANTYPE bean) {
        if (itemId != null && bean != null) {
            return super.addItem(itemId, bean);
        } else {
            return null;
        }
    }

    /**
     * Adds the bean after the given item id.
     * 
     * @see com.vaadin.data.Container.Ordered#addItemAfter(Object, Object)
     */
    @Override
    public BeanItem<BEANTYPE> addItemAfter(IDTYPE previousItemId,
            IDTYPE newItemId, BEANTYPE bean) {
        if (newItemId != null && bean != null) {
            return super.addItemAfter(previousItemId, newItemId, bean);
        } else {
            return null;
        }
    }

    /**
     * Adds a new bean at the given index.
     * 
     * The bean is used both as the item contents and as the item identifier.
     * 
     * @param index
     *            Index at which the bean should be added.
     * @param newItemId
     *            The item id for the bean to add to the container.
     * @param bean
     *            The bean to add to the container.
     * 
     * @return Returns the new BeanItem or null if the operation fails.
     */
    @Override
    public BeanItem<BEANTYPE> addItemAt(int index, IDTYPE newItemId,
            BEANTYPE bean) {
        if (newItemId != null && bean != null) {
            return super.addItemAt(index, newItemId, bean);
        } else {
            return null;
        }
    }

    // automatic item id resolution

    /**
     * Sets the bean id resolver to use a property of the beans as the
     * identifier.
     * 
     * @param propertyId
     *            the identifier of the property to use to find item identifiers
     */
    public void setBeanIdProperty(Object propertyId) {
        setBeanIdResolver(createBeanPropertyResolver(propertyId));
    }

    @Override
    // overridden to make public
    public void setBeanIdResolver(
            BeanIdResolver<IDTYPE, BEANTYPE> beanIdResolver) {
        super.setBeanIdResolver(beanIdResolver);
    }

    @Override
    // overridden to make public
    public BeanItem<BEANTYPE> addBean(BEANTYPE bean)
            throws IllegalStateException, IllegalArgumentException {
        return super.addBean(bean);
    }

    @Override
    // overridden to make public
    public BeanItem<BEANTYPE> addBeanAfter(IDTYPE previousItemId, BEANTYPE bean)
            throws IllegalStateException, IllegalArgumentException {
        return super.addBeanAfter(previousItemId, bean);
    }

    @Override
    // overridden to make public
    public BeanItem<BEANTYPE> addBeanAt(int index, BEANTYPE bean)
            throws IllegalStateException, IllegalArgumentException {
        return super.addBeanAt(index, bean);
    }

    @Override
    // overridden to make public
    public void addAll(Collection<? extends BEANTYPE> collection)
            throws IllegalStateException {
        super.addAll(collection);
    }

}
