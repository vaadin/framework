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
 * BeanItemContainer uses the beans themselves as identifiers. The
 * {@link Object#hashCode()} of a bean is used when storing and looking up beans
 * so it must not change during the lifetime of the bean (it should not depend
 * on any part of the bean that can be modified). Typically this restricts the
 * implementation of {@link Object#equals(Object)} as well in order for it to
 * fulfill the contract between {@code equals()} and {@code hashCode()}.
 * </p>
 * 
 * <p>
 * To add items to the container, use the methods {@link #addBean(Object)},
 * {@link #addBeanAfter(Object, Object)} and {@link #addBeanAt(int, Object)}.
 * Also {@link #addItem(Object)}, {@link #addItemAfter(Object, Object)} and
 * {@link #addItemAt(int, Object)} can be used as synonyms for them.
 * </p>
 * 
 * <p>
 * It is not possible to add additional properties to the container.
 * </p>
 * 
 * @param <BEANTYPE>
 *            The type of the Bean
 * 
 * @since 5.4
 */
@SuppressWarnings("serial")
public class BeanItemContainer<BEANTYPE> extends
        AbstractBeanContainer<BEANTYPE, BEANTYPE> {

    /**
     * Bean identity resolver that returns the bean itself as its item
     * identifier.
     * 
     * This corresponds to the old behavior of {@link BeanItemContainer}, and
     * requires suitable (identity-based) equals() and hashCode() methods on the
     * beans.
     * 
     * @param <BT>
     * 
     * @since 6.5
     */
    private static class IdentityBeanIdResolver<BT> implements
            BeanIdResolver<BT, BT> {

        @Override
        public BT getIdForBean(BT bean) {
            return bean;
        }

    }

    /**
     * Constructs a {@code BeanItemContainer} for beans of the given type.
     * 
     * @param type
     *            the type of the beans that will be added to the container.
     * @throws IllegalArgumentException
     *             If {@code type} is null
     */
    public BeanItemContainer(Class<? super BEANTYPE> type)
            throws IllegalArgumentException {
        super(type);
        super.setBeanIdResolver(new IdentityBeanIdResolver<BEANTYPE>());
    }

    /**
     * Constructs a {@code BeanItemContainer} and adds the given beans to it.
     * The collection must not be empty.
     * {@link BeanItemContainer#BeanItemContainer(Class)} can be used for
     * creating an initially empty {@code BeanItemContainer}.
     * 
     * Note that when using this constructor, the actual class of the first item
     * in the collection is used to determine the bean properties supported by
     * the container instance, and only beans of that class or its subclasses
     * can be added to the collection. If this is problematic or empty
     * collections need to be supported, use {@link #BeanItemContainer(Class)}
     * and {@link #addAll(Collection)} instead.
     * 
     * @param collection
     *            a non empty {@link Collection} of beans.
     * @throws IllegalArgumentException
     *             If the collection is null or empty.
     * 
     * @deprecated As of 6.5, use {@link #BeanItemContainer(Class, Collection)}
     *             instead
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public BeanItemContainer(Collection<? extends BEANTYPE> collection)
            throws IllegalArgumentException {
        // must assume the class is BT
        // the class information is erased by the compiler
        this((Class<BEANTYPE>) getBeanClassForCollection(collection),
                collection);
    }

    /**
     * Internal helper method to support the deprecated {@link Collection}
     * container.
     * 
     * @param <BT>
     * @param collection
     * @return
     * @throws IllegalArgumentException
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    private static <BT> Class<? extends BT> getBeanClassForCollection(
            Collection<? extends BT> collection)
            throws IllegalArgumentException {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException(
                    "The collection passed to BeanItemContainer constructor must not be null or empty. Use the other BeanItemContainer constructor.");
        }
        return (Class<? extends BT>) collection.iterator().next().getClass();
    }

    /**
     * Constructs a {@code BeanItemContainer} and adds the given beans to it.
     * 
     * @param type
     *            the type of the beans that will be added to the container.
     * @param collection
     *            a {@link Collection} of beans (can be empty or null).
     * @throws IllegalArgumentException
     *             If {@code type} is null
     */
    public BeanItemContainer(Class<? super BEANTYPE> type,
            Collection<? extends BEANTYPE> collection)
            throws IllegalArgumentException {
        super(type);
        super.setBeanIdResolver(new IdentityBeanIdResolver<BEANTYPE>());

        if (collection != null) {
            addAll(collection);
        }
    }

    /**
     * Adds all the beans from a {@link Collection} in one go. More efficient
     * than adding them one by one.
     * 
     * @param collection
     *            The collection of beans to add. Must not be null.
     */
    @Override
    public void addAll(Collection<? extends BEANTYPE> collection) {
        super.addAll(collection);
    }

    /**
     * Adds the bean after the given bean.
     * 
     * The bean is used both as the item contents and as the item identifier.
     * 
     * @param previousItemId
     *            the bean (of type BT) after which to add newItemId
     * @param newItemId
     *            the bean (of type BT) to add (not null)
     * 
     * @see com.vaadin.data.Container.Ordered#addItemAfter(Object, Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public BeanItem<BEANTYPE> addItemAfter(Object previousItemId,
            Object newItemId) throws IllegalArgumentException {
        return super.addBeanAfter((BEANTYPE) previousItemId,
                (BEANTYPE) newItemId);
    }

    /**
     * Adds a new bean at the given index.
     * 
     * The bean is used both as the item contents and as the item identifier.
     * 
     * @param index
     *            Index at which the bean should be added.
     * @param newItemId
     *            The bean to add to the container.
     * @return Returns the new BeanItem or null if the operation fails.
     */
    @Override
    @SuppressWarnings("unchecked")
    public BeanItem<BEANTYPE> addItemAt(int index, Object newItemId)
            throws IllegalArgumentException {
        return super.addBeanAt(index, (BEANTYPE) newItemId);
    }

    /**
     * Adds the bean to the Container.
     * 
     * The bean is used both as the item contents and as the item identifier.
     * 
     * @see com.vaadin.data.Container#addItem(Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public BeanItem<BEANTYPE> addItem(Object itemId) {
        return super.addBean((BEANTYPE) itemId);
    }

    /**
     * Adds the bean to the Container.
     * 
     * The bean is used both as the item contents and as the item identifier.
     * 
     * @see com.vaadin.data.Container#addItem(Object)
     */
    @Override
    public BeanItem<BEANTYPE> addBean(BEANTYPE bean) {
        return addItem(bean);
    }

    /**
     * Unsupported in BeanItemContainer.
     */
    @Override
    protected void setBeanIdResolver(
            AbstractBeanContainer.BeanIdResolver<BEANTYPE, BEANTYPE> beanIdResolver)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "BeanItemContainer always uses an IdentityBeanIdResolver");
    }

}
